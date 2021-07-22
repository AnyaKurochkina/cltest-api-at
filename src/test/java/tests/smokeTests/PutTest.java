package tests.smokeTests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.Hooks;
import steps.smokeProjectRunSteps.SmokeProjectRunSteps;

@DisplayName("Тесты на загрузку мапы")
@Tag("smoke")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PutTest {

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Первый тест на загрузку мапы")
    @Test
    public void test3() {
        System.out.println("test3()");
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some_key", "some_value");
    }

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Второй тест на загрузку мапы")
    @Test
    public void test2() {
        System.out.println("test2()");
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key2", "some value2");
    }

    @Description("Что-нибудь > загружаем в мапу")
    @DisplayName("Третий тест на загрузку мапы")
    @Test
//    @Order(1)
    public void test1() {
        System.out.println("test1()");
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.loadMap("some key3", "some value3");
    }
}
