package tests.orderService;

import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
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

    @TmsLink("391703")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Astra product) {
        //noinspection EmptyTryBlock
        try (Astra astra = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("391705")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkPreconditionStatusProduct(ProductStatus.CREATED);
            astra.expandMountPoint();
        }
    }

    @TmsLink("391699")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkPreconditionStatusProduct(ProductStatus.CREATED);
            astra.restart();
        }
    }

    @TmsLink("391702")
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

    @TmsLink("391704")
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

    @TmsLink("391701")
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

    @TmsLink("391700")
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

    @TmsLink("391698")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.deleteObject();
        }
    }
}
