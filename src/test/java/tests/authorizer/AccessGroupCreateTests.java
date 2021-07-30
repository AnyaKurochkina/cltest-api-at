package tests.authorizer;

import io.qameta.allure.Description;
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
@Order(600)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupCreateTests extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();

    @ParameterizedTest
    @MethodSource("dataEnv")
    @Order(1)
    @DisplayName("Создание Группы доступа")
    @Description("Создание Группы доступа с сохранением в Shared Memory")
    public void createBusinessBlock(String env) {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accessGroupSteps.createAccessGroup("PROJECT_"+env, "access_group");
    }

    static Stream<Arguments> dataEnv() {
        return Stream.of(Arguments.arguments("DEV"), Arguments.arguments("TEST"));
    }

}