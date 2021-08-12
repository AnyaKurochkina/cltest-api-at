package tests.KeyCloak;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;


@DisplayName("Набор тестов для проверки KeyCloak")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("regress"), @Tag("teamcity")})
@OrderLabel("tests.keyCloak.KeyCloakTests")
public class KeyCloakTests implements Tests {

    @Test
    @DisplayName("Получение токена")
    @Description("Проверка получения токена для доступа к API портала")
    public void getToken() {
        KeyCloakSteps.getUserToken();
    }


}
