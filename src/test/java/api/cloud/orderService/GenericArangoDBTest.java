package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.GenericArangoDB;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("GenericArangoDB")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("generic_arango_db"), @Tag("prod")})
public class GenericArangoDBTest extends Tests {

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(GenericArangoDB product, Integer num) {
        //noinspection EmptyTryBlock
        try (GenericArangoDB arangoDb = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(GenericArangoDB product, Integer num) {
        try (GenericArangoDB arangoDb = product.createObjectExclusiveAccess()) {
            arangoDb.expandMountPoint();
        }
    }

    @TmsLink("")
    @Tag("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить ОС {0}")
    void updateOsVm(GenericArangoDB product, Integer num) {
        try (GenericArangoDB arangoDb = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(arangoDb.isDev(), "Тест включен только для dev среды");
            arangoDb.updateOsVm();
        }
    }

    @Disabled
    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(GenericArangoDB product, Integer num) {
        try (GenericArangoDB arangoDb = product.createObjectExclusiveAccess()) {
            arangoDb.restart();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(GenericArangoDB product, Integer num) {
        try (GenericArangoDB arangoDb = product.createObjectExclusiveAccess()) {
            arangoDb.resize(arangoDb.getMaxFlavorLinuxVm());
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверка прав у ролей пользователя {0}")
    void checkCreate(GenericArangoDB product, Integer num) {
        try (GenericArangoDB arangoDb = product.createObjectExclusiveAccess()) {
            arangoDb.checkUserGroupBySsh();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка создания {0}")
    void checkCreateAd(GenericArangoDB product, Integer num) {
        try (GenericArangoDB arangoDb = product.createObjectExclusiveAccess()) {
            arangoDb.checkCertsBySsh();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(GenericArangoDB product, Integer num) {
        try (GenericArangoDB arangoDb = product.createObjectExclusiveAccess()) {
            arangoDb.deleteObject();
        }
    }
}
