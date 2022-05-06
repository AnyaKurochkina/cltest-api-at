package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Moon;
import models.orderService.products.OpenShiftProject;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@Epic("Продукты")
@Feature("Moon")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("moon"), @Tag("prod")})
public class MoonTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("847365")
    @ParameterizedTest(name = "Создание проекта {0}")
    void create(Moon product) {
        //noinspection EmptyTryBlock
        try (Moon moon = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("847367")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление проекта {0}")
    @MarkDelete
    void delete(Moon product) {
        try (Moon moon = product.createObjectExclusiveAccess()) {
            moon.deleteObject();
        }
    }
}
