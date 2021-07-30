package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.authorizer.ProjectSteps;
import steps.authorizer.ServiceAccountSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Набор тестов по сервисным аккаунтам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(600)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceAccountCreateTests extends Tests {
    ServiceAccountSteps serviceAccountSteps = new ServiceAccountSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Создание сервисного аккаунта")
    @MethodSource("dataEnv")
    @Description("Создание сервисного аккаунта с сохранением в Shared Memory")
    public void createServiceAccount(String env) {
        serviceAccountSteps.createServiceAccount("PROJECT_"+env);
    }

    static Stream<Arguments> dataEnv() {
        return Stream.of(Arguments.arguments("DEV"), Arguments.arguments("TEST"));
    }

}



