package ui.cloud.tests.orders.windows;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Windows;
import models.cloud.portalBack.AccessGroup;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Graph;
import ui.elements.TypifiedElement;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

@Epic("UI Продукты")
@Feature("Windows")
@Tags({@Tag("ui"), @Tag("ui_windows")})
public class UiWindowsTest extends UiProductTest {
    Windows product;
    // product = Windows.builder().platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/compute/orders/8f8ca2bb-242a-46dc-8699-09f5c7fb373f/main?context=proj-ln4zg69jek&type=project&org=vtb").build().buildFromLink()

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("872651")
    @Order(1)
    @DisplayName("UI Windows. Заказ")
    void orderWindows() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            WindowsOrderPage orderPage = new WindowsOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            orderPage.getRoleServer().selectByValue(product.getRole());
            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            orderPage.getGroup().select(accessGroup.getPrefixName());
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = IProductPage.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new Alert().checkColor(Alert.Color.GREEN).checkText("Заказ успешно создан");
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
        Assertions.assertEquals(preBillingProductPrice, winPage.getCostOrder(), 0.01, "Стоимость заказа отличается от стоимости предбиллинга");
    }

    @Test
    @TmsLink("976726")
    @Order(2)
    @DisplayName("UI Windows. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        winPage.checkHeadersHistory();
        winPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @TmsLinks({@TmsLink("1057146"), @TmsLink("1057141")})
    @Order(3)
    @DisplayName("UI Windows. Добавление/удаление группы доступа")
    void deleteGroup() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.deleteGroup("Administrators"));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.addGroup("Administrators", Collections.singletonList(accessGroup.getPrefixName())));
    }

    @Test
    @TmsLink("1057193")
    @Order(5)
    @DisplayName("UI Windows. Изменение группы доступа")
    void updateGroup() {
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.updateGroup("Administrators",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @TmsLink("2023")
    @Order(6)
    @DisplayName("UI Windows. Проверка 'Защита от удаления'")
    void checkProtectOrder() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.switchProtectOrder("Защита от удаления включена");
        try {
            winPage.runActionWithParameters("Виртуальная машина", "Удалить", "Удалить", () ->
            {
                Dialog dlgActions = new Dialog("Удаление");
                dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
            new Alert().checkColor(Alert.Color.RED).checkText("Заказ защищен от удаления").close();
            TypifiedElement.refresh();
        } finally {
            winPage.switchProtectOrder("Защита от удаления выключена");
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
        Assumptions.assumeFalse("OpenStack".equals(product.getPlatform()), "Тест отключен для платформы OpenStack");
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
    @Order(100)
    @TmsLink("872683")
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.LESS, winPage::delete);
    }

}
