package tests.portalBack;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.portalBack.AccessGroupSteps;
import tests.Tests;

@DisplayName("Набор тестов по удалению групп доступа")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.AccessGroupDeleteTests")
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupDeleteTests implements Tests {
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Удаление Группы доступа")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Удаление Группы доступа")
    public void deleteAccessGroup(String env) {
        accessGroupSteps.deleteAccessGroup(env);
    }


}