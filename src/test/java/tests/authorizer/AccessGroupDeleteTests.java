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

@DisplayName("Набор тестов по удалению групп доступа")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.AccessGroupDeleteTests")
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupDeleteTests implements Tests {
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();

    @ParameterizedTest
    @Source(ProductArgumentsProvider.ENV)
    @Order(1)
    @DisplayName("Удаление Группы доступа")
    @Description("Удаление Группы доступа")
    public void deleteAccessGroup(String env) {
        accessGroupSteps.deleteAccessGroup(env);
    }


}