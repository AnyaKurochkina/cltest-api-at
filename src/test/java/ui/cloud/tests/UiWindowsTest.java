package ui.cloud.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
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
          product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://cloud.vtb.ru/vm/orders/cdf5702c-e512-488a-9d07-d9832d123dd6/main?context=proj-frybyv41jh&type=project&org=vtb").build();
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
        winPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        winPage.checkHeaderHistoryTable();
    }

    @Test
    @TmsLink("976729")
    @Order(3)
    @DisplayName("UI Windows. Проверка на наличие элемента Строка 'Развертывание' со статусом 'В порядке'.")
    void isHistoryRowDeployOk() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getActionHistory().shouldBe(Condition.enabled).click();
        winPage.getHistoryRowDeployOk().shouldBe(activeCnd);
    }

    @Test
    @TmsLink("976730")
    @Order(4)
    @DisplayName("UI Windows. Проверка на наличие элемента Строка 'Развертывание' со статусом 'Ошибка'.")
    void checkHistoryRowDeployErr() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getActionHistory().shouldBe(Condition.enabled).click();
        winPage.getHistoryRowDeployErr().shouldNotBe(Condition.visible);
    }

    @Test
    @TmsLink("976731")
    @Order(5)
    @DisplayName("UI Windows. Проверка наличия элемента \"Схема выполнения\".")
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
        winPage.getLoadOrderPricePerDayAfterOrder().shouldBe(Condition.enabled);
        winPage.getProgressBars().shouldBe(Condition.disappear);
        winPage.getOrderPricePerDayAfterOrder().shouldBe(Condition.visible);
        currentCost = winPage.getCurrentCostReloadPage(product);
        Assertions.assertEquals(currentCost, prePriceOrderDbl);
    }

    @Test
    @Order(7)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    @SneakyThrows
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.restart();
        winPage.checkHistoryRowRestartByPowerOk();
        winPage.checkHistoryRowRestartByPowerErr();
    }

    @Test
    @Order(8)
    @TmsLink("976734")
    @DisplayName("UI Windows. Проверка стоимости после действия Перезагрузить по питанию")
    @SneakyThrows
    void checkCostAfterRestart() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(9)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.stopSoft();
        winPage.checkHistoryRowTurnOffOk();
        winPage.checkHistoryRowTurnOffErr();
    }

    @Test
    @Order(10)
    @TmsLink("976738")
    @DisplayName("UI Windows. Проверка стоимости после действия Выключить")
    void checkCostAfterStopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "больше");
    }

    @Test
    @Order(11)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.changeConfiguration();
        winPage.checkHistoryRowChangeFlavorOk();
        winPage.checkHistoryRowChangeFlavorErr();
    }

    @Test
    @Order(12)
    @TmsLink("976742")
    @DisplayName("UI Windows. Проверка стоимости после действия Изменить конфигурацию")
    void checkCostAfterChangeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(costAfterChange, currentCost, ",больше");
    }

    @Test
    @Order(13)
    @TmsLink("872682")
    @DisplayName("UI Windows. Включить")
    void start() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.start();
        winPage.checkHistoryRowTurnOnOk();
        winPage.checkHistoryRowTurnOnErr();
    }

    @Test
    @Order(14)
    @TmsLink("976743")
    @DisplayName("UI Windows. Проверка стоимости после действия Включить")
    void checkStartAfterStart() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(costAfterChange, currentCost, "больше");
    }

    @Test
    @Order(15)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.discActAdd();
        winPage.checkHistoryRowDiscAddOk();
        winPage.checkHistoryRowDiscAddErr();
    }

    @Test
    @Order(16)
    @TmsLink("976746")
    @DisplayName("UI Windows. Проверка стоимости после действия Добавить диск")
    void checkCostAfterDiscActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(costAfterChange, currentCost, "больше");
    }

    @Test
    @Order(17)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.discActOff();
        winPage.checkHistoryRowDiscTurnOffOk();
        winPage.checkHistoryRowDiscTurnOffErr();
    }

    @Test
    @Order(18)
    @TmsLink("976747")
    @DisplayName("UI Windows. Проверка стоимости после действия Отключить в ОС")
    void checkCostAfterTurnOffOs() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(19)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.discActOn();
        winPage.checkHistoryRowDiscTurnOnOk();
        winPage.checkHistoryRowDiscTurnOnErr();
    }

    @Test
    @Order(20)
    @TmsLink("976749")
    @DisplayName("UI Windows. Проверка стоимости после действия Подключить в ОС")
    void checkCostAfterConnectOS() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(21)
    @TmsLinks({@TmsLink("714872"), @TmsLink("646056")})
    @DisplayName("UI Windows. Отключить в ОС. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.discActOff();
        winPage.discActDelete();
        winPage.checkHistoryRowDiscDeleteOk();
        winPage.checkHistoryRowDiscDeleteErr();
    }

    @Test
    @Order(22)
    @TmsLink("976752")
    @DisplayName("UI Windows. Проверка стоимости после действия Удалить диск")
    void checkCostAfterDdiscActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(23)
    @TmsLink("647426")
    @DisplayName("UI Windows. Проверить конфигурацию")
    void vmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.vmActCheckConfig();
        winPage.checkHistoryRowCheckConfigOk();
        winPage.checkHistoryRowCheckConfigErr();
    }

    @Test
    @Order(24)
    @TmsLink("976756")
    @DisplayName("UI Windows. Проверка стоимости после действия Проверить конфигурацию")
    void checkCostAfterVmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(25)
    @TmsLink("14485")
    @DisplayName("UI Windows. Выключить принудительно")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost = winPage.getCurrentCostReloadPage(product);
        winPage.stopHard();
        winPage.checkHistoryRowForceTurnOffOk();
        winPage.checkHistoryRowForceTurnOffErr();
    }

    @Test
    @Order(26)
    @TmsLink("976760")
    @DisplayName("UI Windows. Проверка стоимости после действия Выключить принудительно")
    void checkCostAfterStopHard() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "больше");
    }

    @Test
    @Order(100)
    @TmsLink("872683")
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() {
//        WindowsPage winPage = new WindowsPage(product);
//        winPage.delete();
//        winPage.checkHistoryRowDeletedOk();
//        winPage.checkHistoryRowDeletedErr();
    }

}
