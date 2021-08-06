package tests.keyCloak;

import io.qameta.allure.Description;
import org.jetbrains.annotations.PropertyKey;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import tests.Tests;
import steps.keyCloak.KeyCloakSteps;

@DisplayName("Набор тестов для проверки KeyCloak")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("regress")})
@OrderLabel("tests.keyCloak.KeyCloakTests")
public class KeyCloakTests extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();

    @Test
    @DisplayName("Получение токена")
    @Description("Проверка получения токена для доступа к API портала")
    public void getToken() {
        keyCloakSteps.getUserToken();
    }


}
