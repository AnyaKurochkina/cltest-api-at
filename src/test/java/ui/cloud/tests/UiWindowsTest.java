package ui.cloud.tests;

import com.codeborne.selenide.Condition;
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
    CommonChecks commonChecks = new CommonChecks();
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
        new LoginPage(product.getProjectId())
                .singIn();
    }

    @Test
    @TmsLink("872651")
    @Order(1)
    @DisplayName("UI Windows. Заказ")
    void orderWindows() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsOrderPage orderPage = new WindowsOrderPage();
//        //Проверки полей до заказа
//        commonChecks.getMark().clear();
//        commonChecks.getOrderProduct().shouldBe(Condition.disabled);
//        log.info("Проверка кнопки \"Заказать\" до заполнения полей");
//        commonChecks.getOrderPricePerDay().getAttribute("textContent").contains("— ₽");
//        log.info("Проверка нельзя заказать до заполнения полей");
//
//        commonChecks.autoChangeableFieldCheck(commonChecks.getVmNumber(), "0", "10");
//        commonChecks.autoChangeableFieldCheck(commonChecks.getVmNumber(), "100", "30");
//        commonChecks.autoChangeableFieldCheck(commonChecks.getVmNumber(), "N", "10");
//        commonChecks.autoChangeableFieldCheck(commonChecks.getVmNumber(), "1", "1");
//        log.info("Проверки поля количества VM");
//
//
//        commonChecks.getMark().sendKeys(CTRL+A);
//        commonChecks.getMark().shouldBe(Condition.matchText("Поле должно содержать от 3 до 64 символов"));
//        log.info("Проверка кнопки \"Метка\" Поле должно содержать от 3 до 64 символов");
//        commonChecks.getOrderProduct().shouldBe(Condition.disabled);
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getRoleServer().selectByValue(product.getRole());
        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
//        //пользователь проверяет, что у элемента "Стоимость в сутки" атрибут "textContent" содержит значение "≈"
//        commonChecks.isCostDayContains("≈");
//        //пользователь проверяет детали заказа
//        commonChecks.checkOrderDetails(commonChecks.getCalculationDetails(), "Windows Server");
        orderPage.orderClick();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        orderPage.getLabel())
                .hover()
                .click();
        WindowsPage winPage = new WindowsPage(product);
        winPage.waitChangeStatus();
        winPage.checkLastAction();
//        // Проверки после заказа продукта
//        commonChecks.checkHeaderHistoryTable();
//        commonChecks.checkHistoryRowDeployOk();
//        commonChecks.checkHistoryRowDeployErr();
//        commonChecks.getHistoryRow0().getAttribute("title").contains("Просмотр схемы выполнения");
//        log.info("пользователь проверяет, что на странице присутствует текст \"Просмотр схемы выполнения\"");
//        commonChecks.getGraphScheme().shouldBe(Condition.visible);
//        log.info(" пользователь проверяет наличие элемента \"Схема выполнения\"");
    }

    @Test
    @Order(2)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.restart();
    }

    @Test
    @Order(3)
    @TmsLinks({@TmsLink("872671"), @TmsLink("872667")})
    @DisplayName("UI Windows. Выключить принудительно. Включить")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.stopHard();
        winPage.start();
    }

    @Test
    @Order(4)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void start() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.stopSoft();
        winPage.start();
    }

    @Test
    @Order(100)
    @TmsLink("872683")
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.delete();
    }


}
