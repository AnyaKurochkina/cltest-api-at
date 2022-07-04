package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.WildFly;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("WildFly")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("wildfly"), @Tag("prod")})
public class WildFlyTest extends Tests {
    final String productName = "WildFly RHEL";

    @TmsLink("377474")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(WildFly product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377467")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.expandMountPoint();
        }
    }

    @TmsLink("377470")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.restart();
        }
    }

    @TmsLink("377473")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopSoft();
            wildFly.start();
        }
    }

    @TmsLink("377469")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopHard();
            try {
                wildFly.resize(wildFly.getMaxFlavor());
            } finally {
                wildFly.start();
            }
        }
    }

    @TmsLink("654210")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.refreshVmConfig();
        }
    }

    @TmsLinks({@TmsLink("377471"),@TmsLink("377472")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @TmsLinks({@TmsLink("989498"),@TmsLink("989495")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавление/Удаление пользователя WildFly {0}")
    void user(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.addUser("user1", "Deployer");
            wildFly.deleteUser("user1", "Deployer");
        }
    }

    @TmsLinks({@TmsLink("989496"),@TmsLink("989497")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавление/Удаление группы WildFly {0}")
    void group(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.addGroup("group1", "Deployer");
            wildFly.deleteGroup("group1", "Deployer");
        }
    }

    @TmsLink("377477")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.updateCerts();
        }
    }

    @TmsLink("377468")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.deleteObject();
        }
    }
}
