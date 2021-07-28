package tests.portalBack;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.keyCloak.KeyCloakSteps;
import steps.portalBack.PortalBack;
import tests.Tests;

import java.util.stream.Stream;

import static steps.Steps.titleInformationSystem;

@DisplayName("Получение среды назначения")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(400)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectEnvironment extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    PortalBack portalBack = new PortalBack();


    @Order(1)
    @ParameterizedTest
    @MethodSource("dataProjectEnv")
    @DisplayName("Получение среды назначения")
    @Description("Получение среды назначения с сохранением в Shared Memory")
    public void getProjectEnv(String env) {
        testVars.setVariables("token", keyCloakSteps.getToken());
        portalBack.getProjectEnv(env);
    }

    static Stream<Arguments> dataProjectEnv() {
        return Stream.of(Arguments.arguments("DEV"), Arguments.arguments("TEST"));
    }
}
