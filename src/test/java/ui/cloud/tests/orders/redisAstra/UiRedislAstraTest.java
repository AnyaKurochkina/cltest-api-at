package ui.cloud.tests.orders.redisAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.PostgreSQL;
import models.orderService.products.Redis;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.time.Duration;

@ExtendWith(ConfigExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_redis_astra")})
@Log4j2
public class UiRedislAstraTest extends Tests {

    Redis product;

    public UiRedislAstraTest() {
        if (Configure.ENV.equals("prod"))
         // product = Redis.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            product = Redis.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/3eba6325-c957-43cb-8ef8-a2ec3694e04a/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = Redis.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

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
            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            orderPage.getGroup().select(accessGroup.getPrefixName());
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = IProductPage.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
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
    @Order(5)
    @TmsLink("796986")
    @DisplayName("UI RedisAstra. Перезагрузить по питанию")
    void restart() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::restart);
    }

    @Test
    @Order(9)
    @TmsLink("796991")
    @DisplayName("UI RedisAstra. Расширить диск")
    void expandDisk() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.enlargeDisk("/app/redis/data", "20", new Table("Размер, Гб").getRowByIndex(0)));
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
    @Order(20)
    @TmsLinks({@TmsLink("796982"), @TmsLink("797001")})
    @DisplayName("UI RedisAstra. Выключить принудительно / Включить")
    void stopHard() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopHard);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::start);
    }

    @Test
    @Order(21)
    @TmsLink("796981")
    @DisplayName("UI RedisAstra. Выключить")
    void stopSoft() {
        RedisAstraPage pSqlPage = new RedisAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopSoft);
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
