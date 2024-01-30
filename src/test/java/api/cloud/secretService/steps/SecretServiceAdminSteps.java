package api.cloud.secretService.steps;

import api.cloud.secretService.models.*;
import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Step;
import models.AbstractEntity;
import org.json.JSONObject;

import static models.Entity.serialize;
import static tests.routes.SecretServiceAdminAPI.*;

public class SecretServiceAdminSteps {

    @Step("Создание секрета")
    public static SecretResponse postV1Secrets(Secret secret) {
        return Http.builder().setRole(Role.SUPERADMIN).body(serialize(secret)).api(postV1Secrets).extractAs(SecretResponse.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Удаление секрета {secretId}")
    public static void deleteV1SecretsId(String secretId) {
        Http.builder().setRole(Role.SUPERADMIN).api(deleteV1SecretsId, secretId);
    }

    @Step("Список хранилищ секретов")
    public static EnginePage getV1Engines(QueryBuilder query) {
        return Http.builder().setRole(Role.SUPERADMIN).api(getV1Engines, query).extractAllPages(EnginePage.class);
    }

    @Step("Добавление данных для секрета {secretId}")
    public static void postV1SecretsSecretIdData(String secretId, JSONObject data) {
        Http.builder().setRole(Role.SUPERADMIN).body(new JSONObject().put("data", data)).api(postV1SecretsSecretIdData, secretId);
    }

    @Step("Удаление данных для секрета {secretId}")
    public static void deleteV1SecretsSecretIdData(String secretId, Object[] data) {
        Http.builder().setRole(Role.SUPERADMIN).api(deleteV1SecretsSecretIdData, secretId, new QueryBuilder().add("data[]", data));
    }

    @Step("Получение данных для секрета {secretId}")
    public static JSONObject getV1SecretsSecretIdData(String secretId) {
        return new JSONObject(Http.builder().setRole(Role.SUPERADMIN).api(getV1SecretsSecretIdData, secretId).toString());
    }

    @Step("Обновление данных для секрета {secretId}")
    public static String patchV1SecretsSecretIdData(String secretId, JSONObject data) {
        return Http.builder().setRole(Role.SUPERADMIN).body(new JSONObject().put("data", data)).api(patchV1SecretsSecretIdData, secretId).toString();
    }

    @Step("Создание правила доступа для секрета {secretId}")
    public static AccessRuleResponse postV1SecretsSecretIdAccessRules(String secretId, AccessRule accessRule) {
        return Http.builder().setRole(Role.SUPERADMIN).body(serialize(accessRule)).api(postV1SecretsSecretIdAccessRules, secretId).extractAs(AccessRuleResponse.class);
    }

    @Step("Просмотр правила доступа {ruleId} для секрета {secretId}")
    public static AccessRuleResponse getV1SecretsSecretIdAccessRulesId(String secretId, String ruleId) {
        return Http.builder().setRole(Role.SUPERADMIN).api(getV1SecretsSecretIdAccessRulesId, secretId, ruleId).extractAs(AccessRuleResponse.class);
    }

    @Step("Список правил доступа для секрета {secretId}")
    public static AccessRuleResponsePage getV1SecretsSecretIdAccessRules(String secretId) {
        return Http.builder().setRole(Role.SUPERADMIN).api(getV1SecretsSecretIdAccessRules, secretId).extractAllPages(AccessRuleResponsePage.class);
    }

    @Step("Обновление правила доступа {ruleId} для секрета {secretId}")
    public static AccessRuleResponse patchV1SecretsSecretIdAccessRulesId(String secretId, String ruleId, AccessRule accessRule) {
        return Http.builder().setRole(Role.SUPERADMIN).body(serialize(accessRule)).api(patchV1SecretsSecretIdAccessRulesId, secretId, ruleId)
                .extractAs(AccessRuleResponse.class);
    }

    @Step("Удаление правила доступа {ruleId} для секрета {secretId}")
    public static void deleteV1SecretsSecretIdAccessRulesId(String secretId, String ruleId, Role role) {
        Http.builder().setRole(role).api(deleteV1SecretsSecretIdAccessRulesId, secretId, ruleId);
    }

    @Step("Просмотр настройки видимости для секрета {secretId}")
    public static VisibilityResponse getV1SecretsSecretIdVisibilityConditions(String secretId) {
        return Http.builder().setRole(Role.SUPERADMIN).api(getV1SecretsSecretIdVisibilityConditions, secretId).extractAs(VisibilityResponse.class);
    }

    @Step("Обновление настройки видимости для секрета {secretId}")
    public static VisibilityResponse patchV1SecretsSecretIdVisibilityConditions(String secretId, Visibility visibility) {
        return Http.builder().setRole(Role.SUPERADMIN).body(serialize(visibility)).api(patchV1SecretsSecretIdVisibilityConditions, secretId).extractAs(VisibilityResponse.class);
    }

    @Step("Получение списка секретов")
    public static SecretResponsePage getV1Secrets(QueryBuilder query) {
        return Http.builder().setRole(Role.SUPERADMIN).api(getV1Secrets, query).extractAs(SecretResponsePage.class);
    }

    @Step("Просмотр секрета")
    public static SecretResponse getV1SecretsId(String secretId, Role role) {
        return Http.builder().setRole(role).api(getV1SecretsId, secretId).extractAs(SecretResponse.class);
    }

    @Step("Обновление тегов секрета")
    public static SecretResponse patchV1SecretsId(String secretId, Secret secret) {
        return Http.builder().setRole(Role.SUPERADMIN).body(serialize(secret)).api(patchV1SecretsId, secretId).extractAs(SecretResponse.class);
    }

    @Step("Получить список пользователей")
    public static UserPage getV1Users() {
        return Http.builder().setRole(Role.SUPERADMIN).api(getV1Users).extractAs(UserPage.class);
    }

    @Step("Просмотр пользователя")
    public static User getV1UsersId(String userId) {
        return Http.builder().setRole(Role.SUPERADMIN).api(getV1UsersId, userId).extractAs(User.class);
    }
}
