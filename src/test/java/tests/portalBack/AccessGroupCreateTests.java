package tests.portalBack;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import steps.portalBack.AccessGroupSteps;
import tests.Tests;

@DisplayName("Набор тестов по группам доступа")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.portalBack.AccessGroupCreateTests")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupCreateTests implements Tests {
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();

    @ParameterizedTest
    @Order(1)
    @TmsLink("13")
    @DisplayName("Создание Группы доступа")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Создание Группы доступа с сохранением в Shared Memory")
    public void createAccessGroup(String env, String tmsId) {
        accessGroupSteps.createAccessGroup("PROJECT_"+env, "access-group");
    }
}