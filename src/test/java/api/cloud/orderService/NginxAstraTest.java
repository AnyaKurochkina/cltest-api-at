package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.Nginx;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import static core.utils.AssertUtils.assertContains;

@Log4j2
@Epic("Продукты")
@Feature("Nginx Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("nginx_astra"), @Tag("prod")})
public class NginxAstraTest extends Tests {

    @TmsLink("846594")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(Nginx product, Integer num) {
        //noinspection EmptyTryBlock
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("846597")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(Nginx product, Integer num) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.expandMountPoint();
        }
    }

    @Disabled
    @TmsLink("846600")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(Nginx product, Integer num) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.restart();
        }
    }

    @Disabled
    @TmsLink("846599")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(Nginx product, Integer num) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.stopSoft();
            nginx.start();
        }
    }

    @TmsLink("846596")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(Nginx product, Integer num) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
//            nginx.stopHard();
//            try {
            nginx.resize(nginx.getMaxFlavorLinuxVm());
//            } finally {
//                nginx.start();
//            }
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("846595"), @TmsLink("846598")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(Nginx product, Integer num) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.stopHard();
            nginx.start();
        }
    }

    @TmsLink("1127039")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить сертификаты {0}")
    void updateCerts(Nginx product, Integer num) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.updateCerts();
        }
    }

    @TmsLink("847277")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка создания {0}")
    void checkCreate(Nginx product, Integer num) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            assertContains(nginx.executeSsh("sudo systemctl status nginx | grep active"), "Active: active (running)");
        }
    }

    @TmsLink("846593")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(Nginx product, Integer num) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.deleteObject();
        }
    }
}
