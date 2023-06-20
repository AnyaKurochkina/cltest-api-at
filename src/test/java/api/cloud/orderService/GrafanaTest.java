package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Grafana;
import models.cloud.orderService.products.Podman;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import static core.utils.AssertUtils.assertContains;

@Epic("Продукты")
@Feature("Grafana")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("grafana"), @Tag("prod")})
public class GrafanaTest extends Tests {

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Grafana product) {
        //noinspection EmptyTryBlock
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Grafana product) {
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
            grafana.expandMountPoint();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(Grafana product) {
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
            grafana.resetPassword("Ha81GpR75Dhet6yY0t4DBBNjJ6imn");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "AD Проверка создания {0}")
    void checkCreate(Grafana product) {
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
            grafana.checkCertsBySsh();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Grafana product) {
        try (Grafana grafana = product.createObjectExclusiveAccess()) {
            grafana.deleteObject();
        }
    }
}
