package tests.authorizer;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.authorizer.ServiceAccountSteps;
import tests.Tests;

@DisplayName("Удаление сервисных аккаунтов")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.ServiceAccountDeleteTests")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceAccountDeleteTests implements Tests {
    ServiceAccountSteps serviceAccountSteps = new ServiceAccountSteps();

    @ParameterizedTest
    @Order(1)
    @TmsLink("22")
    @DisplayName("Удаление сервисного аккаунта")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Удаление сервисного аккаунта с сохранением в Shared Memory")
    public void createServiceAccount(String env) {
        serviceAccountSteps.deleteServiceAccount("PROJECT_"+env);
    }


}



