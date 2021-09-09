package tests.portalBack;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
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
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectEnvironment implements Tests {
    PortalBackSteps portalBackSteps = new PortalBackSteps();

    @Order(1)
    @TmsLink("15")
    @ParameterizedTest
    @Source(ProductArgumentsProvider.ENV)
    @DisplayName("Получение среды назначения")
    @Description("Получение среды назначения с сохранением в Shared Memory")
    public void getProjectEnv(String env) {
        portalBackSteps.getProjectEnv(env);
    }
}
