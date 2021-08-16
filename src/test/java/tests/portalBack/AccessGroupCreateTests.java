package tests.portalBack;

import io.qameta.allure.Description;
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
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupCreateTests implements Tests {
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Создание Группы доступа")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Создание Группы доступа с сохранением в Shared Memory")
    public void createAccessGroup(String env) {
        accessGroupSteps.createAccessGroup("PROJECT_"+env, "access_group");
    }

}