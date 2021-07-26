package tests.smokeTests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.Hooks;
import steps.smokeProjectRunSteps.SmokeProjectRunSteps;

import java.util.stream.Stream;

@DisplayName("Тесты на загрузку мапы")
@Order(1)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class PutTest extends Hooks {

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Первый тест на загрузку мапы")
    @Tag("smoke")
    @ParameterizedTest
    @MethodSource("dataProviderMethod")
    @Order(1)
    public void test3(String key, String value) {
        System.out.println("test3()");
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap(key, value);
    }


    static Stream<Arguments> dataProviderMethod() {
        return Stream.of
                (Arguments.arguments("some key", "some value"),
                        Arguments.arguments("some key5", "some value5"),
                        Arguments.arguments("some key7", "some value7"));

    }

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Второй тест на загрузку мапы")
    @Tag("smoke")
    @Test
    @Order(2)
    public void test2() {
        System.out.println("test2()");
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key2", "some value2");
    }

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Третий тест на загрузку мапы")
    @Tag("smoke")
    @Test
    @Order(3)
    public void test1() {
        System.out.println("test1()");
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key3", "some value3");
    }
}
