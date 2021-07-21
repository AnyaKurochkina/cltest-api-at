package tests.smokeTests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.Hooks;
import steps.smokeProjectRunSteps.SmokeProjectRunSteps;
import tests.MyExtension;

import java.util.stream.Stream;

@DisplayName("Тесты на загрузку мапы")
@Order(1)
@Tag("smoke")
public class PutTest extends Hooks {

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Первый тест на загрузку мапы")
    @Tag("smoke")
    @ParameterizedTest
    @MethodSource("dataProviderMethod")
    public void test1(String key, String value) {
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
    @Tag("production")
    @Test
    public void test2() {
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key2", "some value2");
    }

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Третий тест на загрузку мапы")
    @ExtendWith(MyExtension.class)
    @Test
    public void test3() {
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key3", "some value3");
    }
}
