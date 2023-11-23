package ui.cloud.tests.orders.tarantoolDataGridAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.TarantoolDataGrid;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
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

@Epic("UI Продукты")
@Feature("Tarantool Data Grid Astra")
@Tags({@Tag("ui"), @Tag("ui_tarantool_data_grid_astra")})
public class UiTarantoolDataGridAstraTest extends UiProductTest {

    TarantoolDataGrid product; // = TarantoolDataGrid.builder().build().buildFromLink("https://ift2-portal-front.oslb-dev01.corp.dev.vtb/db/orders/6da3b2b7-d956-4f5c-83a3-11b7b2da9490/main?context=proj-gxsz4e3shy&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("")
    @Order(1)
    @DisplayName("UI TarantoolDataGrid. Заказ")
    void orderTarantoolDataGrid() {
        double prebillingCost;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            TarantoolDataGridAstraOrderPage orderPage = new TarantoolDataGridAstraOrderPage();
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelectCluster().set("tdg:rps-2000:storage-6GB");
            orderPage.getGroupSelect().set(accessGroup);
            orderPage.getGroupSelectTarantool().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
            tarantoolDataGridAstraPage.waitChangeStatus(Duration.ofMinutes(25));
            tarantoolDataGridAstraPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        checkOrderCost(prebillingCost, tarantoolDataGridAstraPage);
    }

    @Test
    @TmsLink("")
    @Order(2)
    @DisplayName("UI TarantoolDataGrid. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.getGeneralInfoTab().switchTo();
        tarantoolDataGridAstraPage.checkHeadersHistory();
        tarantoolDataGridAstraPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(3)
    @Disabled
    @TmsLink("")
    @DisplayName("UI TarantoolDataGrid. Обновить версию приложения Tarantool Data Grid")
    void updateVersionApp() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.runActionWithCheckCost(CompareType.EQUALS, tarantoolDataGridAstraPage::updateVersionApp);
    }

    @Test
    @Order(4)
    @TmsLink("")
    @DisplayName("UI TarantoolDataGrid. Обновить сертификаты Tarantool Data Grid")
    void updateCertificate() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.runActionWithCheckCost(CompareType.EQUALS, tarantoolDataGridAstraPage::updateCertificate);
    }

    @Test
    @Order(5)
    @TmsLink("")
    @DisplayName("UI TarantoolDataGrid. Создать резервную копию")
    void createReserveCopy() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.runActionWithCheckCost(CompareType.EQUALS, tarantoolDataGridAstraPage::createReserveCopy);
    }

    @Test
    @Order(6)
    @TmsLink("")
    @DisplayName("UI TarantoolDataGrid. Остановка сервисов TDG")
    void stopTdg() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.runActionWithCheckCost(CompareType.EQUALS, tarantoolDataGridAstraPage::stopTdg);
    }

    @Test
    @Order(7)
    @TmsLink("")
    @DisplayName("UI TarantoolDataGrid. Запуск сервисов TDG")
    void startTdg() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.runActionWithCheckCost(CompareType.EQUALS, tarantoolDataGridAstraPage::startTdg);
    }

    @Test
    @Order(8)
    @TmsLink("")
    @DisplayName("UI TarantoolDataGrid. Перезапуск сервисов TDG")
    void resetTdg() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.runActionWithCheckCost(CompareType.EQUALS, tarantoolDataGridAstraPage::resetTdg);
    }


    @Test
    @Order(9)
    @TmsLink("")
    @DisplayName("UI TarantoolDataGrid. Мониторинг ОС")
    void monitoringOs() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.checkMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("")
    @DisplayName("UI TarantoolDataGrid. Удаление продукта")
    void delete() {
        TarantoolDataGridAstraPage tarantoolDataGridAstraPage = new TarantoolDataGridAstraPage(product);
        tarantoolDataGridAstraPage.runActionWithCheckCost(CompareType.LESS, tarantoolDataGridAstraPage::delete);
    }
}
