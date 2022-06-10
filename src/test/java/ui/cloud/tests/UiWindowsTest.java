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

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
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
    CommonChecks commonChecks = new CommonChecks();
    Double prepriceOrderDbl;
    Double priceOrderDbl;

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
        Configuration.browserSize="1366x768";
        new LoginPage(product.getProjectId())
                .singIn();
    }
    @Test
    @TmsLink("872651")
    @Order(1)
    @DisplayName("UI Windows. Заказ")
    @SneakyThrows
    void orderWindows() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        // Проверки полей до заказа
        commonChecks.getMark().sendKeys(CONTROL + "a");
        commonChecks.getMark().sendKeys(BACK_SPACE);
        $(byText("Поле должно содержать от 3 до 64 символов")).should(Condition.exist);
        log.info("Проверка поля метка должно содержать от 3 до 64 символов");
        commonChecks.getOrderProduct().shouldBe(Condition.disabled);
        log.info("Проверка кнопки \"Заказать\" до заполнения полей");
        commonChecks.getOrderPricePerDay().getAttribute("textContent").contains("— ₽");
        log.info("Проверка атрибута \"textContent\" на содержание символа \"— ₽\"");
        commonChecks.checkFieldVmNumber();
        WindowsOrderPage orderPage = new WindowsOrderPage();
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getRoleServer().selectByValue(product.getRole());
        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());//"cloud-zorg-group3"
        commonChecks.getLoadOrderPricePerDay().shouldBe(Condition.visible);
        commonChecks.getLoadOrderPricePerDay().shouldBe(Condition.disappear);
        //пользователь проверяет, что у элемента "Стоимость в сутки" атрибут "textContent" содержит значение "≈"
        commonChecks.isCostDayContains("≈");
        //пользователь проверяет детали заказа
        commonChecks.checkOrderDetails(commonChecks.getCalculationDetails(), "Windows Server");
        //получает стоимосить на предбиллинге
        commonChecks.getOrderBtn().shouldBe(Condition.visible);
        String prepriceStr = commonChecks.getOrderPricePerDay().getAttribute("textContent");
        prepriceOrderDbl = CommonChecks.getNumbersFromText(prepriceStr);
        orderPage.orderClick();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        orderPage.getLabel())
                .hover()
                .click();
        WindowsPage winPage = new WindowsPage(product);
        winPage.waitChangeStatus();
        winPage.checkLastAction();
        // Проверки после заказа продукта
        commonChecks.checkHeaderHistoryTable();
        commonChecks.checkHistoryRowDeployOk();
        commonChecks.checkHistoryRowDeployErr();
        commonChecks.getActionHistory().shouldBe(Condition.enabled).click();
        commonChecks.getHistoryRow0().shouldHave(Condition.attributeMatching("title","Просмотр схемы выполнения"));
        log.info("пользователь проверяет, что на странице присутствует текст \"Просмотр схемы выполнения\"");
        commonChecks.getHistoryRow0().shouldBe(Condition.enabled).click();
        commonChecks.getGraphScheme().shouldBe(Condition.visible);
        log.info("пользователь проверяет наличие элемента \"Схема выполнения\"");
        commonChecks.getCloseModalWindowButton().shouldBe(Condition.enabled).click();
        //пользователь проверяет, что стоимость продукта соответствует предбиллингу
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        commonChecks.getOrderPricePerDayAfterOrder().shouldBe(Condition.visible);
        String priceStr = commonChecks.getOrderPricePerDayAfterOrder().getAttribute("textContent");
        priceOrderDbl = CommonChecks.getNumbersFromText(priceStr);
        Assertions.assertEquals(priceOrderDbl, prepriceOrderDbl);
    }

    @Test
    @Order(2)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    @SneakyThrows
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.restart();
        commonChecks.checkHistoryRowRestartByPowerOk();
        commonChecks.checkHistoryRowRestartByPowerErr();
    }

    @Test
    @Order(3)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.stopSoft();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"меньше");
        commonChecks.checkHistoryRowTurnOffOk();
        commonChecks.checkHistoryRowTurnOffErr();
    }


    @Test
    @Order(4)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.changeConfiguration();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"больше");
        commonChecks.checkHistoryRowChangeFlavorOk();
        commonChecks.checkHistoryRowChangeFlavorErr();
    }

    @Test
    @Order(5)
    @TmsLink("872682")
    @DisplayName("UI Windows. Включить")
    void start() {
        WindowsPage winPage = new WindowsPage(product);
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.start();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"больше");
        commonChecks.checkHistoryRowTurnOnOk();
        commonChecks.checkHistoryRowTurnOnErr();
    }

    @Test
    @Order(6)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.discActAdd();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"больше");
        commonChecks.checkHistoryRowDiscAddOk();
        commonChecks.checkHistoryRowDiscAddErr();
    }

    @Test
    @Order(10)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.discActOn();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"равна");
        commonChecks.checkHistoryRowDiscTurnOnOk();
        commonChecks.checkHistoryRowDiscTurnOnErr();
    }


    @Test
    @Order(9)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() {
        WindowsPage winPage = new WindowsPage(product);
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.discActOff();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"равна");
        commonChecks.checkHistoryRowDiscTurnOffOk();
        commonChecks.checkHistoryRowDiscTurnOffErr();
    }


    @Test
    @Order(11)
    @TmsLinks({@TmsLink("714872"),@TmsLink("646056")})
    @DisplayName("UI Windows. Отключить в ОС. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.discActOff();
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.discActDelete();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"меньше");
        commonChecks.checkHistoryRowDiscDeleteOk();
        commonChecks.checkHistoryRowDiscDeleteErr();
    }

    @Test
    @Order(12)
    //@TmsLink("647426")
    @DisplayName("UI Windows. Проверить конфигурацию")
    void vmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.vmActCheckConfig();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"равна");
        commonChecks.checkHistoryRowCheckConfigOk();
        commonChecks.checkHistoryRowCheckConfigErr();
    }

    @Test
    @Order(13)
    @TmsLink("14485")
    @DisplayName("UI Windows. Выключить принудительно")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        commonChecks.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        double currentCost = commonChecks.getCurrentCost();
        winPage.stopHard();
        commonChecks.vmOrderTextCompareByKey(currentCost,commonChecks.getCostAfterChange(),"меньше");
        commonChecks.checkHistoryRowForceTurnOffOk();
        commonChecks.checkHistoryRowForceTurnOffErr();
    }


    @Test
    @Order(100)
    @TmsLink("872683")
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.delete();
        commonChecks.checkHistoryRowDeletedOk();
        commonChecks.checkHistoryRowDeletedErr();
    }

}
