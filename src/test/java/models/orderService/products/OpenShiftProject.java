package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.subModels.Role;
import org.json.JSONObject;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;
import java.util.*;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class OpenShiftProject extends IProduct {
    public String resourcePoolLabel;
    public String domain;
    public List<Role> roles = new ArrayList<>();
    public String status = "NOT_CREATED";
    public boolean isDeleted = false;

    @Override
    public void order() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        projectId = project.id;
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = new Http(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders", getJsonParametrizedTemplate())
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        roles.add(new Role ("edit", accessGroup.name));
        orderServiceSteps.checkOrderStatus("success", this);
        status = "CREATED";
        cacheService.saveEntity(this);
    }

    @Override
    public void init() {
        jsonTemplate = "/orders/openshift_project.json";
        productName = "OpenShift project";
    }

    @Override
    public JSONObject getJsonParametrizedTemplate() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        ResourcePool resourcePool = cacheService.entity(ResourcePool.class)
                .withField("label", resourcePoolLabel)
                .getEntity();

        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.attrs.resource_pool_id", resourcePool.id)
                .set("$.order.attrs.roles[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.user_mark", "openshift"+ new Random().nextInt())
                .build();
    }

    public void changeProject() {
        String data = String.format("{\"quota\":{\"cpu\":1,\"memory\":2},\"roles\":[{\"role\":\"view\",\"groups\":[\"%s\"]}]}", roles.get(0).getGroupId());
        roles.get(0).setName("view");
        String actionId = orderServiceSteps.executeAction("Изменить проект", this, new JSONObject(data));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        cacheService.saveEntity(this);
        Assert.assertEquals(orderServiceSteps.getFiledProduct(this, "data.find{it.type=='project'}.config.quota.memory"), 2);
        Assert.assertEquals(orderServiceSteps.getFiledProduct(this, "data.find{it.type=='project'}.config.roles[0].role"), "view");
    }

    @Override
    public void delete() {
        String actionId = orderServiceSteps.executeAction("Удалить проект", this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void runActionsBeforeOtherTests(){
        changeProject();
    }

}
