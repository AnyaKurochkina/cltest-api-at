package models.orderService.products;

import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Role;
import org.json.JSONObject;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class OpenShiftProject extends IProduct {
    @ToString.Include
    public String resourcePoolLabel;
    public List<Role> roles;
    public static final String CHANGE_PROJECT = "Изменить проект";

    public OpenShiftProject() {
        jsonTemplate = "/orders/openshift_project.json";
        productName = "OpenShift project";
    }

    @Override
    @Step("Заказ продукта")
    public void create() {
        JSONObject template = getJsonParametrizedTemplate();
        AccessGroup accessGroup = AccessGroup.builder().projectName(projectId).build().createObject();
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = new Http(OrderServiceSteps.URL)
                .setProjectId(projectId)
                .post("order-service/api/v1/projects/" + projectId + "/orders", template)
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        roles = new ArrayList<>();
        roles.add(new Role("edit", accessGroup.name));
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        save();
    }

    @SneakyThrows
    @Override
    public JSONObject getJsonParametrizedTemplate() {
        Project project = Project.builder().env(env).isForOrders(true).build().createObject();
        if (productId == null) {
            projectId = project.id;
            productId = orderServiceSteps.getProductId(this);
        }
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        List<ResourcePool> resourcePoolList = orderServiceSteps.getResourcesPoolList("container", project.id);
        ResourcePool resourcePool = resourcePoolList.stream().
                filter(r -> r.getLabel().equals(resourcePoolLabel)).findFirst().orElseThrow(NoSuchFieldException::new);
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.attrs.resource_pool", new JSONObject(resourcePool.toString()))
                .set("$.order.attrs.roles[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.user_mark", "openshift" + new Random().nextInt())
                .build();
    }

    public void changeProject(String action) {
        String data = String.format("{\"quota\":{\"cpu\":1,\"memory\":2,\"storage\":{\"sc-nfs-netapp-q\": 0}},\"roles\":[{\"role\":\"view\",\"groups\":[\"%s\"]}]}", roles.get(0).getGroupId());
        roles.get(0).setName("view");
        orderServiceSteps.executeAction(action, this, new JSONObject(data));
        save();
        Assert.assertEquals("Память не изменилась", 2, orderServiceSteps.getFiledProduct(this, "data.find{it.type=='project'}.config.quota.memory"));
        Assert.assertEquals("Роль не изменилась", "view", orderServiceSteps.getFiledProduct(this, "data.find{it.type=='project'}.config.roles[0].role"));
    }

    @Override
    @Step("Удаление продукта")
    protected void delete() {
        super.delete("Удалить проект");
    }
}
