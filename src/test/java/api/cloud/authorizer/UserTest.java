package api.cloud.authorizer;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.User;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;

@Epic("Управление")
@Feature("Пользователи")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTest extends Tests {

    @Order(1)
    @Test
    @TmsLink("734430")
    @DisplayName("Добавление пользователя")
    void addUser() {
        User.builder().build().createObject();
    }

    @Order(2)
    @Test
    @TmsLink("747955")
    @MarkDelete
    @DisplayName("Удаление пользователя")
    void deleteUser() {
        User.builder().build().createObjectExclusiveAccess().deleteObject();
    }
}
