package models.orderService.products;

import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
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
public class Moon extends IProduct {
    @ToString.Include
    public String resourcePoolLabel;
    @ToString.Include
    String segment;
    String dataCentre;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/moon.json";
        productName = "Moon";
        initProduct();
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
        List<ResourcePool> resourcePoolList = OrderServiceSteps.getResourcesPoolList("container", projectId);
        ResourcePool resourcePool = resourcePoolList.stream().
                filter(r -> r.getLabel().equals(resourcePoolLabel)).findFirst().orElseThrow(NoSuchFieldException::new);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.attrs.resource_pool", new JSONObject(resourcePool.toString()))
                .set("$.order.product_id", productId)
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.net_segment", segment)
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
