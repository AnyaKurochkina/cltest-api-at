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
@Feature("Nginx Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("nginx_astra"), @Tag("prod")})
public class NginxAstraTest extends Tests {
    final String productName = "Nginx Astra";

    @TmsLink("846594")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Nginx product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (Nginx nginx = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("846597")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Nginx product) {
        product.setProductName(productName);
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.expandMountPoint();
        }
    }

    @TmsLink("846600")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Nginx product) {
        product.setProductName(productName);
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.restart();
        }
    }

    @TmsLink("846599")
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

    @TmsLink("846596")
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

    @TmsLinks({@TmsLink("846595"),@TmsLink("846598")})
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

    @TmsLink("846593")
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
