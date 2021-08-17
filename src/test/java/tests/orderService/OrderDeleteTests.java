package tests.orderService;

import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.IProductMock;
import org.junit.Mock;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@DisplayName("Набор для проверки экшенов #2")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.OrderDeleteTests")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod")})
public class OrderDeleteTests implements Tests {

    @ParameterizedTest
    @DisplayName("Проверка actions продуктов #2")
    @Source(ProductArgumentsProvider.PRODUCTS)
    //@Mock
    public void orderDelete(IProduct product) {
        product.runActionsAfterOtherTests();
    }


}

