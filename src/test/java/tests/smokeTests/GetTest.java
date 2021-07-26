package tests.smokeTests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.Hooks;
import steps.smokeProjectRunSteps.SmokeProjectRunSteps;
@Order(2)
@Tag("kek")
@DisplayName("Тесты на чтение из мапы")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetTest extends Hooks {

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Первый тест на чтение из мапы")
    @Tag("smoke")
    @Order(1)
    @Test
    public void test1(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key");
    }

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Второй тест на чтение из мапы")
    @Test
    @Order(2)
    public void test2(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key2");
    }

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Третий тест на чтение из мапы")
    @Test
    @Order(3)
    public void test3(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key3");
    }


}
