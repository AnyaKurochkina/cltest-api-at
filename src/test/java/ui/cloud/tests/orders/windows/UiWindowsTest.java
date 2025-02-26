package ui.cloud.tests.orders.windows;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Windows;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.*;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Graph;
import ui.elements.TypifiedElement;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("Windows")
@Tags({@Tag("ui"), @Tag("ui_windows")})
public class UiWindowsTest extends UiProductTest {
    private Windows product;
    //=Windows.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/all/orders/d83b7cd3-f4c0-4797-a922-6d3ab3bfe780/main?context=proj-iv550odo9a&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("872651")
    @Order(1)
    @DisplayName("UI Windows. Заказ")
    void orderWindows() {
        double prebillingCost;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectCategory("Базовые вычисления")
                    .expandProductsList()
                    .selectProduct(product.getProductName());
            WindowsOrderPage orderPage = new WindowsOrderPage();
            //orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getRoleServer().setContains("Autotests");
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            WindowsPage winPages = new WindowsPage(product);
            winPages.waitChangeStatus(Duration.ofMinutes(25));
            winPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        WindowsPage winPage = new WindowsPage(product);
        checkOrderCost(prebillingCost, winPage);
    }

    @Test
    @TmsLink("976726")
    @Order(2)
    @DisplayName("UI Windows. Проверка графа в истории действий")
    void checkHeaderHistoryTable() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getBtnGeneralInfo().click();
        winPage.checkHeadersHistory();
        winPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @TmsLinks({@TmsLink("1057146"), @TmsLink("1057141")})
    @Order(3)
    @DisplayName("UI Windows. Добавление/удаление группы доступа")
    void deleteGroup() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.deleteGroup("Administrators"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.addGroup("Administrators", Collections.singletonList(product.additionalAccessGroup())));
    }

    @Test
    @TmsLink("1057193")
    @Order(5)
    @DisplayName("UI Windows. Изменение группы доступа")
    void updateGroup() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.updateGroup("Administrators",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @TmsLink("2023")
    @Order(6)
    @DisplayName("UI Windows. Проверка 'Защита от удаления'")
    void checkProtectOrder() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.switchProtectOrder(true);
        try {
            winPage.runActionWithParameters("Виртуальная машина", "Удалить", "Удалить", () ->
            {
                Dialog dlgActions = Dialog.byTitle("Удаление");
                dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
            Alert.red("Заказ защищен от удаления");
            TypifiedElement.refreshPage();
        } finally {
            winPage.switchProtectOrder(false);
        }
    }

    @Test
    @Order(7)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, winPage::restart);
    }

    @Test
    @Order(8)
    @TmsLink("233926")
    @DisplayName("UI Windows. Расширить диск")
    void expandDisk() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("N", "15"));
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.expandDisk("N", "20"));
    }

    @Test
    @Order(17)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, winPage::changeConfiguration);
    }

    @Test
    @Order(10)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("T", "11"));
    }

    @Test
    @Order(11)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("S", "11"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("S"));
    }

    @Test
    @Order(12)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("R", "11"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("R"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.enableDisk("R"));
    }

    @Test
    @Order(13)
    @TmsLink("646056")
    @DisplayName("UI Windows. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("P", "11"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("P"));
        winPage.runActionWithCheckCost(CompareType.LESS, () -> winPage.deleteDisk("P"));
    }

    @Test
    @Order(14)
    @TmsLink("647426")
    @DisplayName("UI Windows. Проверить конфигурацию")
    void vmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, winPage::checkConfiguration);
    }

    @Disabled
    @Test
    @Order(15)
    @TmsLinks({@TmsLink("14485"), @TmsLink("247978")})
    @DisplayName("UI Windows. Выключить принудительно / Включить")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.LESS, winPage::stopHard);
        winPage.runActionWithCheckCost(CompareType.MORE, winPage::start);
    }

    @Disabled
    @Test
    @Order(16)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.LESS, winPage::stopSoft);
    }

    @Test
    @Order(17)
    @TmsLink("1171958")
    @DisplayName("UI Windows. Мониторинг ОС")
    void monitoringOs() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.checkMonitoringOs();
    }

    @Test
    @Order(18)
    @TmsLink("1418993")
    @DisplayName("UI Windows. Добавить ключ Астром")
    void addKeyAstrom() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.addKeyAstrom();
    }

    @Test
    @Order(19)
    @TmsLink("1419327")
    @DisplayName("UI Windows. Удалить ключ Астром")
    void deleteKeyAstrom() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.deleteKeyAstrom();
    }

    @Test
    @Order(20)
    @TmsLink("")
    @DisplayName("UI Windows. Обновить ОС")
    void updateOs() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.updateOs();
    }

    @Test
    @Order(100)
    @TmsLink("872683")
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.ZERO, winPage::delete);
    }
}
