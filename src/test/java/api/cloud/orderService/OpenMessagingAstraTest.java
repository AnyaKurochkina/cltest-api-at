package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.OpenMessagingAstra;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("OpenMessaging Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("open_messaging_astra"), @Tag("prod")})
public class OpenMessagingAstraTest extends Tests {

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(OpenMessagingAstra product, Integer num) {
        //noinspection EmptyTryBlock
        try (OpenMessagingAstra astra = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновление установки LT {0}")
    void expandMountPoint(OpenMessagingAstra product, Integer num) {
        try (OpenMessagingAstra astra = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(product.getEnv().equalsIgnoreCase("LT"), "Тест включен только для LT среды");
            astra.upgradeSetup();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить операционную систему LT {0}")
    void updateOS(OpenMessagingAstra product, Integer num) {
        try (OpenMessagingAstra astra = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(product.getEnv().equalsIgnoreCase("LT"), "Тест включен только для LT среды");
            astra.updateOS();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновление сертификатов LT {0}")
    void updateCerts(OpenMessagingAstra product, Integer num) {
        try (OpenMessagingAstra astra = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(product.getEnv().equalsIgnoreCase("LT"), "Тест включен только для LT среды");
            astra.updateCerts();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Вертикальное масштабирование {0}")
    void verticalScaling(OpenMessagingAstra product, Integer num) {
        try (OpenMessagingAstra astra = product.createObjectExclusiveAccess()) {
            astra.verticalScaling();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(OpenMessagingAstra product, Integer num) {
        try (OpenMessagingAstra astra = product.createObjectExclusiveAccess()) {
            astra.deleteObject();
        }
    }
}
