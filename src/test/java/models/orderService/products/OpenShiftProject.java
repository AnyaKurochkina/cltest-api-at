package models.orderService.products;

import core.helper.Http;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Role;
import org.json.JSONObject;
import ru.testit.services.StepAspect;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static core.helper.Configure.OrderServiceURL;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class OpenShiftProject extends IProduct {
    @ToString.Include
    public String resourcePoolLabel;
    @Singular
    public List<Role> roles;
    @ToString.Include
    String segment;
    String dataCentre;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/openshift_project.json";
        productName = "OpenShift project";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if(projectId == null) {
            projectId = project.getId();
        }
        if(productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
        if(roles == null) {
            AccessGroup accessGroup = AccessGroup.builder().projectName(projectId).build().createObject();
            roles = Collections.singletonList(new Role("edit", accessGroup.getName()));
        }
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        StepAspect.step("Заказ продукта", "desc", () -> {
            JsonPath array = new Http(OrderServiceURL)
                    .setProjectId(projectId)
                    .body(toJson())
                    .post("projects/" + projectId + "/orders")
                    .assertStatus(201)
                    .jsonPath();
            orderId = array.get("[0].id");
            orderServiceSteps.checkOrderStatus("success", this);
            setStatus(ProductStatus.CREATED);
            compareCostOrderAndPrice();
        });
    }

    @SneakyThrows
    @Override
    public JSONObject toJson() {
        AccessGroup accessGroup = AccessGroup.builder().projectName(projectId).build().createObject();
        List<ResourcePool> resourcePoolList = orderServiceSteps.getResourcesPoolList("container", projectId);
        ResourcePool resourcePool = resourcePoolList.stream().
                filter(r -> r.getLabel().equals(resourcePoolLabel)).findFirst().orElseThrow(NoSuchFieldException::new);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.attrs.resource_pool", new JSONObject(resourcePool.toString()))
                .set("$.order.attrs.roles[0].groups[0]", accessGroup.getName())
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.net_segment", segment)
                .set("$.order.attrs.user_mark", "openshift" + new Random().nextInt())
                .build();
    }

    //Изменить проект
    public void changeProject() {
        String shdQuoteValue;
        shdQuoteValue = hasShdQuote() ? "1" : "0";
        String data = String.format("{\"quota\":{\"cpu\":1,\"memory\":2,\"storage\":{\"sc-nfs-netapp-q\": %s}},\"roles\":[{\"role\":\"view\",\"groups\":[\"%s\"]}]}",
                shdQuoteValue,
                roles.get(0).getGroupId());
        roles.get(0).setName("view");
        orderServiceSteps.executeAction("update_openshift_project", this, new JSONObject(data));
        save();
        Assertions.assertEquals(2, orderServiceSteps.getProductsField(this, "data.find{it.type=='project'}.config.quota.memory"), "Память не изменилась");
        Assertions.assertEquals("view", orderServiceSteps.getProductsField(this, "data.find{it.type=='project'}.config.roles[0].role"), "Роль не изменилась");
        if (shdQuoteValue.equals("1")){
            Assertions.assertEquals(1, orderServiceSteps.getProductsField(this, "data.find{it.type=='project'}.config.quota.storage.sc-nfs-netapp-q"), "СХД не изменился на 1");
        }
    }

    @Override
    @Step("Удаление продукта")
    protected void delete() {
        delete("delete_openshift_project");
    }

    //Проверка на наличие СХД у продукта
    private boolean hasShdQuote() {
        String jsonArray = new Http(OrderServiceURL)
                .setProjectId(getProjectId())
                .get(String.format("products/resource_pools?category=container&project_name=%s&quota[storage][sc-nfs-netapp-q]=1",
                        getProjectId()))
                .assertStatus(200)
                .toJson()
                .getJSONArray("list")
                .toString();
        return jsonArray.contains(resourcePoolLabel);
    }

}
