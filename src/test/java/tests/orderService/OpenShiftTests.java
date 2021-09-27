package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("OpenShift")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("tariffPlans")
@Execution(ExecutionMode.CONCURRENT)
@Order(1)
public class OpenShiftTests extends Tests {
//    @Order(1)
    @TmsLink("40")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @Story("Создание проекта")
    @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest(name = "Создание проекта {0}")
    public void create(IProduct p, String tmsId) {
        IProduct product = p.createObjectExclusiveAccess();
        product.close();
    }

//    @Order(2)
    @TmsLink("41")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @Story("Изменение проекта")
    @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest(name = "Изменение проекта {0}")
    public void change(IProduct p, String tmsId) {
        try (IProduct product = p.createObjectExclusiveAccess()) {
            product.invokeAction("Изменить проект");
        }
    }

//    @Order(3)
//    @TmsLink("42")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @Story("Удаление проекта")
//    @ParameterizedTest(name = "Удаление проекта {0}")
//    public void delete(IProduct p, String tmsId) {
//        try (IProduct product = p.createObjectExclusiveAccess()) {
//            product.invokeAction("Удалить проект");
//        }
//    }

}
