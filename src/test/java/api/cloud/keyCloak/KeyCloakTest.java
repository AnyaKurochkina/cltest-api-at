package api.cloud.keyCloak;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import steps.keyCloak.KeyCloakSteps;


@Feature("Авторизация на портале")
@Epic("Главная страница")
@Tags({@Tag("regress"), @Tag("teamcity"), @Tag("health_check")})
public class KeyCloakTest extends Tests {

    @Test
    @TmsLink("SOUL-6526")
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
