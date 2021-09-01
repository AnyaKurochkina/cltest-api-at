package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.authorizer.ServiceAccountSteps;
import tests.Tests;

@DisplayName("Набор тестов по сервисным аккаунтам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.ServiceAccountCreateTests")
@Tags({@Tag("regress"), @Tag("orgStructure")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceAccountCreateTests implements Tests {
    ServiceAccountSteps serviceAccountSteps = new ServiceAccountSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Создание сервисного аккаунта")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Создание сервисного аккаунта с сохранением в Shared Memory")
    public void createServiceAccount(String env, String tmsId) {
        serviceAccountSteps.createServiceAccount("PROJECT_"+env);
    }



}



