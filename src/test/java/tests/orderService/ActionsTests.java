package tests.orderService;

import core.utils.Waiting;
import io.qameta.allure.Allure;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import org.junit.*;
import org.junit.jupiter.api.*;
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

    @ParameterizedTest(name = "{0}")
    @Owner(value = "Ермаков Роман")
    @DisplayName("Проверка actions у заказанных продуктов")
    @Source(ProductArgumentsProvider.PRODUCTS)
//    @Mock
    public void runActions(IProduct product, String tmsId) {
        Allure.tms("3." + tmsId, "");
        Assumptions.assumeTrue(product.getStatus() == ProductStatus.CREATED, "Продукт "+ product.toString() + " не был заказан");
        Waiting.sleep((int) ((Math.random() * (60000)) + 0));
        product.runActionsBeforeOtherTests();
    }
}
