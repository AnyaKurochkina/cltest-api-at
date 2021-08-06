package tests.portalBack;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.keyCloak.KeyCloakSteps;
import steps.portalBack.PortalBackSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Получение среды назначения")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.portalBack.ProjectEnvironment")
@Tags({@Tag("regress")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectEnvironment extends Tests {
    PortalBackSteps portalBackSteps = new PortalBackSteps();


    @Order(1)
    @ParameterizedTest
    @MethodSource("dataProjectEnv")
    @DisplayName("Получение среды назначения")
    @Description("Получение среды назначения с сохранением в Shared Memory")
    public void getProjectEnv(String env) {
        portalBackSteps.getProjectEnv(env);
    }

    static Stream<Arguments> dataProjectEnv() {
        return Stream.of(Arguments.arguments("DEV"), Arguments.arguments("TEST"));
    }
}
