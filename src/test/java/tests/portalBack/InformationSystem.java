package tests.portalBack;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;
import steps.portalBack.PortalBackSteps;
import tests.Tests;

import static steps.Steps.titleInformationSystem;

@DisplayName("Набор тестов по информационным ситсемам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.portalBack.InformationSystem")
@Tags({@Tag("regress"), @Tag("orgStructure")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InformationSystem implements Tests {
    PortalBackSteps portalBackSteps = new PortalBackSteps();

    @Order(1)
    @Test
    @DisplayName("Получение информационной системы")
    @Description("Получение информационной системы с сохранением в Shared Memory")
    public void getInformationSystem() {
        portalBackSteps.getInfoSys(titleInformationSystem);
    }
}
