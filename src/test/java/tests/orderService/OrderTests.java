package tests.orderService;

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


@DisplayName("Набор для создания продуктов")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.OrderTests")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod")})
public class OrderTests implements Tests {

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @Mock
    public void order(IProduct product) {
        product.order();
        product.runActionsBeforeOtherTests();
    }



}
