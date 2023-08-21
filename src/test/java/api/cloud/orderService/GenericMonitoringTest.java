package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.GenericMonitoring;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Generic Monitoring")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("gm"), @Tag("prod")})
public class GenericMonitoringTest extends Tests {

    @TmsLink("1731017")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(GenericMonitoring product) {
        //noinspection EmptyTryBlock
        try (GenericMonitoring gm = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("1731018")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(GenericMonitoring product) {
        try (GenericMonitoring gm = product.createObjectExclusiveAccess()) {
            gm.expandMountPoint();
        }
    }

    @TmsLink("1731020")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(GenericMonitoring product) {
        try (GenericMonitoring gm = product.createObjectExclusiveAccess()) {
            gm.resize(gm.getMaxFlavorLinuxVm());
        }
    }

    @TmsLink("1731021")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "AD Проверка создания {0}")
    void checkCreate(GenericMonitoring product) {
        try (GenericMonitoring gm = product.createObjectExclusiveAccess()) {
            gm.checkCertsBySsh();
        }
    }

    @TmsLink("1731022")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(GenericMonitoring product) {
        try (GenericMonitoring gm = product.createObjectExclusiveAccess()) {
            gm.deleteObject();
        }
    }
}
