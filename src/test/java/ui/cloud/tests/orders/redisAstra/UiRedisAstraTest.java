package ui.cloud.tests.orders.redisAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Redis;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.*;
import ui.elements.Alert;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("Redis (Astra)")
@Tags({@Tag("ui"), @Tag("ui_redis_astra")})
public class UiRedisAstraTest extends UiProductTest {

    private Redis product;// = Redis.builder().build().buildFromLink("https://ift2-portal-front.oslb-dev01.corp.dev.vtb/all/orders/9121cf13-4723-4bd7-9ea7-77319da23191/main?context=proj-gxsz4e3shy&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("796925")
    @Order(1)
    @DisplayName("UI RedisAstra. Заказ")
    void orderRedisAstra() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .expandProductsList()
                    .selectProduct(product.getProductName());
            RedisAstraOrderPage orderPage = new RedisAstraOrderPage();
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getUserInput().setValue(RedisAstraOrderPage.userNameRedisSentinel);
            orderPage.getGeneratePassButton().click();
            Alert.green("Значение скопировано");
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getRoleSelect().set("superuser");
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
    @TmsLink("1236732")
    @Order(2)
    @DisplayName("UI RedisAstra. Проверка графа в истории действий")
    void checkHeaderHistoryTable() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.checkHeadersHistory();
        redisPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(3)
    @TmsLink("796991")
    @DisplayName("UI RedisAstra. Расширить точку монтирования")
    void expandDisk() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, () -> redisPage.enlargeDisk("/app/redis/data", "20", new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Order(4)
    @TmsLink("797003")
    @DisplayName("UI RedisAstra. Изменить конфигурацию")
    void changeConfiguration() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, redisPage::changeConfiguration);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(5)
    @TmsLink("797006")
    @DisplayName("UI RedisAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::checkConfiguration);
    }

    @Test
    @Order(6)
    @TmsLink("")
    @DisplayName("UI RedisAstra. Сбросить пароль (удалить)")
    void resetPassword() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.resetPassword(RedisAstraOrderPage.userNameRedisSentinel, "Сбросить пароль (удалить)"));
    }

    @Test
    @Order(7)
    @TmsLink("")
    @DisplayName("UI RedisAstra. Сбросить пароль пользователя")
    void resetPasswordUser() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.resetPassword(RedisAstraOrderPage.userNameRedisSentinel, "Сбросить пароль пользователя"));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @TmsLinks({@TmsLink("1454017"), @TmsLink("1454015")})
    @Order(8)
    @DisplayName("UI RedisAstra. Удалить и добавить группу доступа")
    void deleteGroup() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.deleteGroup("user");
        redisPage.addGroup("superuser", Collections.singletonList(product.accessGroup()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @TmsLink("1454016")
    @Order(9)
    @DisplayName("UI RedisAstra. Изменить состав группы доступа")
    void updateGroup() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.updateGroup("superuser",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @Order(10)
    @TmsLink("1296747")
    @DisplayName("UI RedisAstra. Мониторинг ОС")
    void monitoringOs() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(11)
    @TmsLink("1296747")
    @DisplayName("UI RedisAstra. Изменить параметр notify-keyspace-events")
    void changeParamNotify() {
        if (product.isProd()) {
            RedisAstraPage redisPage = new RedisAstraPage(product);
            redisPage.runActionWithCheckCost(CompareType.MORE, () -> redisPage.changeParamNotify("KEA"));
        }
    }

    @Test
    @Order(12)
    @TmsLink("")
    @DisplayName("UI RedisAstra. Обновить ОС")
    void updateOs() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::updateOs);
    }

    @Test
    @Order(13)
    @TmsLink("")
    @DisplayName("UI RedisAstra. Установить Ключ-Астром")
    void addKeyAstrom() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, redisPage::addKeyAstrom);
    }

    @Test
    @Order(14)
    @TmsLink("")
    @DisplayName("UI RedisAstra. Удалить Ключ-Астром")
    void delKeyAstrom() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.LESS, redisPage::delKeyAstrom);
    }

    @Test
    @Order(100)
    @TmsLink("796989")
    @DisplayName("UI RedisAstra. Удалить рекурсивно")
    void delete() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.delete();
    }
}