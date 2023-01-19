package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.orderService.ResourcePool;
import models.cloud.orderService.interfaces.IProduct;
import org.json.JSONObject;
import steps.orderService.OrderServiceSteps;

import java.util.List;
import java.util.Random;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Moon extends IProduct {
    @ToString.Include
    public String resourcePoolLabel;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/moon.json";
        productName = "Moon";
        initProduct();
        if(segment == null)
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
        List<ResourcePool> resourcePoolList = OrderServiceSteps.getResourcesPoolList("container", projectId, "openshift_project_moon");
        ResourcePool resourcePool = resourcePoolList.stream()
//                .filter(r -> r.getLabel().equals(resourcePoolLabel))
//                .findFirst().orElseThrow(() -> new NoSuchFieldException(String.format("Кластер '%s' не найден", resourcePoolLabel)));
                .findFirst().orElseThrow(() -> new NoSuchFieldException("Список кластеров пуст"));
        resourcePoolLabel = resourcePool.getLabel();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.attrs.resource_pool", new JSONObject(resourcePool.toString()))
                .set("$.order.product_id", productId)
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.net_segment", getSegment())
                .set("$.order.attrs.user_mark", "moon" + new Random().nextInt())
                .set("$.order.label", getLabel())
                .build();
    }


    @Override
    @Step("Удаление продукта")
    protected void delete() {
        delete("delete_openshift_project_with_app");
    }

}
