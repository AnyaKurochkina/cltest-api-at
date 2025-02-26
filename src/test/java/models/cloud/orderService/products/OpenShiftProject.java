package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.orderService.ResourcePool;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Role;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static core.helper.Configure.orderServiceURL;

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
    String dataCentre;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/openshift_project.json";
        productName = "OpenShift project (Ключ Астром)";
        initProduct();
        if (roles == null) {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(getProjectId(), "", "compute");
            roles = Collections.singletonList(new Role("edit", accessGroup));
        }
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if(dataCentre == null)
            setDataCentre(OrderServiceSteps.getDataCentre(this));
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
        String accessGroup = roles.get(0).getGroupId();
        List<ResourcePool> resourcePoolList = OrderServiceSteps.getResourcesPoolList("container", projectId, "openshift_project");
        ResourcePool resourcePool = resourcePoolList.stream()
//                .filter(r -> r.getLabel().equals(resourcePoolLabel))
//                .findFirst().orElseThrow(() -> new NoSuchFieldException(String.format("Кластер '%s' не найден", resourcePoolLabel)));
                .findFirst().orElseThrow(() -> new NoSuchFieldException("Список кластеров пуст"));
        resourcePoolLabel = resourcePool.getLabel();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.resource_pool", new JSONObject(resourcePool.toString()))
                .set("$.order.attrs.roles[0].groups[0]", accessGroup)
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.net_segment", getSegment())
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("update_openshift_project").product(this).data(new JSONObject(data)).build());
        save();
        Assertions.assertEquals(2, OrderServiceSteps.getProductsField(this, "data.find{it.type=='project'}.data.config.quota.memory"), "Память не изменилась");
        Assertions.assertEquals("view", OrderServiceSteps.getProductsField(this, "data.find{it.type=='project'}.data.config.roles[0].role"), "Роль не изменилась");
        if (shdQuoteValue.equals("1")) {
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
        String jsonArray = new Http(orderServiceURL)
                .setProjectId(getProjectId(), core.enums.Role.ORDER_SERVICE_ADMIN)
                .get(String.format("/v1/products/resource_pools?category=container&project_name=%s&quota[storage][sc-nfs-netapp-q]=1&resource_type=cluster:openshift",
                        getProjectId()))
                .assertStatus(200)
                .toJson()
                .getJSONArray("list")
                .toString();
        return jsonArray.contains(resourcePoolLabel);
    }
}
