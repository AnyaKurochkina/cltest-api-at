package ui.cloud.tests.orders.redisAstra;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Redis;
import models.cloud.portalBack.AccessGroup;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.*;
import ui.cloud.pages.orders.*;
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

    Redis product;
    //= Redis.builder().build().buildFromLink("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/db/orders/37be3184-396f-4d3d-94aa-534cea51a43d/main?context=proj-pkvckn08w9&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
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
            orderPage.getCreateDefaultUserSwitch().setEnabled(true);
            orderPage.getGeneratePassButton().click();
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
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
    @DisplayName("UI RedisAstra. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.checkHeadersHistory();
        redisPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(9)
    @TmsLink("796991")
    @DisplayName("UI RedisAstra. Расширить точку монтирования")
    void expandDisk() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, () -> redisPage.enlargeDisk("/app/redis/data", "20", new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Order(10)
    @TmsLink("797003")
    @DisplayName("UI RedisAstra. Изменить конфигурацию")
    void changeConfiguration() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, redisPage::changeConfiguration);
    }

    @Test
    @Order(11)
    @TmsLink("797006")
    @DisplayName("UI RedisAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::checkConfiguration);
    }

    @Test
    @Order(19)
    @TmsLink("796997")
    @DisplayName("UI RedisAstra. Сбросить пароль")
    void resetPassword() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::resetPassword);
    }

    @Test
    @TmsLinks({@TmsLink("1454017"), @TmsLink("1454015")})
    @Order(25)
    @DisplayName("UI RedisAstra. Удалить и добавить группу доступа")
    void deleteGroup() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.deleteGroup("superuser");
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        redisPage.addGroup("superuser", Collections.singletonList(accessGroup.getPrefixName()));
    }

    @Test
    @TmsLink("1454016")
    @Order(26)
    @DisplayName("UI RedisAstra. Изменить состав группы доступа")
    void updateGroup() {
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.updateGroup("superuser",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @Order(27)
    @EnabledIfEnv("prod")
    @TmsLink("1296747")
    @DisplayName("UI Windows. Мониторинг ОС")
    void monitoringOs() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.checkMonitoringOs();
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