package api.cloud.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.OpenShiftProject;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;


@Epic("Продукты")
@Feature("OpenShift")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("openshift"), @Tag("prod")})
public class OpenShiftTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("376186")
    @ParameterizedTest(name = "[{1}] Создание проекта {0}")
    void create(OpenShiftProject product, Integer num) {
        //noinspection EmptyTryBlock
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {}
    }

    @Tag("actions")
    @TmsLink("376495")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменение проекта {0}")
    void change(OpenShiftProject product, Integer num) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.changeProject();
        }
    }

    @TmsLink("376187")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление проекта {0}")
    @MarkDelete
    void delete(OpenShiftProject product, Integer num) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.deleteObject();
        }
    }
}
