package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import org.json.JSONObject;
import org.junit.Action;
import steps.orderService.OrderServiceSteps;

import static org.junit.Assert.assertTrue;

@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class Redis extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String domain;
    @ToString.Include
    String osVersion;

    @Override
    public void order() {
        JSONObject template = getJsonParametrizedTemplate();
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath jsonPath = new Http(OrderServiceSteps.URL)
                .setProjectId(projectId)
                .post("order-service/api/v1/projects/" + projectId + "/orders", template)
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        cacheService.saveEntity(this);
    }

    public Redis() {
        jsonTemplate = "/orders/redis.json";
        productName = "Redis";
    }

    @Override
    public JSONObject getJsonParametrizedTemplate() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(true)
                .getEntity();
        if(productId == null) {
            projectId = project.id;
            productId = orderServiceSteps.getProductId(this);
        }
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", ((ProjectEnvironment) cacheService.entity(ProjectEnvironment.class).withField("env", project.env).getEntity()).envType.contains("TEST"))
                .set("$.order.attrs.os_version", osVersion)
                .build();
    }

    //Удалить рекурсивно
    @Override
    @Action("delete_two_layer")
    public void delete(String action) {
        orderServiceSteps.executeAction(action, this, null);
        setStatus(ProductStatus.DELETED);
        cacheService.saveEntity(this);
    }

    //Расширить
    @Override
    @Action("expand_mount_point")
    public void expandMountPoint(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, EXPAND_MOUNT_SIZE);
        orderServiceSteps.executeAction("Расширить", this, new JSONObject("{\"size\": 10, \"mount\": \"/app/redis/data\"}"));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore<sizeAfter);
    }

    //Сбросить пароль
    @Action("reset_redis_password")
    public void resetPassword(String action) {
        String password = "yxjpjk7xvOImb1O9vZZiGUlsItkqLqtbB1VPZHzL6";
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{redis_password: \"%s\"}", password)));
    }

}
