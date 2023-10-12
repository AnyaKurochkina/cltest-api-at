package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.TarantoolDataGrid;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.List;

@Epic("Продукты")
@Feature("Tarantool Data Grid")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("tarantool_data_grid"), @Tag("prod")})
public class TarantoolDataGridTest extends Tests {
    private static final List<String> services = Arrays.asList("zorg-core-01", "zorg-r01-s01");

    @TmsLink("1746053")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(TarantoolDataGrid product) {
        //noinspection EmptyTryBlock
        try (TarantoolDataGrid tarantool = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("1746057")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать резервную копию {0}")
    void backup(TarantoolDataGrid product) {
        try (TarantoolDataGrid tarantool = product.createObjectExclusiveAccess()) {
            tarantool.backup();
        }
    }

    @TmsLink("1746073")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Обновить сертификаты {0}")
    void updateCerts(TarantoolDataGrid product) {
        try (TarantoolDataGrid tarantool = product.createObjectExclusiveAccess()) {
            tarantool.updateCerts();
        }
    }

    @TmsLink("1746075")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Остановить/запустить сервисы {0}")
    void stopInstances(TarantoolDataGrid product) {
        try (TarantoolDataGrid tarantool = product.createObjectExclusiveAccess()) {
            tarantool.stopInstances(services);
            tarantool.startInstances(services);
        }
    }

    @TmsLink("1746100")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Перезапустить сервисы {0}")
    void restartInstances(TarantoolDataGrid product) {
        try (TarantoolDataGrid tarantool = product.createObjectExclusiveAccess()) {
            tarantool.restartInstances(services);
        }
    }

    @TmsLink("1746103")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    @MarkDelete
    void delete(TarantoolDataGrid product) {
        try (TarantoolDataGrid tarantool = product.createObjectExclusiveAccess()) {
            tarantool.deleteObject();
        }
    }
}
