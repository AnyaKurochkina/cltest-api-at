package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import models.orderService.products.OpenShiftProject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("OpenShift")
@Tag("tariffPlans")
public class OpenShiftTests extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("40")
    @ParameterizedTest(name = "Создание проекта {0}")
    public void create(OpenShiftProject p) {
        IProduct product = p.createObjectExclusiveAccess();
        product.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("41")
    @ParameterizedTest(name = "Изменение проекта {0}")
    public void change(OpenShiftProject p) {
        try (IProduct product = p.createObjectExclusiveAccess()) {
            product.invokeAction("Изменить проект");
        }
    }

    @TmsLink("42")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление проекта {0}")
    @Deleted(OpenShiftProject.class)
    public void delete(OpenShiftProject p) {
        try (IProduct product = p.createObjectExclusiveAccess()) {
            product.invokeAction("Удалить проект");
        }
    }

    @TmsLink("43")
    @Test
    @Story("Простой тест")
    public void test() {
    }

}
