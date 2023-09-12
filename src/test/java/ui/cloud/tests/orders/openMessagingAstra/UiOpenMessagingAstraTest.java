package ui.cloud.tests.orders.openMessagingAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.OpenMessagingAstra;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.*;
import ui.elements.Graph;
import ui.extesions.UiProductTest;

import java.time.Duration;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("OpenMessagingAstra")
@Tags({@Tag("ui"), @Tag("ui_openmessaging_astra")})
public class UiOpenMessagingAstraTest extends UiProductTest {

    OpenMessagingAstra product;// = OpenMessagingAstra.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/all/orders/323af104-d7ee-4f4c-8c2c-f57805328c98/main?context=proj-iv550odo9a&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("")
    @Order(1)
    @DisplayName("UI OpenMessaging Astra. Заказ")
    void orderOpenMessagingAstra() {
        double prebillingCost;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            OpenMessagingAstraOrderPage orderPage = new OpenMessagingAstraOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
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
            OpenMessagingAstraPage openMessagingAstraPage = new OpenMessagingAstraPage(product);
            openMessagingAstraPage.waitChangeStatus(Duration.ofMinutes(25));
            openMessagingAstraPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        OpenMessagingAstraPage openMessagingAstraPage = new OpenMessagingAstraPage(product);
        checkOrderCost(prebillingCost, openMessagingAstraPage);
    }
    @Test
    @TmsLink("")
    @Order(2)
    @DisplayName("UI OpenMessaging Astra. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        OpenMessagingAstraPage openMessagingAstraPage = new OpenMessagingAstraPage(product);
        openMessagingAstraPage.getGeneralInfoTab().switchTo();
        openMessagingAstraPage.checkHeadersHistory();
        openMessagingAstraPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[2]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }
    @Test
    @Order(3)
    @TmsLink("")
    @DisplayName("UI OpenMessaging Astra. Обновление установки")
    void updateInstallation() {
        OpenMessagingAstraPage openMessagingAstraPage = new OpenMessagingAstraPage(product);
        openMessagingAstraPage.runActionWithCheckCost(CompareType.EQUALS, openMessagingAstraPage::updateInstallation);
    }
    @Test
    @Order(4)
    @TmsLink("")
    @DisplayName("UI OpenMessaging Astra. Мониторинг ОС")
    void monitoringOs() {
        OpenMessagingAstraPage openMessagingAstraPage = new OpenMessagingAstraPage(product);
        openMessagingAstraPage.checkClusterMonitoringOs();
    }
    @Test
    @Order(100)
    @TmsLink("")
    @DisplayName("UI OpenMessaging Astra. Удаление продукта")
    void delete() {
        OpenMessagingAstraPage openMessagingAstraPage = new OpenMessagingAstraPage(product);
        openMessagingAstraPage.runActionWithCheckCost(CompareType.LESS, openMessagingAstraPage::delete);
    }
}
