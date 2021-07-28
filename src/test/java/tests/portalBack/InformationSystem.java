package tests.portalBack;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.authorizer.ProjectSteps;
import steps.keyCloak.KeyCloakSteps;
import steps.portalBack.PortalBack;
import tests.Tests;

import java.util.stream.Stream;

import static steps.Steps.titleInformationSystem;

@DisplayName("Набор тестов по информационным ситсемам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(200)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InformationSystem extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    PortalBack portalBack = new PortalBack();


    @Order(1)
    @Test
    @DisplayName("Получение информационной системы")
    @Description("Получение информационной системы с сохранением в Shared Memory")
    public void getInformationSystem() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        portalBack.getInfoSys(titleInformationSystem);
    }

}
