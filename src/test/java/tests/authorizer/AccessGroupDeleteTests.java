package tests.Authorizer;

import io.qameta.allure.Description;
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
@Order(99997)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupDeleteTests extends Tests {
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();

    @ParameterizedTest
    @MethodSource("dataEnv")
    @Order(1)
    @DisplayName("Удаление Группы доступа")
    @Description("Удаление Группы доступа")
    public void deleteAccessGroup(String env) {
        accessGroupSteps.deleteAccessGroup(env);
    }

    static Stream<Arguments> dataEnv() {
        return Stream.of(Arguments.arguments("DEV"),
                Arguments.arguments("TEST"))
                ;
    }

}