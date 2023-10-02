package api.cloud.keyCloak;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;
import api.Tests;

import static org.junit.jupiter.api.Assertions.fail;


@Feature("Авторизация на портале")
@Epic("Главная страница")
@Tags({@Tag("regress"), @Tag("teamcity"), @Tag("health_check")})
public class KeyCloakTest extends Tests {

    @Test
    @TmsLink("376589")
    @DisplayName("Получение токена")
    void getUserToken() {
        KeyCloakSteps.getUserToken(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("111259")
    @DisplayName("Получение токена SA")
    void getTokenServiceAccount() {
        KeyCloakSteps.getServiceAccountToken(((Project)Project.builder().isForOrders(true).build().createObject()).getId(), Role.CLOUD_ADMIN);
    }
}
