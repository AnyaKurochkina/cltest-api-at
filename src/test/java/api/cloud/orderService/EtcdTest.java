package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.Artemis;
import models.cloud.orderService.products.Etcd;
import models.cloud.orderService.products.TarantoolDataGrid;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.List;

@Epic("Продукты")
@Feature("Etcd")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("wtcd"), @Tag("prod")})
public class EtcdTest extends Tests {


    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(Etcd product) {
        //noinspection EmptyTryBlock
        try (Etcd etcd = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Проверка создания {0}")
    void checkCreate(Etcd product) {
        try (Etcd etcd = product.createObjectExclusiveAccess()) {
            etcd.checkUserGroupBySsh();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Расширить точку монтирования {0}")
    void expandMountPoint(Etcd product) {
        try (Etcd etcd = product.createObjectExclusiveAccess()) {
            etcd.expandMountPoint("/app/etcd/data");
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Проверить конфигурацию {0}")
    void refreshVmConfig(Etcd product) {
        try (Etcd etcd = product.createObjectExclusiveAccess()) {
            etcd.refreshVmConfig();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Сбросить пароль {0}")
    void resetPassword(Etcd product) {
        try (Etcd etcd = product.createObjectExclusiveAccess()) {
            etcd.resetPassword("OyrjONmSaArAd7NkqCBdXxlvpy51");
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создание сертификата для пользователя etcd {0}")
    void createCerts(Etcd product) {
        try (Etcd etcd = product.createObjectExclusiveAccess()) {
            etcd.createCerts();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    @MarkDelete
    void delete(Etcd product) {
        try (Etcd etcd = product.createObjectExclusiveAccess()) {
            etcd.deleteObject();
        }
    }
}
