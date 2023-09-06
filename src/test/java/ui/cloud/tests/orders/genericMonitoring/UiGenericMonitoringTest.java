package ui.cloud.tests.orders.genericMonitoring;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.GenericMonitoring;
import org.junit.EnabledIfEnv;
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

@Epic("UI Продукты")
@Feature("GenericMonitoring")
@Tags({@Tag("ui"), @Tag("ui_generic_monitoring")})
public class UiGenericMonitoringTest extends UiProductTest {

    GenericMonitoring product;// = GenericMonitoring.builder().build().buildFromLink("https://ift2-portal-front.oslb-dev01.corp.dev.vtb/all/orders/b7202e43-ca1a-4300-be33-8da921183d11/main?context=proj-pkvckn08w9&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1687127")
    @Order(1)
    @DisplayName("UI GenericMonitoring. Заказ")
    void orderGenericMonitoring() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            GenericMonitoringOrderPage orderPage = new GenericMonitoringOrderPage();
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
            GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
            genericMonitoringPage.waitChangeStatus(Duration.ofMinutes(25));
            genericMonitoringPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        checkOrderCost(prebillingCost, genericMonitoringPage);
    }

    @Test
    @TmsLink("1742146")
    @Order(2)
    @DisplayName("UI GenericMonitoring. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.getGeneralInfoTab().switchTo();
        genericMonitoringPage.checkHeadersHistory();
        genericMonitoringPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[2]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(3)
    @TmsLink("1687139")
    @DisplayName("UI GenericMonitoring. Расширить точку монтирования")
    void expandDisk() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.MORE, () -> genericMonitoringPage
                .enlargeDisk("/app", "20", new Table("Размер, ГБ").getRowByIndex(0)));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(4)
    @TmsLink("1687130")
    @DisplayName("UI GenericMonitoring. Проверить конфигурацию")
    void vmActCheckConfig() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.EQUALS, genericMonitoringPage::checkConfiguration);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(5)
    @TmsLinks({@TmsLink("1687134"), @TmsLink("1687131")})
    @DisplayName("UI GenericMonitoring. Удалить и добавить группу доступа")
    void addGroup() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.EQUALS, () -> genericMonitoringPage.deleteGroup("user"));
        genericMonitoringPage.runActionWithCheckCost(CompareType.EQUALS, () -> genericMonitoringPage.addGroup("user", Collections.singletonList(product.accessGroup())));

    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(6)
    @TmsLink("1687128")
    @DisplayName("UI GenericMonitoring. Изменить состав группы")
    void changeGroup() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.EQUALS, () -> genericMonitoringPage.updateGroup("user",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(7)
    @TmsLink("1687132")
    @DisplayName("UI GenericMonitoring. Изменить конфигурацию")
    void changeConfiguration() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.MORE, genericMonitoringPage::changeConfiguration);
    }

    @Test
    @Disabled("Действие отсутствует")
    @Order(8)
    @TmsLink("")
    //@EnabledIfEnv("blue")
    @DisplayName("UI GenericMonitoring. Создать снапшот")
    void сreateSnapshot() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.EQUALS, genericMonitoringPage::сreateSnapshot);
    }

    @Test
    @Disabled("Действие отсутствует")
    @Order(9)
    @TmsLink("")
    //@EnabledIfEnv("blue")
    @DisplayName("UI GenericMonitoring. Удалить снапшот")
    void deleteSnapshot() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.EQUALS, genericMonitoringPage::deleteSnapshot);
    }

    @Test
    @Disabled("Действие отсутствует")
    @Order(10)
    @TmsLink("1687129")
    @DisplayName("UI GenericMonitoring.  Реинвентаризация ВМ (Linux)")
    void reInventory() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.EQUALS, genericMonitoringPage::reInventory);
    }

    @Test
    @Order(11)
    @TmsLink("1687133")
    @DisplayName("UI GenericMonitoring. Мониторинг ОС")
    void monitoringOs() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.checkMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("1687137")
    @DisplayName("UI GenericMonitoring. Удаление продукта")
    void delete() {
        GenericMonitoringPage genericMonitoringPage = new GenericMonitoringPage(product);
        genericMonitoringPage.runActionWithCheckCost(CompareType.LESS, genericMonitoringPage::delete);
    }
}
