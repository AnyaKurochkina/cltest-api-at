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
    Double prepriceOrderDbl;
    Double priceOrderDbl;
    String labelOrder;

    //TODO: пока так :)
    public UiWindowsTest() {
        if (Configure.ENV.equals("prod"))
            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
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
    @TmsLink("")
    @Order(1)
    @DisplayName("UI Windows. Проверка поля Количество VM до заказа продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        iProductPage.checkFieldVmNumber();
    }

    @Test
    @TmsLink("")
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
    @TmsLink("")
    @Order(3)
    @DisplayName("UI Windows. Проверка кнопки Заказать на неактивность, до заполнения полей")
    void checkBtnOrderDisabled() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        iProductPage.getOrderProduct().shouldBe(Condition.disabled);
    }

    @Test
    @TmsLink("")
    @Order(4)
    @DisplayName("UI Windows. Проверка атрибута \"textContent\" на содержание символа \"— ₽\"")
    void checkFieldAtrTextContentSymbol() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        Objects.requireNonNull(iProductPage.getOrderPricePerDay().getAttribute("textContent")).contains("— ₽");
    }

    @Test
    @TmsLink("")
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
    @TmsLink("")
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
        prepriceOrderDbl = iProductPage.convertToDblPriceOrder(iProductPage.getOrderPricePerDay().getAttribute("textContent"));
        orderPage.orderClick();
        labelOrder = orderPage.getLabel();
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
    @TmsLink("")
    @Order(8)
    @DisplayName("UI Windows. Проверка заголовка столбцов в Истории действий.")
    void checkHeaderHistoryTable() {
        new IndexPage().getBtnProducts().click();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        labelOrder)
                .hover()
                .click();
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        iProductPage.checkHeaderHistoryTable();
    }

    @Test
    @TmsLink("")
    @Order(9)
    @DisplayName("UI Windows. Проверка на наличие элемента Строка 'Развертывание' со статусом 'В порядке'.")
    void isHistoryRowDeployOk() {
        new IndexPage().getBtnProducts().click();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        labelOrder)
                .hover()
                .click();
        iProductPage.getActionHistory().shouldBe(Condition.enabled).click();
        iProductPage.getHistoryRowDeployOk().shouldBe(activeCnd);
    }

    @Test
    @TmsLink("")
    @Order(10)
    @DisplayName("UI Windows. Проверка на наличие элемента Строка 'Развертывание' со статусом 'Ошибка'.")
    void checkHistoryRowDeployErr() {
        new IndexPage().getBtnProducts().click();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        labelOrder)
                .hover()
                .click();
        iProductPage.getActionHistory().shouldBe(Condition.enabled).click();
        iProductPage.getHistoryRowDeployErr().shouldNotBe(Condition.visible);
    }

    @Test
    @TmsLink("")
    @Order(11)
    @DisplayName("UI Windows. Проверка наличия элемента \"Схема выполнения\".")
    void checkHistoryGraphScheme() {
        new IndexPage().getBtnProducts().click();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        labelOrder)
                .hover()
                .click();
        iProductPage.getActionHistory().shouldBe(Condition.enabled).click();
        iProductPage.getHistoryRow0().shouldHave(Condition.attributeMatching("title", "Просмотр схемы выполнения"));
        iProductPage.getHistoryRow0().shouldBe(Condition.enabled).click();
        iProductPage.getGraphScheme().shouldBe(Condition.visible);
        iProductPage.getCloseModalWindowButton().shouldBe(Condition.enabled).click();
    }

    @Test
    @TmsLink("")
    @Order(12)
    @DisplayName("UI Windows. Проверка стоимости продукта соответствию предбиллингу")
    void checkPrePriceOrder() {
        new IndexPage().getBtnProducts().click();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        labelOrder)
                .hover()
                .click();
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        iProductPage.getLoadOrderPricePerDayAfterOrder().shouldBe(Condition.enabled);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        iProductPage.getProgressBars().shouldBe(Condition.disappear);
        iProductPage.getOrderPricePerDayAfterOrder().shouldBe(Condition.visible);
        priceOrderDbl = iProductPage.convertToDblPriceOrder(iProductPage.getOrderPricePerDayAfterOrder().getAttribute("textContent"));
        Assertions.assertEquals(priceOrderDbl, prepriceOrderDbl);
    }


    @Test
    @Order(13)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    @SneakyThrows
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.restart();
        iProductPage.checkHistoryRowRestartByPowerOk();
        iProductPage.checkHistoryRowRestartByPowerErr();
    }

    @Test
    @Order(14)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.stopSoft();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "меньше");
        iProductPage.checkHistoryRowTurnOffOk();
        iProductPage.checkHistoryRowTurnOffErr();
    }


    @Test
    @Order(15)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.changeConfiguration();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "больше");
        iProductPage.checkHistoryRowChangeFlavorOk();
        iProductPage.checkHistoryRowChangeFlavorErr();
    }

    @Test
    @Order(16)
    @TmsLink("872682")
    @DisplayName("UI Windows. Включить")
    void start() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.start();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "больше");
        iProductPage.checkHistoryRowTurnOnOk();
        iProductPage.checkHistoryRowTurnOnErr();
    }

    @Test
    @Order(17)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.discActAdd();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "больше");
        iProductPage.checkHistoryRowDiscAddOk();
        iProductPage.checkHistoryRowDiscAddErr();
    }

    @Test
    @Order(18)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.discActOff();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "равна");
        iProductPage.checkHistoryRowDiscTurnOffOk();
        iProductPage.checkHistoryRowDiscTurnOffErr();
    }

    @Test
    @Order(19)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.discActOn();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "равна");
        iProductPage.checkHistoryRowDiscTurnOnOk();
        iProductPage.checkHistoryRowDiscTurnOnErr();
    }

    @Test
    @Order(20)
    @TmsLinks({@TmsLink("714872"), @TmsLink("646056")})
    @DisplayName("UI Windows. Отключить в ОС. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.discActOff();
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.discActDelete();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "равна");
        iProductPage.checkHistoryRowDiscDeleteOk();
        iProductPage.checkHistoryRowDiscDeleteErr();
    }

    @Test
    @Order(21)
    @TmsLink("647426")
    @DisplayName("UI Windows. Проверить конфигурацию")
    void vmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.vmActCheckConfig();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "равна");
        iProductPage.checkHistoryRowCheckConfigOk();
        iProductPage.checkHistoryRowCheckConfigErr();
    }

    @Test
    @Order(22)
    @TmsLink("14485")
    @DisplayName("UI Windows. Выключить принудительно")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.stopHard();
        iProductPage.vmOrderTextCompareByKey(currentCost, iProductPage.getCostAfterChange(), "равна");
        iProductPage.checkHistoryRowForceTurnOffOk();
        iProductPage.checkHistoryRowForceTurnOffErr();
    }


    @Test
    @Order(100)
    @TmsLink("872683")
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.delete();
        iProductPage.checkHistoryRowDeletedOk();
        iProductPage.checkHistoryRowDeletedErr();
    }

}
