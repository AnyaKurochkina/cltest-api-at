package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.WildFly;
import org.json.JSONObject;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assumptions;
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
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(WildFly product, Integer num) {
        //noinspection EmptyTryBlock
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("833652")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.expandMountPoint();
        }
    }

    @Disabled
    @TmsLink("833651")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.restart();
        }
    }

    @Disabled
    @TmsLink("833645")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopSoft();
            wildFly.start();
        }
    }

    @Disabled
    @TmsLink("833648")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
//            wildFly.stopHard();
//            try {
                wildFly.resize(wildFly.getMaxFlavorLinuxVm());
//            } finally {
//                wildFly.start();
//            }
        }
    }

    @TmsLink("833647")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверить конфигурацию {0}")
    void refreshVmConfig(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.refreshVmConfig();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("833646"),@TmsLink("833653")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @TmsLink("833644")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить сертификаты {0}")
    void updateCerts(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            JSONObject data = new JSONObject().put("accept_cert_updating", true).put("is_balancer", false).put("is_balancer", "default");
            wildFly.updateCerts(data);
        }
    }

    @TmsLink("1095072")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Синхронизировать конфигурацию сервера {0}")
    void syncDev(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.syncDev();
        }
    }

    @TmsLink("1356699")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить ОС сервера {0}")
    void updateOs(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            Assumptions.assumeFalse(product.isProd(), "Тест отключен для PROD среды");
            wildFly.updateOs();
        }
    }

    @TmsLinks({@TmsLink("1356700"),@TmsLink("1356702")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Запустить сервис {0}")
    void startService(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopService();
            wildFly.startService();
        }
    }

    @TmsLink("1356703")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезапустить сервис {0}")
    void restartService(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.restartService();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Заменить Java Wildfly {0}")
    void wildflyChangeJava(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.wildflyChangeJava();
        }
    }

    @TmsLinks({@TmsLink("989482"),@TmsLink("989486")})
    @Disabled
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавление/Удаление пользователя WildFly {0}")
    void user(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.addUser("user1", "Deployer");
            wildFly.deleteUser("user1", "Deployer");
        }
    }

    @TmsLinks({@TmsLink("989487"),@TmsLink("989491")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавление/Удаление группы WildFly {0}")
    void group(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            String group = PortalBackSteps.getRandomAccessGroup(wildFly.getProjectId(), wildFly.getDomain(), "compute");
            wildFly.addGroup(group, "Deployer");
            wildFly.deleteGroup(group, "Deployer");
        }
    }

    @TmsLink("908268")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка создания {0}")
    void checkCreate(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            assertContains(wildFly.executeSsh("sudo systemctl status wildfly | grep active"), "Active: active (exited)");
        }
    }

    @TmsLink("833649")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(WildFly product, Integer num) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.deleteObject();
        }
    }
}
