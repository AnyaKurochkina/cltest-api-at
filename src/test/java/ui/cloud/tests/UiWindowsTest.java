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
    IProductPage iProductPage = new IProductPage() {
    };
    Double prepriceOrderDbl;
    Double priceOrderDbl;

    //TODO: пока так :)
    public UiWindowsTest() {
        product = product.buildFromLink("https://prod-portal-front.cloud.vtb.ru/vm/orders/07d858dc-3bcf-488f-bd57-a528bb142c77/main?context=proj-frybyv41jh&type=project&org=vtb");
//        if (Configure.ENV.equals("prod"))
//            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
//        else
//            product = Windows.builder().env("DSO").platform("vSphere").segment("dev-srv-app").build();
//        product.init();
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
        iProductPage.getMark().sendKeys(CONTROL + "a");
        iProductPage.getMark().sendKeys(BACK_SPACE);
        $(byText("Поле должно содержать от 3 до 64 символов")).should(Condition.exist);
        log.info("Проверка поля метка должно содержать от 3 до 64 символов");
        iProductPage.getOrderProduct().shouldBe(Condition.disabled);
        log.info("Проверка кнопки \"Заказать\" до заполнения полей");
        iProductPage.getOrderPricePerDay().getAttribute("textContent").contains("— ₽");
        log.info("Проверка атрибута \"textContent\" на содержание символа \"— ₽\"");
        iProductPage.checkFieldVmNumber();
        WindowsOrderPage orderPage = new WindowsOrderPage();
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getRoleServer().selectByValue(product.getRole());
        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        iProductPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
        iProductPage.getLoadOrderPricePerDay().shouldBe(Condition.disappear);
        //пользователь проверяет, что у элемента "Стоимость в сутки" атрибут "textContent" содержит значение "≈"
        iProductPage.isCostDayContains("≈");
        //пользователь проверяет детали заказа
        iProductPage.checkOrderDetails(iProductPage.getCalculationDetails(), "Windows Server");
        //получает стоимосить на предбиллинге
        iProductPage.getOrderBtn().shouldBe(Condition.visible);
        String prepriceStr = iProductPage.getOrderPricePerDay().getAttribute("textContent");
        prepriceOrderDbl = iProductPage.getNumbersFromText(prepriceStr);
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
        iProductPage.checkHeaderHistoryTable();
        log.info("пользователь проверяет, что на вкладке История действий таблица содержит необходимые столбцы");
        iProductPage.checkHistoryRowDeployOk();
        iProductPage.checkHistoryRowDeployErr();
        iProductPage.getActionHistory().shouldBe(Condition.enabled).click();
        iProductPage.getHistoryRow0().shouldHave(Condition.attributeMatching("title","Просмотр схемы выполнения"));
        log.info("пользователь проверяет, что на странице присутствует текст \"Просмотр схемы выполнения\"");
        iProductPage.getHistoryRow0().shouldBe(Condition.enabled).click();
        iProductPage.getGraphScheme().shouldBe(Condition.visible);
        log.info("пользователь проверяет наличие элемента \"Схема выполнения\"");
        iProductPage.getCloseModalWindowButton().shouldBe(Condition.enabled).click();
      //пользователь проверяет, что стоимость продукта соответствует предбиллингу
      //iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        iProductPage.getLoadOrderPricePerDayAfterOrder().shouldBe(Condition.enabled);
      //iProductPage.getLoadOrderPricePerDayAfterOrder().shouldBe(Condition.disappear);
      //iProductPage.getOrderPricePerDayAfterOrder().shouldBe(Condition.visible);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        iProductPage.getProgressBars().shouldBe(Condition.disappear);
        iProductPage.getOrderPricePerDayAfterOrder().shouldBe(Condition.visible);
        String priceStr = iProductPage.getOrderPricePerDayAfterOrder().getAttribute("textContent");
        priceOrderDbl = iProductPage.getNumbersFromText(priceStr);
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
        iProductPage.checkHistoryRowRestartByPowerOk();
        iProductPage.checkHistoryRowRestartByPowerErr();
    }

    @Test
    @Order(3)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.stopSoft();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"меньше");
        iProductPage.checkHistoryRowTurnOffOk();
        iProductPage.checkHistoryRowTurnOffErr();
    }


    @Test
    @Order(4)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.changeConfiguration();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"больше");
        iProductPage.checkHistoryRowChangeFlavorOk();
        iProductPage.checkHistoryRowChangeFlavorErr();
    }

    @Test
    @Order(5)
    @TmsLink("872682")
    @DisplayName("UI Windows. Включить")
    void start() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.start();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"больше");
        iProductPage.checkHistoryRowTurnOnOk();
        iProductPage.checkHistoryRowTurnOnErr();
    }

    @Test
    @Order(6)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.discActAdd();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"больше");
        iProductPage.checkHistoryRowDiscAddOk();
        iProductPage.checkHistoryRowDiscAddErr();
    }

    @Test
    @Order(10)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.discActOn();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"равна");
        iProductPage.checkHistoryRowDiscTurnOnOk();
        iProductPage.checkHistoryRowDiscTurnOnErr();
    }


    @Test
    @Order(9)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);

        double currentCost = iProductPage.getCurrentCost();
        winPage.discActOff();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"равна");
        iProductPage.checkHistoryRowDiscTurnOffOk();
        iProductPage.checkHistoryRowDiscTurnOffErr();
    }


    @Test
    @Order(11)
    @TmsLinks({@TmsLink("714872"),@TmsLink("646056")})
    @DisplayName("UI Windows. Отключить в ОС. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.discActOff();
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.discActDelete();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"меньше");
        iProductPage.checkHistoryRowDiscDeleteOk();
        iProductPage.checkHistoryRowDiscDeleteErr();
    }

    @Test
    @Order(12)
    //@TmsLink("647426")
    @DisplayName("UI Windows. Проверить конфигурацию")
    void vmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.vmActCheckConfig();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"равна");
        iProductPage.checkHistoryRowCheckConfigOk();
        iProductPage.checkHistoryRowCheckConfigErr();
    }

    @Test
    @Order(13)
    @TmsLink("14485")
    @DisplayName("UI Windows. Выключить принудительно")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        iProductPage.getBtnGeneralInfo().shouldBe(Condition.enabled);
        double currentCost = iProductPage.getCurrentCost();
        winPage.stopHard();
        iProductPage.vmOrderTextCompareByKey(currentCost,iProductPage.getCostAfterChange(),"меньше");
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
