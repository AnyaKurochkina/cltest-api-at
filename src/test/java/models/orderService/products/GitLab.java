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
public class GitLab extends IProduct {
    @ToString.Include
    String groupName;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/gitlab.json";
        productName = "Группа Gitlab";
        initProduct();
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
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.group_name", "vtb-" + new Random().nextInt())
                .set("$.order.label", getLabel())
                .build();
    }

    @Override
    @Step("Удаление продукта")
    protected void delete() {
        delete("delete_gitlab_group");
    }
}
