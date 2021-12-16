package models.orderService.products;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.util.List;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class HcpBucket extends IProduct {

//    private String projectName;
    private String bucketName;
    private String segment;
    private String platform;
    private int hardQuota;
    private String dataCentre;
    private String servicePlan;
    private boolean replication;
    /*
    delete_bucket_acls - Изменить ACL -no
    create_or_change_bucket_acls - настроить acl -no
    change_bucket_versioning - Измененить параметры версионирования {"item_id":"bd3128c3-fb9e-42d0-9f8f-e49f7091c614","order":{"data":{"bucket":{"versioning":{"prune":true,"enabled":true,"pruneDays":10}}}}}
    change_bucket_config - Измененить конфигурацию бакета {"item_id":"bd3128c3-fb9e-42d0-9f8f-e49f7091c614","order":{"data":{"bucket":{"hard_quota":20,"service_plan":"Cold","replication_enabled":false}}}}
    delete_bucket_acls - Удалить ACL -no
    remove_bucket_product - Удалить бакет
    * */

    @Override
    public Entity init() {
        jsonTemplate = "/orders/hcp_bucket.json";
        productName = "HCP bucket";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            projectId = project.getId();
        }
        if (productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.net_segment", segment)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.service_plan", servicePlan)
                .build();
    }

    @Override
    protected void create() {
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = new Http(Configure.OrderServiceURL)
                .setProjectId(projectId)
                .body(toJson())
                .post("projects/" + projectId + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        compareCostOrderAndPrice();
    }

    @Step("Измененить параметры версионирования")
    public void changeBucketVersioning(){
        orderServiceSteps.executeAction("change_bucket_versioning", this, new JSONObject("{\"bucket\":{\"versioning\":{\"prune\":true,\"enabled\":true,\"pruneDays\":10}}}"));
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, "data[0].config.bucket.versioning.prune"), "Очистка не активирована");
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, "data[0].config.bucket.versioning.enabled"), "Версионирование не активировалось");
    }

    @Step("Измененить конфигурацию бакета")
    public void changeBucketConfig(){
        orderServiceSteps.executeAction("change_bucket_config", this, new JSONObject("{\"bucket\":{\"hard_quota\":20,\"service_plan\":\"Cold\",\"replication_enabled\":false}}"));
        Integer hardQuota = (Integer) orderServiceSteps.getProductsField(this, "data[0].config.bucket.hard_quota");
        Assertions.assertEquals(hardQuota, 20, "Макс. объем не изменился! Макс. объем = " + hardQuota);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("remove_bucket_product");
    }
}
