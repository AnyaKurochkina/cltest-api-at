package models.cloud.orderService.products;

import core.enums.KafkaRoles;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.util.Random;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class KafkaService extends IProduct {
    public static final String KAFKA_CLUSTER_ACL_ROLE = "data[0].data.config.acls.any{it.client_cn=='%s' & it.client_role=='%s'}";
    public static final String KAFKA_CLUSTER_ACL_GROUP = "data[0].data.config.group_acls.any{it.group_name=='%s'}";
    String topicName;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/kafka_service.json";
        productName = "Kafka Topic как услуга";
        initProduct();
        if(segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (availabilityZone == null)
            setAvailabilityZone(OrderServiceSteps.getAvailabilityZone(this));
        if(platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if(domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.topic_name", "1418_" + new Random().nextInt())
                .set("$.order.attrs.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .build();
    }

    public void createAclRole(String cert, KafkaRoles role) {
        OrderServiceSteps.executeAction("taas_create_acls", this, new JSONObject("{\"acls\":[{\"client_cn\":\"" + cert + "\",\"client_role\":\"" + role.getRole() + "\"}]}"), this.projectId);
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_ROLE, cert, role.getRole())), "ACL не создался");
    }

    public void deleteAclRole(String cert, KafkaRoles role) {
        JSONObject object = new JSONObject().put("client_cn", cert).put("client_role", role.getRole());
        OrderServiceSteps.executeAction("taas_delete_acls", this, new JSONObject().put("selected", new JSONArray().put(object.put("rawData", new JSONObject(object.toMap())))), this.projectId);
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_ROLE, cert, role.getRole())), "ACL не создался");
    }

    public void createAclGroup(String group) {
        OrderServiceSteps.executeAction("taas_create_group_acls", this, new JSONObject("{\"acls\":[{\"client_cn\":\"*\",\"group_name\":\"" + group + "\"}]}"), this.projectId);
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_GROUP, group)), "ACL не создался");
    }

    public void deleteAclGroup(String group) {
        JSONObject object = new JSONObject().put("client_cn", "*").put("group_name", group);
        OrderServiceSteps.executeAction("taas_delete_group_acls", this, new JSONObject().put("selected", new JSONArray().put(object.put("rawData", new JSONObject(object.toMap())))), this.projectId);
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_GROUP, group)), "ACL не создался");
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("taas_delete_topic");
    }

}
