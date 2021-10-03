package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Rhel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Rhel")
@Tags({@Tag("regress"), @Tag("orders")})
public class RhelTests extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("44")
    @ParameterizedTest(name = "Создать {0}")
    void create(Rhel product) {
        Rhel rhel = product.createObjectExclusiveAccess();
        rhel.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("45")
    @ParameterizedTest(name = "Расширить {0}")
    void change(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.expandMountPoint("Расширить");
        }
    }

    @TmsLink("46")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted(Rhel.class)
    void delete(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.deleteObject();
        }
    }

//    @TmsLink("47")
//    @Test
//    @Story("Простой тест 2")
//    public void test() {
//    }

}
