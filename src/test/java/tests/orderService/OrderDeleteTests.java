package tests.orderService;

import io.qameta.allure.Allure;
import io.qameta.allure.Owner;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import org.junit.Assume;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@DisplayName("Удаление заказанных продуктов")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.OrderDeleteTests")
@Tags({@Tag("regress"), @Tag("orders2"), @Tag("prod"), @Tag("smoke")})
public class OrderDeleteTests extends Tests {


    @Owner(value = "Ермаков Роман")
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удаление заказанных продуктов")
    @Source(ProductArgumentsProvider.PRODUCTS)
//    @Mock
    public void orderDelete(IProduct product, String tmsId) {
        Allure.tms("19." + tmsId, "");
        product.toStringProductStep();
        Assumptions.assumeTrue(product.getStatus() == ProductStatus.CREATED, "Продукт "+ product.toString() + " не был заказан");
        product.runActionsAfterOtherTests();
    }
}

