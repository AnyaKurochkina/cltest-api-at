package models.orderService.products;

import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.subModels.Role;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Log4j2
@SuperBuilder
@NoArgsConstructor
@Data
public class OpenShiftProject extends IProduct {
    public String resourcePoolLabel;
    public String domain;
    @Builder.Default
    public List<Role> roles = new ArrayList<>();
    @Builder.Default
    public String status = "NOT_CREATED";
    @Builder.Default
    public boolean isDeleted = false;

    @Override
    public void order() {
        productName = "OpenShift project";
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        ResourcePool resourcePool = cacheService.entity(ResourcePool.class)
                .withField("label", resourcePoolLabel)
                .getEntity();
        projectId = project.id;
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = jsonHelper.getJsonTemplate("/orders/openshift_project.json")
                .set("$.order.attrs.resource_pool_id", resourcePool.id)
                .set("$.order.attrs.roles[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.user_mark", "openshift"+ new Random().nextInt())
                .send(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        roles.add(new Role ("edit", accessGroup.name));
        orderServiceSteps.checkOrderStatus("success", this);
        status = "CREATED";
        cacheService.saveEntity(this);
    }

    public void changeProject() {
        String data = String.format("{\"quota\":{\"cpu\":1,\"memory\":2},\"roles\":[{\"role\":\"view\",\"groups\":[\"%s\"]}]}", roles.get(0).getGroupId());
        roles.get(0).setName("view");
        String actionId = orderServiceSteps.executeAction("Изменить проект", data, this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        cacheService.saveEntity(this);
        Assert.assertEquals(orderServiceSteps.getFiledProduct(this, "data.find{it.type=='project'}.config.quota.memory"), 2);
        Assert.assertEquals(orderServiceSteps.getFiledProduct(this, "data.find{it.type=='project'}.config.roles[0].role"), "view");
    }

    public void deleteProject() {
        String actionId = orderServiceSteps.executeAction("Удалить проект", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

}
