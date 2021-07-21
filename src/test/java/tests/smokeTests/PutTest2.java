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

@DisplayName("Тесты на загрузку мапы 2")

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(2)
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PutTest2 extends Hooks {

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Первый тест на загрузку мапы 2")
    @Tag("smoke")
    @ParameterizedTest
    @MethodSource("dataProviderMethod")
    @Order(1)
    @Execution(ExecutionMode.CONCURRENT)
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
    @DisplayName("Второй тест на загрузку мапы 2")
    @Tag("smoke")
    @Order(2)
    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void test2() throws InterruptedException {
        System.out.println("test2()");
        Thread.sleep(5000);
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key2", "some value2");
    }

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Третий тест на загрузку мапы 2")
    @Tag("smoke")
    @Order(3)
    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    public void test1() throws InterruptedException {
        System.out.println("test1()");
        Thread.sleep(5000);
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key3", "some value3");
    }
}
