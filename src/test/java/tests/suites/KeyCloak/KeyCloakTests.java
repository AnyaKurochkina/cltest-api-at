package tests.suites.KeyCloak;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SuiteDisplayName;
import tests.suites.RhelFullAction;
import tests.suites.Tests;

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
