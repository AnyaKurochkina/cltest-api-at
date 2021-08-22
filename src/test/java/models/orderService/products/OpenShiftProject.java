package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Role;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;
import java.util.*;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class OpenShiftProject extends IProduct {
    public String resourcePoolLabel;
    public List<Role> roles = new ArrayList<>();

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
        setStatus(ProductStatus.CREATED);
        cacheService.saveEntity(this);
    }

    public OpenShiftProject() {
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
    @Action("Изменить проект")
    public void changeProject(String action) {
        String data = String.format("{\"quota\":{\"cpu\":1,\"memory\":2,\"storage\":{\"sc-nfs-netapp-q\": 0}},\"roles\":[{\"role\":\"view\",\"groups\":[\"%s\"]}]}", roles.get(0).getGroupId());
        roles.get(0).setName("view");
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(data));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        cacheService.saveEntity(this);
        Assert.assertEquals(orderServiceSteps.getFiledProduct(this, "data.find{it.type=='project'}.config.quota.memory"), 2);
        Assert.assertEquals(orderServiceSteps.getFiledProduct(this, "data.find{it.type=='project'}.config.roles[0].role"), "view");
    }

    @Override
    @Action("Удалить проект")
    public void delete(String action) {
        super.delete(action);
    }


}
