package ui.cloud.tests.orders.redisSentinelAstra;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Redis;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;
import static ui.cloud.pages.orders.RedisAstraOrderPage.userNameRedisSentinel;

@Epic("UI Продукты")
@Feature("Redis (Astra)")
@Tags({@Tag("ui"), @Tag("ui_redis_sentinel_astra")})
public class UiRedisSentinelAstraTest extends UiProductTest {

    Redis product;// = Redis.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/all/orders/da477d21-546a-4da3-b39c-11d14e4de0ef/main?context=proj-iv550odo9a&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        product.setProductName("Redis Sentinel Astra (Redis с репликацией)");
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("")
    @Order(1)
    @DisplayName("UI Redis Sentinel Astra. Заказ")
    void orderRedisAstra() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .expandProductsList()
                    .selectProduct(product.getProductName());
            RedisAstraOrderPage orderPage = new RedisAstraOrderPage();
            orderPage.getSegmentSelect().set("B2B-HCE-TS-DEV-SRV-APP");
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getUserInput().setValue(userNameRedisSentinel);
            orderPage.getGeneratePassButton().click();
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelectRedisSentinel().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getRoleSelect().set("user");
            orderPage.getGroupSelect().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            RedisAstraPage redisPage = new RedisAstraPage(product);
            redisPage.waitChangeStatus(Duration.ofMinutes(25));
            redisPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        RedisAstraPage redisPage = new RedisAstraPage(product);
        checkOrderCost(prebillingCost, redisPage);
    }

    @Test
    @TmsLink("")
    @Order(2)
    @DisplayName("UI Redis Sentinel Astra. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.checkHeadersHistory();
        redisPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(9)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Расширить точку монтирования")
    void expandDisk() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, () -> redisPage.enlargeDisk("/app/redis/data", "20", new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Order(10)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Изменить конфигурацию")
    void changeConfiguration() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, redisPage::changeConfigurationSentinel);
    }

    @Test
    @Order(11)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Проверить конфигурацию")
    void vmActCheckConfig() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::checkConfiguration);
    }


    @Test
    @Order(12)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Сбросить пароль")
    void resetPassword() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.resetPasswordSentinel(RedisAstraOrderPage.userNameRedisSentinel));
    }

    @Test
    @Order(13)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Удалить пользователя")
    void deleteUser() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.deleteUser(RedisAstraOrderPage.userNameRedisSentinel));
    }

    @Test
    @Order(14)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Создать пользователя")
    void createUser() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.createUser(RedisAstraOrderPage.userNameRedisSentinel));
    }


    @Test
    @TmsLinks({@TmsLink(""), @TmsLink("")})
    @Order(15)
    @DisplayName("UI Redis Sentinel Astra. Удалить и добавить группу доступа")
    void deleteGroup() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.deleteGroup("user");
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        redisPage.addGroup("user", Collections.singletonList(accessGroup.getPrefixName()));
    }

    @Test
    @TmsLink("")
    @Order(16)
    @DisplayName("UI Redis Sentinel Astra. Изменить состав группы доступа")
    void updateGroup() {
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.updateGroup("user",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @Order(17)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Выпустить клиентский сертификат")
    void issueClientCertificate() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.issueClientCertificate(RedisAstraOrderPage.userNameRedisSentinel));
    }


    @Test
    @Order(18)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Мониторинг ОС")
    void monitoringOs() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.checkMonitoringOs();
    }

    @Test
    @Order(19)
    @TmsLink("")
    @Disabled
    @DisplayName("UI Redis Sentinel Astra. Изменить параметр notify-keyspace-events")
    void changeParamNotify() {
        if (product.isProd()) {
            RedisAstraPage redisPage = new RedisAstraPage(product);
            redisPage.runActionWithCheckCost(CompareType.MORE, () -> redisPage.changeParamNotify("KEA"));
        }
    }

    @Test
    @Order(100)
    @TmsLink("")
    @DisplayName("UI Redis Sentinel Astra. Удалить рекурсивно")
    void delete() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.delete();
    }
}