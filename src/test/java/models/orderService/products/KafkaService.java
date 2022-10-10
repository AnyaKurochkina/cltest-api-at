package models.orderService.products;

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
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
import models.subModels.Flavor;
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
    @ToString.Include
    String segment;
    String dataCentre;
    String topicName;
    String domain;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/kafka_service.json";
        productName = "Kafka Topic как услуга";
        initProduct();
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.topic_name", "1418_" + new Random().nextInt())
                .set("$.order.attrs.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
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

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("taas_delete_topic");
    }

}
