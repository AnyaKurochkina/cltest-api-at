package ui.cloud.tests;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.Windows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.WindowsPage;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

@ExtendWith(InterceptTestExtension.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_windows")})
@Log4j2
public class UiWindowsTest extends Tests {

    Windows product;
    //    IProductPage iProductPage = new IProductPage() {
//    };
    double prePriceOrderDbl;
    double costAfterChange;
    double currentCost;

    //TODO: пока так :)
    public UiWindowsTest() {
        if (Configure.ENV.equals("prod"))
//            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/vm/orders/761a5b34-ecfb-4033-ab66-a2a65cf205ec/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = Windows.builder().env("DSO").platform("vSphere").segment("dev-srv-app").build();
//        product.init();
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
//        new IndexPage()
//                .clickOrderMore()
//                .selectProduct(product.getProductName());
//        WindowsOrderPage orderPage = new WindowsOrderPage();
//        orderPage.getOsVersion().select(product.getOsVersion());
//        orderPage.getSegment().selectByValue(product.getSegment());
//        orderPage.getPlatform().selectByValue(product.getPlatform());
//        orderPage.getRoleServer().selectByValue(product.getRole());
//        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
//        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
//        orderPage.getGroup().select(accessGroup.getPrefixName());
//        iProductPage.getOrderBtn().shouldBe(Condition.visible);
//        iProductPage.getLoadOrderPricePerDay().shouldBe(Condition.enabled);
//        iProductPage.getLoadOrderPricePerDay().shouldBe(Condition.disappear);
//        prePriceOrderDbl = iProductPage.convertToDblPriceOrder(iProductPage.getOrderPricePerDay().getAttribute("textContent"));
//        orderPage.orderClick();
//        new ProductsPage()
//                .getRowByColumn("Продукт",
//                        orderPage.getLabel())
//                .hover()
//                .click();
//        WindowsPage winPages = new WindowsPage(product);
//
//        winPages.waitChangeStatus();
//        winPages.checkLastAction();
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
    @Order(5)
    @DisplayName("UI Windows. Проверка элемента 'Схема выполнения'")
    void checkHistoryGraphScheme() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getActionHistory().shouldBe(Condition.enabled).click();
        winPage.getHistoryRow0().shouldHave(Condition.attributeMatching("title", "Просмотр схемы выполнения"));
        winPage.getHistoryRow0().shouldBe(Condition.enabled).click();
        winPage.getGraphScheme().shouldBe(Condition.visible);
        winPage.getCloseModalWindowButton().shouldBe(Condition.enabled).click();
    }

    @Test
    @TmsLink("976732")
    @Order(6)
    @DisplayName("UI Windows. Проверка стоимости продукта соответствию предбиллингу")
    void checkPrePriceOrder() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getCurrentPriceOrder().shouldBe(Condition.enabled);
        winPage.getProgressBars().shouldBe(Condition.disappear);
        winPage.getOrderPricePerDayAfterOrder().shouldBe(Condition.visible);
        currentCost = winPage.getCostOrder();
        Assertions.assertEquals(currentCost, prePriceOrderDbl);
    }

    @Test
    @Order(7)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    @SneakyThrows
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, winPage::restart);
    }

    @Test
    @Order(9)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.LESS, winPage::stopSoft);
        winPage.runActionWithCheckCost(CompareType.MORE, winPage::start);
    }

    @Test
    @Order(11)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
//        winPage.runActionWithCheckCost(CompareType.LESS, winPage::stopHard);
        try {
            winPage.runActionWithCheckCost(CompareType.MORE, winPage::changeConfiguration);
        } finally {
            winPage.runActionWithCheckCost(CompareType.LESS, winPage::start);
        }
    }

    @Test
    @Order(15)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("T"));
    }

    @Test
    @Order(17)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("S"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("S"));
    }

    @Test
    @Order(19)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("R"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("R"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.enableDisk("R"));
    }


    @Test
    @Order(21)
    @TmsLink("646056")
    @DisplayName("UI Windows. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.MORE, () -> winPage.addDisk("P"));
        winPage.runActionWithCheckCost(CompareType.EQUALS, () -> winPage.disableDisk("P"));
        winPage.runActionWithCheckCost(CompareType.LESS, () -> winPage.deleteDisk("P"));
    }


    @Test
    @Order(23)
    @TmsLink("647426")
    @DisplayName("UI Windows. Проверить конфигурацию")
    void vmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.EQUALS, winPage::checkConfiguration);
    }

    @Test
    @Order(25)
    @TmsLinks({@TmsLink("14485"), @TmsLink("872682")})
    @DisplayName("UI Windows. Выключить принудительно / Включить")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.runActionWithCheckCost(CompareType.LESS, winPage::stopHard);
        winPage.runActionWithCheckCost(CompareType.MORE, winPage::start);
    }

    @Test
    @Order(100)
    @TmsLink("872683")
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() {
//        WindowsPage winPage = new WindowsPage(product);
//        winPage.delete();
    }

}
