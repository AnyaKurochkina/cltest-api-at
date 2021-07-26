package tests.smokeTests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.smokeProjectRunSteps.SmokeProjectRunSteps;

@Tag("get")
@DisplayName("Тесты на чтение из мапы")
public class Get2Test {

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Первый тест на чтение из мапы get2")
    @Test
    public void test1(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key");
    }

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Второй тест на чтение из мапы get2")
    @Test
    public void test2(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key2");
    }

    @Description("Что-нибудь > загружаем элемент из мапы по ключу")
    @DisplayName("Третий тест на чтение из мапы get2")
    @Test
    public void test3(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.getFromMap("some key3");
    }


}
