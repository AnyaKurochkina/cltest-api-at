package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Astra;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("astra"), @Tag("prod")})
public class AstraTest extends Tests {

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Astra product) {
        //noinspection EmptyTryBlock
        try (Astra astra = product.createObjectExclusiveAccess()) {}
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkPreconditionStatusProduct(ProductStatus.CREATED);
            astra.expandMountPoint();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkPreconditionStatusProduct(ProductStatus.CREATED);
            astra.restart();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkPreconditionStatusProduct(ProductStatus.CREATED);
            astra.stopSoft();
            astra.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkPreconditionStatusProduct(ProductStatus.CREATED);
            astra.stopHard();
            try {
                astra.resize(astra.getMaxFlavor());
            } finally {
                astra.start();
            }
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkPreconditionStatusProduct(ProductStatus.CREATED);
            astra.stopHard();
            astra.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkPreconditionStatusProduct(ProductStatus.CREATED);
            astra.stopHard();
            astra.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.deleteObject();
        }
    }
}
