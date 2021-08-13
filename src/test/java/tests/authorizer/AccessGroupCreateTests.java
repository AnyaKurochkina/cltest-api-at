package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import steps.authorizer.AccessGroupSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Набор тестов по группам доступа")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.AccessGroupCreateTests")
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupCreateTests implements Tests {
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Создание Группы доступа")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Создание Группы доступа с сохранением в Shared Memory")
    public void createBusinessBlock(String env) {
        accessGroupSteps.createAccessGroup("PROJECT_"+env, "access_group");
    }

}