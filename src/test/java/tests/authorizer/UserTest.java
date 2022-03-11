package tests.authorizer;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Управление")
@Feature("Пользователи")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke")})
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTest extends Tests {

    @Order(1)
    @Test
    @TmsLink("734430")
    @DisplayName("Добавление пользователя")
    void addUser() {
        User.builder().build().createObject();
    }
}
