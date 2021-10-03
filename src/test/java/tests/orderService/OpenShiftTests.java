package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.OpenShiftProject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("OpenShift")
@Tag("orders")
public class OpenShiftTests extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("40")
    @ParameterizedTest(name = "Создание проекта {0}")
    void create(OpenShiftProject product) {
        OpenShiftProject openShift = product.createObjectExclusiveAccess();
        openShift.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("41")
    @ParameterizedTest(name = "Изменение проекта {0}")
    void change(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.changeProject("Изменить проект");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("42")
    @ParameterizedTest(name = "Изменение проекта {0}")
    void changeQuotas(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.changeProject("Изменить проект");
        }
    }

    @TmsLink("43")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление проекта {0}")
    @Deleted(OpenShiftProject.class)
    void delete(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.deleteObject();
        }
    }

//    @TmsLink("43")
//    @Test
//    @DisplayName("Простой тест")
//    public void test() {
//    }

}
