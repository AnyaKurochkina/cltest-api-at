package tests.orderService;

import core.utils.Waiting;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import org.junit.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@DisplayName("Проверка actions у заказанных продуктов")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.ActionsTests")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod")})
public class ActionsTests implements Tests {

    @ParameterizedTest
    @DisplayName("Проверка actions у заказанных продуктов")
    @Source(ProductArgumentsProvider.PRODUCTS)
    //@Mock
    public void runActions(IProduct product) {
        Assume.assumeTrue("Продукт "+ product.toString() + " не был заказан",product.getStatus() == ProductStatus.CREATED);
        Waiting.sleep((int) ((Math.random() * (60000)) + 0));
        product.runActionsBeforeOtherTests();
    }


}
