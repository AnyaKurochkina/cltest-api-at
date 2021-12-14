package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.OpenShiftProject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@Epic("Продукты")
@Feature("OpenShift")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("openshift"), @Tag("prod")})
public class OpenShiftTest extends Tests {

    @Link(type="manual", value = "377745")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание проекта {0}")
    void create(OpenShiftProject product) {
        //noinspection EmptyTryBlock
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {}
    }

    @Link(type="manual", value = "377741")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение проекта {0}")
    void change(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.checkPreconditionStatusProduct(ProductStatus.CREATED);
            openShift.changeProject();
        }
    }

    @Link(type="manual", value = "377740")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление проекта {0}")
    @MarkDelete
    void delete(OpenShiftProject product) {
        try (OpenShiftProject openShift = product.createObjectExclusiveAccess()) {
            openShift.deleteObject();
        }
    }
}
