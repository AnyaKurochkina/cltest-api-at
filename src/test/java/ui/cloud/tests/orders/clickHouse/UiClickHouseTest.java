package ui.cloud.tests.orders.clickHouse;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.ClickHouse;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import api.Tests;
import ui.cloud.pages.*;
import ui.elements.Alert;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.ConfigExtension;
import ui.extesions.InterceptTestExtension;
import ui.extesions.UiProductTest;

import java.time.Duration;
@Epic("UI Продукты")
@Feature("ClickHouse")
@Tags({@Tag("ui"), @Tag("ui_clickHouse")})
@ExtendWith(ConfigExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
public class UiClickHouseTest extends UiProductTest {

    ClickHouse product;
    //product = ClickHouse.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/7080926c-4670-4857-ba74-464d2e50caf1/main?context=proj-ln4zg69jek&type=project&org=vtb").build();

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("330312")
    @Order(1)
    @DisplayName("UI ClickHouse. Заказ")
    void orderClickHouse() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            ClickHouseOrderPage orderPage = new ClickHouseOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getNameUser().setValue("at_user");
            orderPage.getGeneratePassButton1().shouldBe(Condition.enabled).click();
            orderPage.getGeneratePassButton2().shouldBe(Condition.enabled).click();
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            orderPage.getGroup().select(accessGroup.getPrefixName());
            preBillingProductPrice = IProductPage.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new Alert().checkColor(Alert.Color.GREEN).checkText("Заказ успешно создан");
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            ClickHousePage clickHousePages = new ClickHousePage(product);
            clickHousePages.waitChangeStatus(Duration.ofMinutes(25));
            clickHousePages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        ClickHousePage clickHousePage = new ClickHousePage(product);
        Assertions.assertEquals(preBillingProductPrice, clickHousePage.getCostOrder(), 0.01);
    }

    @Test
    @TmsLink("1236735")
    @Order(2)
    @DisplayName("UI ClickHouse. Проверка полей заказа")
    void
    checkHeaderHistoryTable() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        clickHousePage.checkHeadersHistory();
        clickHousePage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(5)
    @TmsLink("330327")
    @DisplayName("UI ClickHouse. Перезагрузить по питанию")
    void restart() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::restart);
    }


    @Test
    @Order(9)
    @TmsLink("330329")
    @DisplayName("UI ClickHouse. Расширить диск")
    void expandDisk() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.MORE, () -> clickHousePage.enlargeDisk("/app/clickhouse", "20", new Table("Размер, Гб").getRowByIndex(0)));
    }

    @Test
    @Order(11)
    @TmsLink("1177396")
    @DisplayName("UI ClickHouse. Проверить конфигурацию")
    void vmActCheckConfig() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::checkConfiguration);
    }

    @Test
    @Order(15)
    @TmsLink("1162627")
    @DisplayName("UI ClickHouse. Сбросить пароль владельца БД")
    void resetPasswordDb() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::resetPasswordDb);
    }

    @Test
    @Order(17)
    @TmsLink("358259")
    @DisplayName("UI ClickHouse. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::resetPasswordUserDb);
    }

    @Test
    @Order(20)
    @TmsLinks({@TmsLink("330325"), @TmsLink("330330")})
    @Disabled
    @DisplayName("UI ClickHouse. Выключить принудительно / Включить")
    void stopHard() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.LESS, clickHousePage::stopHard);
        clickHousePage.runActionWithCheckCost(CompareType.MORE, clickHousePage::start);
    }

    @Test
    @Order(21)
    @TmsLink("330324")
    @Disabled
    @DisplayName("UI ClickHouse. Выключить")
    void stopSoft() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.LESS, clickHousePage::stopSoft);
    }

    @Test
    @Order(100)
    @TmsLink("330328")
    @DisplayName("UI ClickHouse. Удаление продукта")
    void delete() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.delete();
    }
}
