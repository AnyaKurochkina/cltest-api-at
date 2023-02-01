package models.cloud.orderService.products;

import com.google.gson.reflect.TypeToken;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.orderService.ResourcePool;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.orderService.interfaces.ProductStatus;
import models.cloud.subModels.Flavor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.CollectionUtils;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static core.helper.Configure.OrderServiceURL;
import static core.utils.AssertUtils.assertEqualsList;

@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Vault extends IProduct {
    private static final String POLICIES_PATH = "data[0].data.config.acls.find{it.name == '%s'}.policies";
    private static final String ACL_PATH = "data[0].data.config.acls.any{it.name == '%s'}";

    @Override
    public Entity init() {
        Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            setProjectId(project.getId());
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    public void addRule(String group, String ... policies){
        JSONObject body = new JSONObject().put("name", new JSONArray().put(group)).put("policies", new JSONArray(policies));
        OrderServiceSteps.executeAction("vault_add_acl", this, body);
        @SuppressWarnings(value = "unchecked")
        List<String> list = OrderServiceSteps.getProductsField(this, String.format(POLICIES_PATH, group), List.class);
        assertEqualsList(list, Arrays.asList(policies));
    }

    public void changeRule(String group, String ... policies){
        JSONObject body = new JSONObject().put("name", group).put("policies", new JSONArray(policies));
        OrderServiceSteps.executeAction("vault_set_acl", this, body);
        @SuppressWarnings(value = "unchecked")
        List<String> list = OrderServiceSteps.getProductsField(this, String.format(POLICIES_PATH, group), List.class);
        assertEqualsList(list, Arrays.asList(policies));
    }

    public void deleteRule(String group){
        OrderServiceSteps.executeAction("vault_delete_acl", this, new JSONObject().put("name", group));
        Assertions.assertFalse(OrderServiceSteps.getProductsField(this, String.format(ACL_PATH, group), Boolean.class));
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        orderId = new Http(OrderServiceURL)
                .setProjectId(projectId, Role.ORDER_SERVICE_ADMIN)
                .post("/v1/projects/{}/orders/vault_service", projectId)
                .assertStatus(201)
                .jsonPath()
                .get("[0].id");
        OrderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("vault_project_delete");
    }

    @Override
    public String toString() {
        return "Vault(" + env + ')';
    }
}
