package ui.cloud.tests.orders;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.time.Duration;

@ExtendWith(InterceptTestExtension.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_windows")})
@Log4j2
public class UiWindowsTest extends Tests {
    Windows product;

    //TODO: пока так :)
    public UiWindowsTest() {
        if (Configure.ENV.equals("prod"))
            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
//            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/vm/orders/761a5b34-ecfb-4033-ab66-a2a65cf205ec/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = Windows.builder().env("DSO").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        //Configuration.browserSize = "1366x768";
        new LoginPage(product.getProjectId())
                .signIn();
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
            new OrdersPage()
                    .getRowElementByColumnValue("Продукт",
                            orderPage.getLabelValue())
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
        Assertions.assertEquals(preBillingProductPrice, winPage.getCostOrder(), 0.01);
    }

    @Test
    @TmsLink("976726")
    @Order(2)
    @DisplayName("UI Windows. Проверка заголовка столбцов в Истории действий.")
    void checkHeaderHistoryTable() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click(ClickOptions.usingJavaScript());
        winPage.checkHeadersHistory();
    }

    @Test
    @TmsLink("976731")
    @Order(3)
    @DisplayName("UI Windows. Проверка элемента 'Схема выполнения'")
    void checkHistoryGraphScheme() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getBtnHistory().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        winPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new EntitiesUtils().checkGraphScheme();
    }

    @Test
    @TmsLink("2023")
    @Order(4)
    @DisplayName("UI Windows. Проверка 'Защита от удаления'")
    void checkProtectOrder() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.switchProtectOrder("Защита от удаления включена");
        winPage.runActionWithParameters("Виртуальная машина", "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
        new Alert().checkColor(Alert.Color.RED).checkText("Заказ защищен от удаления");
        Selenide.refresh();
        winPage.switchProtectOrder("Защита от удаления выключена");
    }

    @Test
    @Order(5)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, winPage::restart);
    }

    @Test
    @Order(6)
    @TmsLink("233926")
    @DisplayName("UI Windows. Расширить диск")
    void expandDisk() {
        Assumptions.assumeTrue("OpenStack".equals(product.getPlatform()), "Тест отключен для платформы OpenStack");
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("N", "15"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.expandDisk("N", "20"));
    }

    @Test
    @Order(7)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.LESS, winPage::stopHard);
        try {
            winPage.runActionWithCheckCost(CompareType.MORE, winPage::changeConfiguration);
        } finally {
            winPage.runActionWithCheckCost(CompareType.MORE, winPage::start);
        }
    }

    @Test
    @Order(8)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("T", "11"));
    }

    @Test
    @Order(9)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("S", "11"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("S"));
    }

    @Test
    @Order(10)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("R", "11"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("R"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.enableDisk("R"));
    }

    @Test
    @Order(11)
    @TmsLink("646056")
    @DisplayName("UI Windows. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("P", "11"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("P"));
        winPage.runActionWithCheckCost(CompareType.LESS, () -> winPage.deleteDisk("P"));
    }

    @Test
    @Order(12)
    @TmsLink("647426")
    @DisplayName("UI Windows. Проверить конфигурацию")
    void vmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, winPage::checkConfiguration);
    }

    @Test
    @Order(13)
    @TmsLinks({@TmsLink("14485"), @TmsLink("247978")})
    @DisplayName("UI Windows. Выключить принудительно / Включить")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.LESS, winPage::stopHard);
        winPage.runActionWithCheckCost(CompareType.MORE, winPage::start);
    }

    @Test
    @Order(14)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.LESS, winPage::stopSoft);
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
