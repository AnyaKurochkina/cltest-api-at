package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.Grafana;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Grafana")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("grafana"), @Tag("prod")})
public class GrafanaTest extends Tests {

    @TmsLink("1731001")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(Grafana product, Integer num) {
        //noinspection EmptyTryBlock
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("1731002")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(Grafana product, Integer num) {
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
            grafana.expandMountPoint();
        }
    }

    @TmsLink("1731003")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Сбросить пароль {0}")
    void resetPassword(Grafana product, Integer num) {
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
            grafana.resetPassword("Ha81GpR75Dhet6yY0t4DBBNjJ6imn");
        }
    }

    @TmsLink("1731006")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка создания {0}")
    void checkCreate(Grafana product, Integer num) {
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
            grafana.checkCertsBySsh();
        }
    }

    @TmsLink("1731008")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(Grafana product, Integer num) {
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
            grafana.deleteObject();
        }
    }
}
