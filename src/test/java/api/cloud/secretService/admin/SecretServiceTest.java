package api.cloud.secretService.admin;

import api.cloud.secretService.AbstractSecretTest;
import io.qameta.allure.TmsLink;
import models.cloud.secretService.SecretResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.secretService.SecretServiceSteps;

public class SecretServiceTest extends AbstractSecretTest {

    @Test
    @TmsLink("")
    @DisplayName("Проверка /health")
    void getV1Health() {
        Assertions.assertTrue(SecretServiceSteps.getV1Health());
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание/Удаление секрета")
    void postV1Secrets() {
        SecretResponse secretResponse = generateSecret();
        secretResponse.delete();
    }
}
