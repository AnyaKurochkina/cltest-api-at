package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.AccessGroup;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Rhel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Rhel")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("rhel"), @Tag("prod")})
public class RhelTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Rhel product) {
        //noinspection EmptyTryBlock
        try (Rhel rhel = product.createObjectExclusiveAccess()){}
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.expandMountPoint();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания {0}")
    @Disabled
    void checkCreate(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            AccessGroup accessGroup = AccessGroup.builder().projectName(rhel.getProjectId()).build().createObject();
            Assertions.assertTrue(accessGroup.getUsers().size() > 0, String.format("Нет пользователей в группе %s", accessGroup.getName()));
            rhel.checkCreateUseSsh(accessGroup.getUsers().get(0));
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.restart();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.stopSoft();
            rhel.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.stopHard();
            try {
                rhel.resize();
            } finally {
                rhel.start();
            }
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.stopHard();
            rhel.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.stopHard();
            rhel.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.deleteObject();
        }
    }
}
