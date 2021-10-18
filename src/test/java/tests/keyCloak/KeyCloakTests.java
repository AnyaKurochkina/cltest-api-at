package tests.keyCloak;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;


@Epic("Авторизация на портале")
@Tags({@Tag("regress"), @Tag("teamcity")})
public class KeyCloakTests extends Tests {

    @Test
    //@Link(type="manual", value = "271850")
//    @Tag("tariffPlans")
    @DisplayName("Получение токена")
    void getToken() {
        KeyCloakSteps.getUserToken();
    }
}
