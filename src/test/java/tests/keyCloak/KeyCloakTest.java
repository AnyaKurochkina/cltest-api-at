package tests.keyCloak;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;


@Epic("Авторизация на портале")
@Tags({@Tag("regress"), @Tag("teamcity")})
public class KeyCloakTest extends Tests {

    @Test
    //@Link(type="manual", value = "271850")
//    @Tag("tariffPlans")
    @DisplayName("Получение токена")
    void getToken() {
        KeyCloakSteps.getUserToken();
    }
}
