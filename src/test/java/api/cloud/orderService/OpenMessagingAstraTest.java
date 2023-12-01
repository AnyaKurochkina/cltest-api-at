package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.OpenMessagingAstra;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
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
    @ParameterizedTest(name = "[{1}] Обновление установки {0}")
    void expandMountPoint(OpenMessagingAstra product, Integer num) {
        try (OpenMessagingAstra astra = product.createObjectExclusiveAccess()) {
            astra.upgradeSetup();
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
