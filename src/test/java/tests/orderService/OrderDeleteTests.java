package tests.orderService;

import core.utils.Waiting;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.IProductMock;
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


@DisplayName("Удаление заказанных продуктов")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.OrderDeleteTests")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod")})
public class OrderDeleteTests implements Tests {

    @ParameterizedTest
    @DisplayName("Удаление заказанных продуктов")
    @Source(ProductArgumentsProvider.PRODUCTS)
    //@Mock
    public void orderDelete(IProduct product) {
        Assume.assumeTrue("Продукт "+ product.toString() + " не был заказан",product.getStatus() == ProductStatus.CREATED);
        Waiting.sleep((int) ((Math.random() * (60000)) + 0));
        product.runActionsAfterOtherTests();
    }


}

