package tests.portalBack;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;
import steps.portalBack.PortalBackSteps;
import tests.Tests;

import static steps.Steps.titleInformationSystem;

@DisplayName("Набор тестов по информационным ситсемам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(200)
@Tags({@Tag("regress")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InformationSystem extends Tests {
    PortalBackSteps portalBackSteps = new PortalBackSteps();


    @Order(1)
    @Test
    @DisplayName("Получение информационной системы")
    @Description("Получение информационной системы с сохранением в Shared Memory")
    public void getInformationSystem() {
        portalBackSteps.getInfoSys(titleInformationSystem);
    }

}
