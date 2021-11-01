package models.orderService.products;

import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Flavor;
import org.json.JSONObject;
import steps.orderService.OrderServiceSteps;
import java.util.List;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class Rhel extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    @ToString.Include
    String osVersion;
    String domain;
    Flavor flavor;

    @Override
    public void init() {
        jsonTemplate = "/orders/rhel.json";
        productName = "Rhel";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if(projectId == null) {
            projectId = project.getId();
        }
        if(productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
        if(domain == null)
            domain = orderServiceSteps.getDomainBySegment(this, segment);
        List<Flavor> flavorList = referencesStep.getProductFlavorsLinkedList(this);
        flavor = flavorList.get(0);
    }

    @Override
    @Step("Заказ продукта")
    public void create() {
        JsonPath array = new Http(OrderServiceSteps.URL)
                .setProjectId(projectId)
                .post("order-service/api/v1/projects/" + projectId + "/orders", toJson())
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
    }

    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(getProjectId()).build().createObject();
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getName())
                .set("$.order.project_name", getProjectId())
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST")).build();
    }

}
