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
@Tag("smoke")
public class Put2Test {

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Первый тест на загрузку мапы 2")
    @Tag("smoke")
    @ParameterizedTest
    @MethodSource("dataProviderMethod")
    public void test3(String key, String value) {
        System.out.println("test4()");
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
    @Test
    public void test2() throws InterruptedException {
        System.out.println("test5()");
        Thread.sleep(5000);
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key22", "some value22");
    }

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Третий тест на загрузку мапы 2")
    @Tag("smoke")
    @Test
    public void test1() throws InterruptedException {
        System.out.println("test6()");
        Thread.sleep(5000);
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key33", "some value33");
    }
}
