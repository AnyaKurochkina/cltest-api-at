package tests.keyCloak;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.Project;
import models.authorizer.ServiceAccount;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.fail;


@Feature("Авторизация на портале")
@Epic("Главная страница")
@Tags({@Tag("regress"), @Tag("teamcity")})
public class KeyCloakTest extends Tests {

    @Test
    @TmsLink("376589")
    @DisplayName("Получение токена")
    void getUserToken() {
        KeyCloakSteps.getUserToken(Role.ADMIN);
    }

    @Test
    @TmsLink("111259")
    @DisplayName("Получение токена SA")
    void getTokenServiceAccount() {
        KeyCloakSteps.getServiceAccountToken(((Project)Project.builder().isForOrders(true).build().createObject()).getId());
    }
}
