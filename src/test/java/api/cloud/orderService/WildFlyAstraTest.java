package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.WildFly;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.portalBack.PortalBackSteps;

import static core.utils.AssertUtils.assertContains;

@Epic("Продукты")
@Feature("WildFly (Astra)")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("wildfly_astra"), @Tag("prod")})
public class WildFlyAstraTest extends Tests {

    @TmsLink("833650")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(WildFly product) {
        //noinspection EmptyTryBlock
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("833652")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.expandMountPoint();
        }
    }

    @Disabled
    @TmsLink("833651")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.restart();
        }
    }

    @Disabled
    @TmsLink("833645")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopSoft();
            wildFly.start();
        }
    }

    @Disabled
    @TmsLink("833648")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
//            wildFly.stopHard();
//            try {
                wildFly.resize(wildFly.getMaxFlavor());
//            } finally {
//                wildFly.start();
//            }
        }
    }

    @TmsLink("833647")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.refreshVmConfig();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("833646"),@TmsLink("833653")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @TmsLink("833644")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.updateCerts();
        }
    }

    @TmsLink("1095072")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Синхронизировать конфигурацию сервера {0}")
    void syncDev(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.syncDev();
        }
    }

    @TmsLink("1356699")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить ОС сервера {0}")
    void updateOs(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.updateOs();
        }
    }

    @TmsLinks({@TmsLink("1356700"),@TmsLink("1356702")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Запустить сервис {0}")
    void startService(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopService();
            wildFly.startService();
        }
    }

    @TmsLink("1356703")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезапустить сервис {0}")
    void restartService(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.restartService();
        }
    }

    @TmsLinks({@TmsLink("989482"),@TmsLink("989486")})
    @Disabled
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавление/Удаление пользователя WildFly {0}")
    void user(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.addUser("user1", "Deployer");
            wildFly.deleteUser("user1", "Deployer");
        }
    }

    @TmsLinks({@TmsLink("989487"),@TmsLink("989491")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавление/Удаление группы WildFly {0}")
    void group(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            String group = PortalBackSteps.getRandomAccessGroup(wildFly.getProjectId(), wildFly.getDomain(), "compute");
            wildFly.addGroup(group, "Deployer");
            wildFly.deleteGroup(group, "Deployer");
        }
    }

    @TmsLink("908268")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "AD Проверка создания {0}")
    void checkCreate(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            assertContains(wildFly.executeSsh("sudo systemctl status wildfly | grep active"), "Active: active (exited)");
        }
    }

    @TmsLink("833649")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.deleteObject();
        }
    }
}
