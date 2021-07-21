package tests.smokeTests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.Hooks;
import steps.smokeProjectRunSteps.SmokeProjectRunSteps;
@Order(3)
@Tag("smoke")
@DisplayName("Тесты на удаление из мапы")
public class DeleteTest extends Hooks {

    @Description("Что-нибудь > удаляем элемент из мапы по ключу")
    @DisplayName("Первый тест на удаление из мапы")
    @Tag("smoke")
    @Test
    public void test1(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.deleteFromMap("some key");
    }

    @Description("Что-нибудь > удаляем элемент из мапы по ключу")
    @DisplayName("Первый тест на удаление из мапы")
    @Test
    public void test2(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.deleteFromMap("some key2");
    }

    @Description("Что-нибудь > удаляем элемент из мапы по ключу")
    @DisplayName("Первый тест на удаление из мапы")
    @Test
    public void test3(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.deleteFromMap("some key3");
    }
}

