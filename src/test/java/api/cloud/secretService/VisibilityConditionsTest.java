
package api.cloud.secretService;

import api.cloud.secretService.models.SecretResponse;
import api.cloud.secretService.models.Visibility;
import api.cloud.secretService.models.VisibilityCondition;
import api.cloud.secretService.models.VisibilityResponse;
import api.cloud.secretService.steps.SecretServiceAdminSteps;
import api.cloud.secretService.steps.SecretServiceSteps;
import core.helper.http.AssertResponse;
import core.helper.http.QueryBuilder;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static java.time.LocalDate.now;


@Feature("Сервис секретов")
@Epic("Настройки видимости")
public class VisibilityConditionsTest extends AbstractSecretTest {
    final JSONObject data = new JSONObject().put("key", "value");

    @Test
    @TmsLink("")
    @DisplayName("Просмотр настройки видимости для секрета")
    void getV1SecretsSecretIdVisibilityConditions() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.getV1SecretsSecretIdVisibilityConditions(secretResponse.getId());
    }

    @Test
    @TmsLink("")
    @DisplayName("Обновление видимости для секрета")
    void setV1SecretsSecretIdVisibilityConditions() {
        SecretResponse secretResponse = generateSecret();
        Visibility vs = Visibility.builder().visibilityConditions(VisibilityCondition.builder().graph("g2").environment("e").build()).build();
        Waiting.sleep(1000);
        VisibilityResponse vsResponse = SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        VisibilityResponse vsResponseUpdated = SecretServiceAdminSteps.getV1SecretsSecretIdVisibilityConditions(secretResponse.getId());

        Assertions.assertAll("Проверка обновленного visible",
                () -> Assertions.assertEquals(vsResponse, vsResponseUpdated),
                () -> Assertions.assertEquals(vs.getVisibilityConditions(), vsResponse.getVisibilityConditions()),
                () -> Assertions.assertNotEquals(vsResponse.getCreatedAt(), vsResponse.getUpdatedAt()),
                () -> Assertions.assertEquals(-1, vsResponse.getCreatedAt().compareTo(vsResponse.getUpdatedAt())));
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. match_any")
    void visibilityConditionsMatchAny() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        Visibility vs = Visibility.builder()
                .unrestricted(false)
                .visibilityConditions(VisibilityCondition.builder().graph("g1").graph("g2").environment("e").build())
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        JSONObject response = SecretServiceSteps.getV1Secrets(new QueryBuilder()
                .add("segment", secretResponse.getEngine().getSegment())
                .add("engine_name", secretResponse.getEngine().getName())
                .add("uri", secretResponse.getUri())
                .add("access_tokens[graph]", "g2"));
        Assertions.assertTrue(data.similar(response));
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. unrestricted = false")
    void visibilityConditionsUnrestricted() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        Visibility vs = Visibility.builder()
                .unrestricted(false)
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        AssertResponse.run(() -> SecretServiceSteps.getV1Secrets(new QueryBuilder()
                        .add("segment", secretResponse.getEngine().getSegment())
                        .add("engine_name", secretResponse.getEngine().getName())
                        .add("uri", secretResponse.getUri())))
                .status(403)
                .responseContains("\"message\": \"Отсутствует доступ\"");
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. match_all")
    void visibilityConditionsMatchAll() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        Visibility vs = Visibility.builder()
                .evaluationMode("match_all")
                .unrestricted(false)
                .visibilityConditions(VisibilityCondition.builder().graph("g1").graph("g2").environment("e").build())
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        JSONObject response = SecretServiceSteps.getV1Secrets(new QueryBuilder()
                .add("segment", secretResponse.getEngine().getSegment())
                .add("engine_name", secretResponse.getEngine().getName())
                .add("uri", secretResponse.getUri())
                .add("access_tokens[graph]", "g1")
                .add("access_tokens[graph]", "g2")
                .add("access_tokens[environment]", "e"));
        Assertions.assertTrue(data.similar(response));
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. match_all. Переданы не все conditions")
    void visibilityConditionsMatchAllBad() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        Visibility vs = Visibility.builder()
                .unrestricted(false)
                .evaluationMode("match_all")
                .visibilityConditions(VisibilityCondition.builder().graph("g1").graph("g2").environment("e").build())
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        AssertResponse.run(() -> SecretServiceSteps.getV1Secrets(new QueryBuilder()
                        .add("segment", secretResponse.getEngine().getSegment())
                        .add("engine_name", secretResponse.getEngine().getName())
                        .add("uri", secretResponse.getUri())
                        .add("access_tokens[environment]", "e")))
                .status(403)
                .responseContains("\"message\": \"Отсутствует доступ\"");
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. cron_schedule. Allow")
    void visibilityConditionsCronScheduleAllow() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        LocalDate date = now();
        Visibility vs = Visibility.builder()
                .unrestricted(false)
                .cronSchedule("* * " + date.getDayOfMonth() + " * *")
                .evaluationMode("match_all")
                .visibilityConditions(VisibilityCondition.builder().graph("g").environment("e").build())
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        SecretServiceSteps.getV1Secrets(new QueryBuilder()
                .add("segment", secretResponse.getEngine().getSegment())
                .add("engine_name", secretResponse.getEngine().getName())
                .add("uri", secretResponse.getUri())
                .add("access_tokens[graph]", "g")
                .add("access_tokens[environment]", "e"));
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. cron_schedule. Deny")
    void visibilityConditionsCronScheduleDeny() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        LocalDate date = now();
        Visibility vs = Visibility.builder()
                .unrestricted(false)
                .cronSchedule("* * " + date.plusDays(1).getDayOfMonth() + " * *")
                .evaluationMode("match_all")
                .visibilityConditions(VisibilityCondition.builder().graph("g").environment("e").build())
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        AssertResponse.run(() -> SecretServiceSteps.getV1Secrets(new QueryBuilder()
                .add("segment", secretResponse.getEngine().getSegment())
                .add("engine_name", secretResponse.getEngine().getName())
                .add("uri", secretResponse.getUri())
                .add("access_tokens[graph]", "g")
                .add("access_tokens[environment]", "e")))
                .status(403)
                .responseContains("\"message\": \"Отсутствует доступ\"");
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. availableDt. Allow")
    void visibilityConditionsAvailableDtAllow() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        OffsetDateTime date = OffsetDateTime.now();
        Visibility vs = Visibility.builder()
                .unrestricted(false)
                .availableFromDt(date.minusDays(1))
                .availableTillDt(date.plusDays(1))
                .evaluationMode("match_all")
                .visibilityConditions(VisibilityCondition.builder().graph("g").environment("e").build())
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        SecretServiceSteps.getV1Secrets(new QueryBuilder()
                .add("segment", secretResponse.getEngine().getSegment())
                .add("engine_name", secretResponse.getEngine().getName())
                .add("uri", secretResponse.getUri())
                .add("access_tokens[graph]", "g")
                .add("access_tokens[environment]", "e"));
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. availableDt. Deny")
    void visibilityConditionsAvailableDtDeny() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        OffsetDateTime date = OffsetDateTime.now();
        Visibility vs = Visibility.builder()
                .unrestricted(false)
                .availableFromDt(date.plusDays(1))
                .availableTillDt(date.plusDays(2))
                .evaluationMode("match_all")
                .visibilityConditions(VisibilityCondition.builder().graph("g").environment("e").build())
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        AssertResponse.run(() -> SecretServiceSteps.getV1Secrets(new QueryBuilder()
                        .add("segment", secretResponse.getEngine().getSegment())
                        .add("engine_name", secretResponse.getEngine().getName())
                        .add("uri", secretResponse.getUri())
                        .add("access_tokens[graph]", "g")
                        .add("access_tokens[environment]", "e")))
                .status(403)
                .responseContains("\"message\": \"Отсутствует доступ\"");
    }

    @Test
    @TmsLink("")
    @DisplayName("Видимости для секрета. excludeVisibilityConditions")
    void excludeVisibilityConditions() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        Visibility vs = Visibility.builder()
                .unrestricted(false)
                .excludeVisibilityConditions(VisibilityCondition.builder().environment("e").build())
                .evaluationMode("match_any")
                .visibilityConditions(VisibilityCondition.builder().graph("g").build())
                .build();
        SecretServiceAdminSteps.patchV1SecretsSecretIdVisibilityConditions(secretResponse.getId(), vs);
        AssertResponse.run(() -> SecretServiceSteps.getV1Secrets(new QueryBuilder()
                        .add("segment", secretResponse.getEngine().getSegment())
                        .add("engine_name", secretResponse.getEngine().getName())
                        .add("uri", secretResponse.getUri())
                        .add("access_tokens[graph]", "g")
                        .add("access_tokens[environment]", "e")))
                .status(403)
                .responseContains("\"message\": \"Отсутствует доступ\"");
    }
}
