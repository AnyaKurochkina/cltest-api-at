package ui.cloud.tests.orders.redisAstra;


import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.Redis;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
@Epic("UI Продукты")
@Feature("Redis (Astra)")
@Tags({@Tag("ui"), @Tag("ui_redis_astra")})
public class UiRedisAstraTest extends UiProductTest {

    Redis product;
    //= Redis.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/eb4e1177-30c7-4bdc-94e0-a5d65d5de1ae/main?context=proj-1oob0zjo5h&type=project&org=vtb");
    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("796925")
    @Order(1)
    @DisplayName("UI RedisAstra. Заказ")
    void orderPostgreSQL() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            RedisAstraOrderPage orderPage = new RedisAstraOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getGeneratePassButton().shouldBe(Condition.enabled).click();
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            orderPage.getConfigure().set(Product.getFlavor(product.getMinFlavor()));
            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            orderPage.getGroup().select(accessGroup.getPrefixName());
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = EntitiesUtils.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            RedisAstraPage pSqlPages = new RedisAstraPage (product);
            pSqlPages.waitChangeStatus(Duration.ofMinutes(25));
            pSqlPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        Assertions.assertEquals(preBillingProductPrice, pSqlPage.getCostOrder(), 0.01);
    }


    @Test
    @TmsLink("1236732")
    @Order(2)
    @DisplayName("UI RedisAstra. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        pSqlPage.checkHeadersHistory();
        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(9)
    @TmsLink("796991")
    @DisplayName("UI RedisAstra. Расширить диск")
    void expandDisk() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.enlargeDisk("/app/redis/data", "20", new Table("Роли узла").getRowByIndex(0)));
    }


    @Test
    @Order(10)
    @TmsLink("797003")
    @DisplayName("UI RedisAstra. Изменить конфигурацию")
    void changeConfiguration() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::changeConfiguration);
    }

    @Test
    @Order(11)
    @TmsLink("797006")
    @DisplayName("UI RedisAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::checkConfiguration);
    }

    @Test
    @Order(19)
    @TmsLink("796997")
    @DisplayName("UI RedisAstra. Сбросить пароль")
    void resetPassword () {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::resetPassword);
    }

    @Test
    @Order(100)
    @TmsLink("796989")
    @DisplayName("UI RedisAstra. Удаление продукта")
    void delete() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.delete();
    }

 }
