package models.orderService.products;

import core.helper.http.Http;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.portalBack.AccessGroup;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.subModels.Role;
import org.json.JSONObject;
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
        initProduct();
        if(roles == null) {
            AccessGroup accessGroup = AccessGroup.builder().projectName(projectId).build().createObject();
            roles = Collections.singletonList(new Role("edit", accessGroup.getPrefixName()));
        }
        if(dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @SneakyThrows
    @Override
    public JSONObject toJson() {
        AccessGroup accessGroup = AccessGroup.builder().projectName(projectId).build().createObject();
        List<ResourcePool> resourcePoolList = OrderServiceSteps.getResourcesPoolList("container", projectId);
        ResourcePool resourcePool = resourcePoolList.stream()
//                .filter(r -> r.getLabel().equals(resourcePoolLabel))
//                .findFirst().orElseThrow(() -> new NoSuchFieldException(String.format("Кластер '%s' не найден", resourcePoolLabel)));
                .findFirst().orElseThrow(() -> new NoSuchFieldException("Список кластеров пуст"));
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.attrs.resource_pool", new JSONObject(resourcePool.toString()))
                .set("$.order.attrs.roles[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.net_segment", segment)
                .set("$.order.attrs.user_mark", "openshift" + new Random().nextInt())
                .set("$.order.label", getLabel())
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
        OrderServiceSteps.executeAction("update_openshift_project", this, new JSONObject(data), this.getProjectId());
        save();
        Assertions.assertEquals(2, OrderServiceSteps.getProductsField(this, "data.find{it.type=='project'}.data.config.quota.memory"), "Память не изменилась");
        Assertions.assertEquals("view", OrderServiceSteps.getProductsField(this, "data.find{it.type=='project'}.data.config.roles[0].role"), "Роль не изменилась");
        if (shdQuoteValue.equals("1")){
            Assertions.assertEquals(1, OrderServiceSteps.getProductsField(this, "data.find{it.type=='project'}.data.config.quota.storage.sc-nfs-netapp-q"), "СХД не изменился на 1");
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
                .get(String.format("/v1/products/resource_pools?category=container&project_name=%s&quota[storage][sc-nfs-netapp-q]=1&resource_type=cluster:openshift",
                        getProjectId()))
                .assertStatus(200)
                .toJson()
                .getJSONArray("list")
                .toString();
        return jsonArray.contains(resourcePoolLabel);
    }
}
