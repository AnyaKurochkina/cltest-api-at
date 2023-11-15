package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.GenericDatabase;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Generic Database")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("gb"), @Tag("prod")})
public class GenericDatabaseTest extends Tests {

    @TmsLink("1731025")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(GenericDatabase product, Integer num) {
        //noinspection EmptyTryBlock
        try (GenericDatabase gb = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить ОС {0}")
    void checkActions(GenericDatabase product, Integer num) {
        try (GenericDatabase gb = product.createObjectExclusiveAccess()) {
            Assertions.assertTrue(gb.isActionExist("update_os_vm"));
        }
    }

    @TmsLink("1731024")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(GenericDatabase product, Integer num) {
        try (GenericDatabase gb = product.createObjectExclusiveAccess()) {
            gb.expandMountPoint();
        }
    }

    @TmsLink("1731026")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(GenericDatabase product, Integer num) {
        try (GenericDatabase gb = product.createObjectExclusiveAccess()) {
            gb.resize(gb.getMaxFlavorLinuxVm());
        }
    }

    @TmsLink("1731023")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка создания {0}")
    void checkCreate(GenericDatabase product, Integer num) {
        try (GenericDatabase gb = product.createObjectExclusiveAccess()) {
            gb.checkCertsBySsh();
        }
    }

    @TmsLink("1731027")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(GenericDatabase product, Integer num) {
        try (GenericDatabase gb = product.createObjectExclusiveAccess()) {
            gb.deleteObject();
        }
    }
}
