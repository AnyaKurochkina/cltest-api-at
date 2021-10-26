package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.OpenShiftProject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@Epic("Продукты")
@Feature("OpenShift")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("openshift")})
public class OpenShiftTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание проекта {0}")
    void create(OpenShiftProject product) {
        OpenShiftProject openShift = product.createObjectExclusiveAccess();
        openShift.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение проекта {0}")
    void change(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.changeProject();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление проекта {0}")
    @Deleted(OpenShiftProject.class)
    void delete(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.deleteObject();
        }
    }
}
