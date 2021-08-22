package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import static org.junit.Assert.assertTrue;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class Windows extends IProduct {
    String segment;
    String dataCentre;
    String platform;
    String osVersion;
    public String domain;
    private static String ADD_DISK = "data.find{it.type=='vm'}.config.extra_disks.any{it.path=='%s'}";

    @Override
    public void order() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        projectId = project.id;
        productId = orderServiceSteps.getProductId(this);
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = new Http(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders", getJsonParametrizedTemplate())
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        cacheService.saveEntity(this);
    }

    public Windows() {
        jsonTemplate = "/orders/windows_server.json";
        productName = "Windows server";
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
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", env.toUpperCase().contains("TEST"))
                .build();
    }

    @Action("Добавить диск")
    private void addDisk(String action) {
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject("{path: \"I\", size: 10, file_system: \"ntfs\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(ADD_DISK, "I")));
    }

}
