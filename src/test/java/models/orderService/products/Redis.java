package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import org.json.JSONObject;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import static org.junit.Assert.assertTrue;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class Redis extends IProduct {
    String segment;
    String dataCentre;
    String platform;
    String domain;
    String status = "NOT_CREATED";
    boolean isDeleted = false;

    @Override
    public void order() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        projectId = project.id;
        productId = orderServiceSteps.getProductId(this);
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath jsonPath = new Http(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders", getJsonParametrizedTemplate())
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        status = "CREATED";
        cacheService.saveEntity(this);
    }

    @Override
    public void init() {
        jsonTemplate = "/orders/redis.json";
        productName = "Redis";
    }

    @Override
    public JSONObject getJsonParametrizedTemplate() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
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
                .build();
    }

    @Override
    public void delete() {
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void expandMountPoint() {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction("Расширить", this, new JSONObject("{\"size\": 10, \"mount\": \"/app/redis/data\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore<sizeAfter);
    }

    public void resetPassword() {
        String password = "yxjpjk7xvOImb1O9vZZiGUlsItkqLqtbB1VPZHzL6";
        String actionId = orderServiceSteps.executeAction("Сбросить пароль", this, new JSONObject(String.format("{redis_password: \"%s\"}", password)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void runActionsBeforeOtherTests(){
        boolean x = true;
        try {
            expandMountPoint();
        } catch (Exception e) {
            x = false;
            e.printStackTrace();
        }
        try {
            resetPassword();
        } catch (Exception e) {
            x = false;
            e.printStackTrace();
        }
        try {
            restart();
        } catch (Exception e) {
            x = false;
            e.printStackTrace();
        }
        try {
            stopSoft();
        } catch (Exception e) {
            x = false;
            e.printStackTrace();
        }
        try {
            start();
        } catch (Exception e) {
            x = false;
            e.printStackTrace();
        }
        try {
            stopHard();
        } catch (Exception e) {
            x = false;
            e.printStackTrace();
        }
        Assert.assertTrue(x);
    }
}
