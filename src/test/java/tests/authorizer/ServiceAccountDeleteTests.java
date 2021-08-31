package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.authorizer.ServiceAccountSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Удаление сервисных аккаунтов")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.ServiceAccountDeleteTests")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("rhel")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceAccountDeleteTests implements Tests {
    ServiceAccountSteps serviceAccountSteps = new ServiceAccountSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Удаление сервисного аккаунта")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Удаление сервисного аккаунта с сохранением в Shared Memory")
    public void createServiceAccount(String env) {
        serviceAccountSteps.deleteServiceAccount("PROJECT_"+env);
    }


}



