package tests.orderService;

import core.utils.Waiting;
import io.qameta.allure.*;
import models.orderService.interfaces.IProduct;
import org.junit.Mock;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@DisplayName("Заказ продуктов")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.OrderTests")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod"), @Tag("rhel")})
public class OrderTests implements Tests {

    @ParameterizedTest(name = "{0}")
    @DisplayName("Заказ продуктов")
    @Owner(value = "Ермаков Роман")
    @Source(ProductArgumentsProvider.PRODUCTS)
//    @Mock
    public void order(IProduct product, String tmsId) {
        Allure.tms("2." + tmsId, "");
        product.toStringProductStep();
        Waiting.sleep((int) ((Math.random() * (60000)) + 0));
        product.order();
    }

}
