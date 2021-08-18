package tests.orderService;

import models.orderService.interfaces.IProduct;
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


@DisplayName("Набор для проверки экшенов #1")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.ActionsTests")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod")})
public class ActionsTests implements Tests {

    @ParameterizedTest
    @DisplayName("Проверка actions продуктов #1")
    @Source(ProductArgumentsProvider.PRODUCTS)
    //@Mock
    public void runActions(IProduct product) {
            product.runActionsBeforeOtherTests();
    }


}
