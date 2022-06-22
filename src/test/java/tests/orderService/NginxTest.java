package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.Nginx;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Nginx")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("nginx"), @Tag("prod")})
public class NginxTest extends Tests {
    final String productName = "Nginx";

    @TmsLink("377462")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Nginx product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (Nginx nginx = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377453")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Nginx product) {
        product.setProductName(productName);
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.expandMountPoint();
        }
    }

    @TmsLink("377456")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Nginx product) {
        product.setProductName(productName);
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.restart();
        }
    }

    @TmsLink("377460")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Nginx product) {
        product.setProductName(productName);
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.stopSoft();
            nginx.start();
        }
    }

    @TmsLink("377455")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Nginx product) {
        product.setProductName(productName);
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.stopHard();
            try {
                nginx.resize(nginx.getMaxFlavor());
            } finally {
                nginx.start();
            }

        }
    }

    @TmsLink("377457")
    @TmsLinks({@TmsLink("377457"),@TmsLink("377458")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(Nginx product) {
        product.setProductName(productName);
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.stopHard();
            nginx.start();
        }
    }

    @TmsLink("377454")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Nginx product) {
        product.setProductName(productName);
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.deleteObject();
        }
    }
}
