package api.cloud.secretService;

import api.cloud.secretService.models.EnginePage;
import api.cloud.secretService.models.SecretResponse;
import api.cloud.secretService.models.User;
import api.cloud.secretService.steps.SecretServiceAdminSteps;
import api.cloud.secretService.steps.SecretServiceSteps;
import core.helper.http.AssertResponse;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Сервис секретов")
@Epic("Действия с секретом (client api)")
public class SecretServiceTest extends AbstractSecretTest {

    @Test
    @TmsLink("1714114")
    @DisplayName("Проверка /health")
    void getV1Health() {
        Assertions.assertTrue(SecretServiceSteps.getV1Health());
    }

    @Test
    @TmsLink("")
    @DisplayName("Проверка /version")
    void getV1Version() {
        Assertions.assertTrue(SecretServiceSteps.getV1Version().startsWith("0."));
    }

    @Test
    @TmsLink("1714115")
    @DisplayName("Создание/Удаление секрета")
    void postV1Secrets() {
        SecretResponse secretResponse = generateSecret();
        secretResponse.delete();
    }

    @Test
    @TmsLink("1714117")
    @DisplayName("Получение данных секрета")
    void getV1Secrets() {
        final JSONObject data = new JSONObject().put("key", "value");
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        JSONObject response = SecretServiceSteps.getV1Secrets(new QueryBuilder()
                .add("segment", secretResponse.getEngine().getSegment())
                .add("engine_name", secretResponse.getEngine().getName())
                .add("uri", secretResponse.getUri()));
        Assertions.assertTrue(data.similar(response));
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение данных секрета. Пустой uri")
    void getV1SecretsEmptyUri() {
        AssertResponse.run(() -> SecretServiceSteps.getV1Secrets(new QueryBuilder()
                        .add("segment", "s")
                        .add("engine_name", "e")))
                .status(400)
                .responseContains("\"code\": \"validation_error\"")
                .responseContains("\"message\": \"uri is missing\"");
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение данных секрета. Пустые segment & engine_name")
    void getV1SecretsEmptySegmentAndEngineName() {
        SecretResponse secretResponse = generateSecret();
        SecretServiceSteps.getV1Secrets(new QueryBuilder().add("uri", secretResponse.getUri()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Секрет не найден")
    void getV1Secrets404() {
        AssertResponse.run(() -> SecretServiceSteps.getV1Secrets(new QueryBuilder().add("uri", "404"))).status(404)
                .responseContains("\"message\": \"Секрет не найден\"");
    }

    @Test
    @TmsLink("")
    @DisplayName("Получить список пользователей")
    void getV1Users() {
        Assertions.assertTrue(SecretServiceAdminSteps.getV1Users().stream().findAny().isPresent());
    }

    @Test
    @TmsLink("")
    @DisplayName("Просмотр пользователя")
    void getV1UsersId() {
        User randomUser = SecretServiceAdminSteps.getV1Users().getList().get(0);
        Assertions.assertEquals(randomUser, SecretServiceAdminSteps.getV1UsersId(randomUser.getId()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Список хранилищ секретов. Фильтр по name")
    void getV1EnginesName() {
        EnginePage enginePage = SecretServiceAdminSteps.getV1Engines(new QueryBuilder());
        final String name = enginePage.getList().get(0).getName();
        Assertions.assertTrue(SecretServiceAdminSteps
                .getV1Engines(new QueryBuilder().add("name", name)).stream().allMatch(e -> e.getName().equals(name)));
    }

    @Test
    @TmsLink("")
    @DisplayName("Список хранилищ секретов. Фильтр по segment")
    void getV1EnginesSegment() {
        EnginePage enginePage = SecretServiceAdminSteps.getV1Engines(new QueryBuilder());
        final String segment = enginePage.getList().get(0).getSegment();
        Assertions.assertTrue(SecretServiceAdminSteps
                .getV1Engines(new QueryBuilder().add("segment", segment)).stream().allMatch(e -> e.getSegment().equals(segment)));
    }

    @Test
    @TmsLink("")
    @DisplayName("Список хранилищ секретов. include")
    void getV1EnginesInclude() {
        Assertions.assertNotNull(SecretServiceAdminSteps.getV1Engines(new QueryBuilder().add("include", "total_count")).getMeta().getTotalCount());
    }
}
