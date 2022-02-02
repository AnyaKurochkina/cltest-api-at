package tests.keyCloak;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;


@Epic("Авторизация на портале")
@Tags({@Tag("regress"), @Tag("teamcity")})
public class KeyCloakTest extends Tests {

    @Test
    @TmsLink("376589")
    @DisplayName("Получение токена")
    void getToken() {
        KeyCloakSteps.getUserToken(Role.ADMIN);
    }
}
