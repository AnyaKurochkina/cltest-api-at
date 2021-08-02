package tests.Authorizer;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.authorizer.ServiceAccountSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Удаление сервисных аккаунтов")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(99997)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceAccountDeleteTests extends Tests {
    ServiceAccountSteps serviceAccountSteps = new ServiceAccountSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Удаление сервисного аккаунта")
    @MethodSource("dataEnv")
    @Description("Удаление сервисного аккаунта с сохранением в Shared Memory")
    public void createServiceAccount(String env) {
        serviceAccountSteps.deleteServiceAccount("PROJECT_"+env);
    }

    static Stream<Arguments> dataEnv() {
        return Stream.of(Arguments.arguments("DEV"), Arguments.arguments("TEST"));
    }

}



