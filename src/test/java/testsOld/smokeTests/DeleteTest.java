package testsOld.smokeTests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import stepsOld.Hooks;
import stepsOld.smokeProjectRunSteps.SmokeProjectRunSteps;
@Order(3)
@DisplayName("Тесты на удаление из мапы")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteTest extends Hooks {

    @Description("Что-нибудь > удаляем элемент из мапы по ключу")
    @DisplayName("Первый тест на удаление из мапы")
    @Tag("smoke")
    @Order(1)
    @Test
    public void test1(){
        SmokeProjectRunSteps smokeProjectRunSteps = new SmokeProjectRunSteps();
        smokeProjectRunSteps.deleteFromMap("some key");
    }

    @Description("Что-нибудь > удаляем элемент из мапы по ключу")
    @DisplayName("Первый тест на удаление из мапы")
    @Order(2)
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

