package tests.smokeTests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.Hooks;
import steps.smokeProjectRunSteps.SmokeProjectRunSteps;
@Order(2)
@Tag("smoke")
@DisplayName("Тесты на чтение из мапы")
public class GetTest extends Hooks {

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Первый тест на чтение из мапы")
    @Tag("smoke")
    @Test
    public void test1(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key");
    }

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Второй тест на чтение из мапы")
    @Test
    public void test2(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key2");
    }

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Третий тест на чтение из мапы")
    @Test
    public void test3(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key3");
    }


}
