package api.cloud.secretService;

import api.cloud.secretService.models.AccessRule;
import api.cloud.secretService.models.AccessRuleResponse;
import api.cloud.secretService.models.SecretResponse;
import api.cloud.secretService.steps.SecretServiceAdminSteps;
import core.enums.Role;
import core.helper.http.AssertResponse;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Сервис секретов")
@Epic("Права доступа")
public class AccessRulesTest extends AbstractSecretTest {
    GlobalUser otherUser = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();

    @Test
    @TmsLink("")
    @DisplayName("Создание правила доступа для секрета")
    void postV1SecretsSecretIdAccessRules() {
        SecretResponse secretResponse = generateSecret();
        AccessRule rule = AccessRule.builder()
                .publicAccess(true)
                .mode("read_only")
                .build();
        AccessRuleResponse ruleResponse = SecretServiceAdminSteps.postV1SecretsSecretIdAccessRules(secretResponse.getId(), rule);
        Assertions.assertAll("Проверка созданного секрета",
                () -> Assertions.assertEquals("read_only", ruleResponse.getMode()),
                () -> Assertions.assertTrue(ruleResponse.getPublicAccess()),
                () -> Assertions.assertNotNull(ruleResponse.getCreatedAt()),
                () -> Assertions.assertNotNull(ruleResponse.getUpdatedAt()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Список правил доступа для секрета")
    void getV1SecretsSecretIdAccessRules() {
        SecretResponse secretResponse = generateSecret();
        AccessRule rule = AccessRule.builder()
                .email(otherUser.getEmail())
                .publicAccess(false)
                .mode("read_write")
                .build();
        AccessRuleResponse ruleResponse = SecretServiceAdminSteps.postV1SecretsSecretIdAccessRules(secretResponse.getId(), rule);
        AssertUtils.assertContainsList(SecretServiceAdminSteps.getV1SecretsSecretIdAccessRules(secretResponse.getId()).getList(), ruleResponse);
    }

    @Test
    @TmsLink("")
    @DisplayName("Просмотр правила доступа для секрета")
    void getV1SecretsSecretIdAccessRulesId() {
        SecretResponse secretResponse = generateSecret();
        AccessRule rule = AccessRule.builder()
                .email(otherUser.getEmail())
                .publicAccess(false)
                .mode("read_write")
                .build();
        AccessRuleResponse ruleResponse = SecretServiceAdminSteps.postV1SecretsSecretIdAccessRules(secretResponse.getId(), rule);
        Assertions.assertEquals(SecretServiceAdminSteps.getV1SecretsSecretIdAccessRulesId(secretResponse.getId(), ruleResponse.getId()), ruleResponse);
    }

    @Test
    @TmsLink("")
    @DisplayName("Обновление правила доступа для секрета")
    void patchV1SecretsSecretIdAccessRulesId() {
        SecretResponse secretResponse = generateSecret();
        AccessRule rule = AccessRule.builder()
                .email(otherUser.getEmail())
                .publicAccess(false)
                .mode("read_write")
                .build();
        AccessRuleResponse ruleResponse = SecretServiceAdminSteps.postV1SecretsSecretIdAccessRules(secretResponse.getId(), rule);
        rule.setMode("read_only");
        Waiting.sleep(1000);
        AccessRuleResponse updatedRuleResponse = SecretServiceAdminSteps.patchV1SecretsSecretIdAccessRulesId(secretResponse.getId(), ruleResponse.getId(), rule);
        Assertions.assertAll("Проверка обновленного секрета",
                () -> Assertions.assertEquals("read_only", updatedRuleResponse.getMode()),
                () -> Assertions.assertNotEquals(updatedRuleResponse.getCreatedAt(), updatedRuleResponse.getUpdatedAt()),
                () -> Assertions.assertEquals(-1, updatedRuleResponse.getCreatedAt().compareTo(updatedRuleResponse.getUpdatedAt())));
    }

    @Test
    @TmsLink("")
    @DisplayName("Удаление правила доступа для секрета")
    void deleteV1SecretsSecretIdAccessRulesId() {
        SecretResponse secretResponse = generateSecret();
        AccessRule rule = AccessRule.builder()
                .email(otherUser.getEmail())
                .publicAccess(false)
                .mode("read_only")
                .build();
        AccessRuleResponse ruleResponse = SecretServiceAdminSteps.postV1SecretsSecretIdAccessRules(secretResponse.getId(), rule);
        SecretServiceAdminSteps.deleteV1SecretsSecretIdAccessRulesId(secretResponse.getId(), ruleResponse.getId(), Role.SUPERADMIN);
        AssertUtils.assertNotContainsList(SecretServiceAdminSteps.getV1SecretsSecretIdAccessRules(secretResponse.getId()).getList(), ruleResponse);
    }

    @Test
    @TmsLink("")
    @DisplayName("Проверка правила доступа read_only = true")
    void checkReadOnlyAccessRule() {
        SecretResponse secretResponse = generateSecret();
        AccessRule rule = AccessRule.builder()
                .publicAccess(false)
                .email(otherUser.getEmail())
                .mode("read_only")
                .build();
        SecretServiceAdminSteps.postV1SecretsSecretIdAccessRules(secretResponse.getId(), rule);
        SecretServiceAdminSteps.getV1SecretsId(secretResponse.getId(), otherUser.getRole());
    }

    @Test
    @TmsLink("")
    @DisplayName("Проверка правила доступа read_only = false")
    void checkReadOnlyFalseAccessRule() {
        SecretResponse secretResponse = generateSecret();
        AssertResponse.run(() -> SecretServiceAdminSteps.getV1SecretsId(secretResponse.getId(), otherUser.getRole()))
                .responseContains("\"message\": \"Отсутствует доступ на чтение\"")
                .status(422);
    }

    @Test
    @TmsLink("")
    @DisplayName("Проверка правила доступа read_write = false")
    void checkReadWriteFalseAccessRule() {
        SecretResponse secretResponse = generateSecret();
        AccessRule rule = AccessRule.builder()
                .publicAccess(false)
                .email(otherUser.getEmail())
                .mode("read_only")
                .build();
        AccessRuleResponse accessRuleResponse = SecretServiceAdminSteps.postV1SecretsSecretIdAccessRules(secretResponse.getId(), rule);
        AssertResponse.run(() -> SecretServiceAdminSteps.deleteV1SecretsSecretIdAccessRulesId(secretResponse.getId(), accessRuleResponse.getId(), otherUser.getRole()))
                .responseContains("\"message\": \"Отсутствует доступ на изменение\"")
                .status(422);
    }

    @Test
    @TmsLink("")
    @DisplayName("Проверка правила доступа public_access = true")
    void checkPublicAccessRule() {
        SecretResponse secretResponse = generateSecret();
        AccessRule rule = AccessRule.builder()
                .publicAccess(true)
                .mode("read_write")
                .build();
        AccessRuleResponse accessRuleResponse = SecretServiceAdminSteps.postV1SecretsSecretIdAccessRules(secretResponse.getId(), rule);
        SecretServiceAdminSteps.deleteV1SecretsSecretIdAccessRulesId(secretResponse.getId(), accessRuleResponse.getId(), otherUser.getRole());
    }
}
