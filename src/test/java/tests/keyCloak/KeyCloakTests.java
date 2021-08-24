package tests.keyCloak;

import io.qameta.allure.Description;
import io.qameta.allure.Link;
import io.qameta.allure.TmsLink;
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
    @TmsLink("1")
    //@Link(type="manual", value = "271850")
    @DisplayName("Получение токена")
    @Description("Проверка получения токена для доступа к API портала")
    public void getToken() {
        KeyCloakSteps.getUserToken();
    }
}
