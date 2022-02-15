package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.OpenShiftProject;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@Epic("Продукты")
@Feature("OpenShift")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("openshift"), @Tag("prod")})
public class OpenShiftTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("376186")
    @ParameterizedTest(name = "Создание проекта {0}")
    void create(OpenShiftProject product) {
        //noinspection EmptyTryBlock
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {}
    }

    @Tag("actions")
    @TmsLink("376495")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение проекта {0}")
    void change(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.changeProject();
        }
    }

    @TmsLink("376187")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление проекта {0}")
    @MarkDelete
    void delete(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.deleteObject();
        }
    }
}
