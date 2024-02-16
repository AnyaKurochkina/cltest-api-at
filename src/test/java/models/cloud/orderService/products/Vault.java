package models.cloud.orderService.products;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import core.enums.Role;
import core.helper.StringUtils;
import core.helper.http.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.orderService.interfaces.ProductStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;
import steps.resourceManager.ResourceManagerSteps;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static core.helper.Configure.orderServiceURL;
import static core.utils.AssertUtils.assertEqualsList;

@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Vault extends IProduct {
    private static final String POLICIES_PATH = "data[0].data.config.acls.find{it.name == '%s'}.policies";
    private static final String ACL_PATH = "data[0].data.config.acls.any{it.name == '%s'}";
    private static final String APP_ROLES_PATH = "data[0].data.config.approles.find{it.approle_name == '%s'}";

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

    public void addRule(String group, String... policies) {
        JSONObject body = new JSONObject().put("name", new JSONArray().put(group)).put("policies", new JSONArray(policies));
        OrderServiceSteps.runAction(ActionParameters.builder().name("vault_add_acl").product(this).data(body).build());
        @SuppressWarnings(value = "unchecked")
        List<String> list = OrderServiceSteps.getProductsField(this, String.format(POLICIES_PATH, group), List.class);
        assertEqualsList(list, Arrays.asList(policies));
    }

    public void changeRule(String group, String... policies) {
        JSONObject body = new JSONObject().put("name", group).put("policies", new JSONArray(policies));
        OrderServiceSteps.runAction(ActionParameters.builder().name("vault_set_acl").product(this).data(body).build());
        @SuppressWarnings(value = "unchecked")
        List<String> list = OrderServiceSteps.getProductsField(this, String.format(POLICIES_PATH, group), List.class);
        assertEqualsList(list, Arrays.asList(policies));
    }

    public void deleteRule(String group) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("vault_delete_acl").product(this).data(new JSONObject().put("name", group)).build());
        Assertions.assertFalse(OrderServiceSteps.getProductsField(this, String.format(ACL_PATH, group), Boolean.class));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class AppRole {
        @Singular("aclCidr")
        private List<String> aclCidr;
        @Singular("policy")
        private List<String> policies;
        private String description;
        private String approleName;
        private int secretIdTtl;

        @EqualsAndHashCode.Exclude
        private String roleSecret, secretIdExpireDate;
    }

    @Step("Vault. Получение полного имени AppRole по name '{name}'")
    public String fullAppRoleName(String name) {
        JsonPath response = ResourceManagerSteps.getProjectById(getProjectId(), "information_system,environment_prefix").jsonPath();
        return StringUtils.format("{}{}-{}-{}", response.getString("data.environment_prefix.name"),
                response.getString("data.information_system.code"), getProjectId(), name);
    }

    @Step("Vault. Создать AppRole")
    public void addAppRole(AppRole role) {
        if (isExistAppRole(role))
            return;
        OrderServiceSteps.runAction(ActionParameters.builder().name("vault_create_approle")
                .product(this).data(serialize(role)).build());
        Assertions.assertTrue(isExistAppRole(role), "Не найден AppRole " + role);
    }

    @Step("Vault. Перевыпустить ключ")
    public void generateSecretIdAppRole(AppRole role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("vault_generate_secret_id_approle:").product(this)
                .data(new JSONObject().put("approle_name", role.getApproleName()).put("checkbox", true)).build());
    }

    @Step("Vault. Удалить AppRole")
    public void deleteAppRole(AppRole role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("vault_delete_approle")
                .product(this).data(new JSONObject().put("approle_name", role.getApproleName())).build());
        Assertions.assertFalse(isExistAppRole(role), "Найден AppRole " + role);
    }

    @Step("Vault. Изменить AppRole")
    public void updateAppRole(AppRole role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("vault_update_approle")
                .product(this).data(serialize(role)).build());
        Assertions.assertTrue(isExistAppRole(role), "Не найден AppRole " + role);
    }

    @Step("Vault. Проверка AppRole {role} на существование")
    public boolean isExistAppRole(AppRole role) {
        return Objects.nonNull(OrderServiceSteps.getObjectClass(this, String.format(APP_ROLES_PATH, role.getApproleName()), AppRole.class));
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        orderId = new Http(orderServiceURL)
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
