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

import java.util.Objects;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.CONTROL;


@ExtendWith(InterceptTestExtension.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_windows")})
@Log4j2

public class UiWindowsTest extends Tests {

    Windows product;
    IProductPage iProductPage = new IProductPage() {
    };
    Double prePriceOrderDbl;
    Double priceOrderDbl;
    //  String labelOrder = "a-9707-05f0c2f751fc";
    double costAfterChange;
    double currentCost;

    //TODO: пока так :)
    public UiWindowsTest() {
        if (Configure.ENV.equals("prod"))
            // product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/vm/orders/8a37e2a0-1b2c-449e-9f7c-9dfe037a5f03/main?context=proj-frybyv41jh&type=project&org=vtb").build();
        else
            product = Windows.builder().env("DSO").platform("vSphere").segment("dev-srv-app").build();
        product.init();

    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        Configuration.browserSize = "1366x768";
        new LoginPage(product.getProjectId())
                .singIn();
    }

    @Test
    @TmsLink("975914")
    @Order(1)
    @DisplayName("UI Windows. Проверка поля Количество VM до заказа продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        iProductPage.checkFieldVmNumber();
    }

    @Test
    @TmsLink("976622")
    @Order(2)
    @DisplayName("UI Windows. Проверка поля Метка до заказа продукта")
    void checkFieldMark() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        iProductPage.getMark().sendKeys(CONTROL + "a");
        iProductPage.getMark().sendKeys(BACK_SPACE);
        $(byText("Поле должно содержать от 3 до 64 символов")).should(Condition.exist);
        log.info("Проверка поля метка должно содержать от 3 до 64 символов");
    }

    @Test
    @TmsLink("976626")
    @Order(3)
    @DisplayName("UI Windows. Проверка кнопки Заказать на неактивность, до заполнения полей")
    void checkBtnOrderDisabled() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        iProductPage.getOrderProduct().shouldBe(Condition.disabled);
    }

    @Test
    @TmsLink("976629")
    @Order(4)
    @DisplayName("UI Windows. Проверка атрибута \"textContent\" на содержание символа \"— ₽\"")
    void checkFieldAtrTextContentSymbol() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        Objects.requireNonNull(iProductPage.getOrderPricePerDay().getAttribute("textContent")).contains("— ₽");
    }

    @Test
    @TmsLink("976722")
    @Order(5)
    @DisplayName("UI Windows. Проверка у элемента \"Стоимость в сутки\" атрибут \"textContent\" содержит значение \"≈\"")
    void checkElementAtrTextContentSymbol() {
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
        iProductPage.isCostDayContains("≈");
    }

    @Test
    @TmsLink("976724")
    @Order(6)
    @DisplayName("UI Windows. Проверка Детали заказа.")
    void checkDetailsOrder() {
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
        iProductPage.checkOrderDetails(iProductPage.getCalculationDetails(), product.getProductName());
    }

    @Test
    @TmsLink("872651")
    @Order(7)
    @DisplayName("UI Windows. Заказ")
    void orderWindows() {
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
        //получает стоимосить на предбиллинге
        iProductPage.getOrderBtn().shouldBe(Condition.visible);
        iProductPage.getLoadOrderPricePerDay().shouldBe(Condition.enabled);
        iProductPage.getLoadOrderPricePerDay().shouldBe(Condition.disappear);
        prePriceOrderDbl = iProductPage.convertToDblPriceOrder(iProductPage.getOrderPricePerDay().getAttribute("textContent"));
        orderPage.orderClick();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        orderPage.getLabel())
                .hover()
                .click();
        WindowsPage winPage = new WindowsPage(product);
        winPage.waitChangeStatus();
        winPage.checkLastAction();
    }

    @Test
    @TmsLink("976726")
    @Order(8)
    @DisplayName("UI Windows. Проверка заголовка столбцов в Истории действий.")
    void checkHeaderHistoryTable() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        winPage.checkHeaderHistoryTable();
    }

    @Test
    @TmsLink("976729")
    @Order(9)
    @DisplayName("UI Windows. Проверка на наличие элемента Строка 'Развертывание' со статусом 'В порядке'.")
    void isHistoryRowDeployOk() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getActionHistory().shouldBe(Condition.enabled).click();
        winPage.getHistoryRowDeployOk().shouldBe(activeCnd);
    }

    @Test
    @TmsLink("976730")
    @Order(10)
    @DisplayName("UI Windows. Проверка на наличие элемента Строка 'Развертывание' со статусом 'Ошибка'.")
    void checkHistoryRowDeployErr() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getActionHistory().shouldBe(Condition.enabled).click();
        winPage.getHistoryRowDeployErr().shouldNotBe(Condition.visible);
    }

    @Test
    @TmsLink("976731")
    @Order(11)
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
    @Order(12)
    @DisplayName("UI Windows. Проверка стоимости продукта соответствию предбиллингу")
    void checkPrePriceOrder() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getLoadOrderPricePerDayAfterOrder().shouldBe(Condition.enabled);
        winPage.getProgressBars().shouldBe(Condition.disappear);
        winPage.getOrderPricePerDayAfterOrder().shouldBe(Condition.visible);
        priceOrderDbl = winPage.convertToDblPriceOrder(winPage.getOrderPricePerDayAfterOrder().getAttribute("textContent"));
        Assertions.assertEquals(priceOrderDbl, prePriceOrderDbl);
    }


    @Test
    @Order(13)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    @SneakyThrows
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.restart();
        winPage.checkHistoryRowRestartByPowerOk();
        winPage.checkHistoryRowRestartByPowerErr();
    }

    @Test
    @Order(14)
    @TmsLink("976734")
    @DisplayName("UI Windows. Проверка стоимости после действия Перезагрузить по питанию")
    @SneakyThrows
    void checkCostAfterRestart() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange =winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }



    @Test
    @Order(15)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.stopSoft();
        winPage.checkHistoryRowTurnOffOk();
        winPage.checkHistoryRowTurnOffErr();
    }

    @Test
    @Order(16)
    @TmsLink("976738")
    @DisplayName("UI Windows. Проверка стоимости после действия Выключить")
    void checkCostAfterStopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange =winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "больше");
    }

    @Test
    @Order(17)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.changeConfiguration();
        winPage.checkHistoryRowChangeFlavorOk();
        winPage.checkHistoryRowChangeFlavorErr();
    }

    @Test
    @Order(18)
    @TmsLink("976742")
    @DisplayName("UI Windows. Проверка стоимости после действия Изменить конфигурацию")
    void checkCostAfterChangeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange =winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "больше");
    }

    @Test
    @Order(19)
    @TmsLink("872682")
    @DisplayName("UI Windows. Включить")
    void start() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.start();
        winPage.checkHistoryRowTurnOnOk();
        winPage.checkHistoryRowTurnOnErr();
    }

    @Test
    @Order(20)
    @TmsLink("976743")
    @DisplayName("UI Windows. Проверка стоимости после действия Включить")
    void checkStartAfterStart() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange =winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "больше");
    }

    @Test
    @Order(21)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.discActAdd();
        winPage.checkHistoryRowDiscAddOk();
        winPage.checkHistoryRowDiscAddErr();
    }

    @Test
    @Order(22)
    @TmsLink("976746")
    @DisplayName("UI Windows. Проверка стоимости после действия Добавить диск")
    void checkCostAfterDiscActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange =winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "больше");
    }

    @Test
    @Order(23)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.discActOff();
        winPage.checkHistoryRowDiscTurnOffOk();
        winPage.checkHistoryRowDiscTurnOffErr();
    }

    @Test
    @Order(24)
    @TmsLink("976747")
    @DisplayName("UI Windows. Проверка стоимости после действия Отключить в ОС")
    void checkCostAfterTurnOffOs() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange =winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(25)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.discActOn();
        winPage.checkHistoryRowDiscTurnOnOk();
        winPage.checkHistoryRowDiscTurnOnErr();
    }

    @Test
    @Order(26)
    @TmsLink("976749")
    @DisplayName("UI Windows. Проверка стоимости после действия Подключить в ОС")
    void checkCostAfterConnectOS() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange =winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(27)
    @TmsLinks({@TmsLink("714872"), @TmsLink("646056")})
    @DisplayName("UI Windows. Отключить в ОС. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.discActDelete();
        winPage.checkHistoryRowDiscDeleteOk();
        winPage.checkHistoryRowDiscDeleteErr();
    }

    @Test
    @Order(28)
    @TmsLink("976752")
    @DisplayName("UI Windows. Проверка стоимости после действия Удалить диск")
    void checkCostAfterDdiscActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange =winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(29)
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
    @Order(30)
    @TmsLink("976756")
    @DisplayName("UI Windows. Проверка стоимости после действия Проверить конфигурацию")
    void checkCostAfterVmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        costAfterChange = winPage.getCostAfterChangeReloadPage(product);
        winPage.vmOrderTextCompareByKey(currentCost, costAfterChange, "равна");
    }

    @Test
    @Order(31)
    @TmsLink("14485")
    @DisplayName("UI Windows. Выключить принудительно")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        currentCost=winPage.getCurrentCostReloadPage(product);
        winPage.stopHard();
        winPage.checkHistoryRowForceTurnOffOk();
        winPage.checkHistoryRowForceTurnOffErr();
    }

    @Test
    @Order(32)
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
        WindowsPage winPage = new WindowsPage(product);
        winPage.delete();
        winPage.checkHistoryRowDeletedOk();
        winPage.checkHistoryRowDeletedErr();
    }

}
