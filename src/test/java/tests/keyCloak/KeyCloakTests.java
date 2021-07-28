package tests.keyCloak;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import tests.Tests;
import steps.keyCloak.KeyCloakSteps;

@DisplayName("Набор тестов для проверки KeyCloak")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(1)
@Tag("regress")
public class KeyCloakTests extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();

    @Test
    @DisplayName("Получение токена")
    @Description("Проверка получения токена для доступа к API портала")
    public void getToken() {
        keyCloakSteps.getToken();
    }


}
