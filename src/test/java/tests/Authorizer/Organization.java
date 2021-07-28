package tests.Authorizer;


import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.authorizer.AuthorizerSteps;
import steps.keyCloak.KeyCloakSteps;
import steps.portalBack.PortalBack;
import tests.Tests;

@DisplayName("Набор тестов по организации")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(1)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Organization extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();


    @Order(1)
    @Test
    @DisplayName("Получение организации")
    @Description("Получение организации с сохранением в Shared Memory")
    public void createProject() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        authorizerSteps.getOrgName("ВТБ");
    }

}
