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

    private static final String READY_ORDER_URL = "https://prod-portal-front.cloud.vtb.ru/vm/orders/c749f2f7-e6eb-4c31-94c5-34cd5cd2f091/main?context=proj-evw9xv5qao&type=project&org=vtb";

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
//        new LoginPage(product.getProjectId())
//                .singIn();
        new LoginPage(READY_ORDER_URL)
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
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getRoleServer().selectByValue(product.getRole());
        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select("cloud-zorg-group3");//accessGroup.getPrefixName()
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
    @Order(2)
    @TmsLink("872666")
    @DisplayName("UI Windows. Перезагрузить по питанию")
    void restart() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.restart();
        commonChecks.checkHistoryRowRestartByPowerOk();
        commonChecks.checkHistoryRowRestartByPowerErr();
    }

//    @Test
//    @Order(4)
//    @TmsLinks({@TmsLink("872671"), @TmsLink("872667")})
//    @DisplayName("UI Windows. Выключить принудительно. Включить")
//    void stopHard() {
//        WindowsPage winPage = new WindowsPage(product);
//        winPage.stopHard();
//        winPage.start();
//    }


    @Test
    @Order(3)
    @TmsLink("872682")
    @DisplayName("UI Windows. Выключить")
    void stopSoft() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.stopSoft();
        commonChecks.checkHistoryRowTurnOffOk();
        commonChecks.checkHistoryRowTurnOffErr();
    }


    @Test
    @Order(4)
    @TmsLink("14510")
    @DisplayName("UI Windows. Изменить конфигурацию")
    void changeConfiguration() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.changeConfiguration();
        commonChecks.checkHistoryRowChangeFlavorOk();
        commonChecks.checkHistoryRowChangeFlavorErr();
    }

    @Test
    @Order(5)
    @TmsLink("872682")
    @DisplayName("UI Windows. Включить")
    void start() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.start();
        commonChecks.checkHistoryRowTurnOnOk();
        commonChecks.checkHistoryRowTurnOnErr();
    }

    @Test
    @Order(6)
    @TmsLink("233925")
    @DisplayName("UI Windows. Добавить диск")
    void discActAdd() throws Throwable {
        WindowsPage winPage = new WindowsPage(product);
        winPage.discActAdd();
        commonChecks.checkHistoryRowDiscAddOk();
        commonChecks.checkHistoryRowDiscAddErr();
    }


    @Test
    @Order(9)
    @TmsLink("714872")
    @DisplayName("UI Windows. Отключить в ОС")
    void discActOff() throws Throwable {
        WindowsPage winPage = new WindowsPage(product);
        winPage.discActOff();
        commonChecks.checkHistoryRowDiscTurnOffOk();
        commonChecks.checkHistoryRowDiscTurnOffErr();
    }

    @Test
    @Order(10)
    @TmsLink("714878")
    @DisplayName("UI Windows. Подключить в ОС")
    void discActOn() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.discActOn();
        commonChecks.checkHistoryRowDiscTurnOnOk();
        commonChecks.checkHistoryRowDiscTurnOnErr();
    }

    @Test
    @Order(11)
    @TmsLink("646056")
    @DisplayName("UI Windows. Удалить диск")
    void discActDelete() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.discActDelete();
        commonChecks.checkHistoryRowDiscDeleteOk();
        commonChecks.checkHistoryRowDiscDeleteErr();
    }

    @Test
    @Order(12)
    //@TmsLink("647426")
    @DisplayName("UI Windows. Проверить конфигурацию")
    void vmActCheckConfig() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.vmActCheckConfig();
        commonChecks.checkHistoryRowCheckConfigOk();
        commonChecks.checkHistoryRowCheckConfigErr();
    }

    @Test
    @Order(13)
    @TmsLink("14485")
    @DisplayName("UI Windows. Выключить принудительно")
    void stopHard() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.stopHard();
        commonChecks.checkHistoryRowForceTurnOffOk();
        commonChecks.checkHistoryRowForceTurnOffErr();
    }


    @Test
    @Order(100)
    @TmsLink("872683")
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() throws Throwable {
        WindowsPage winPage = new WindowsPage(product);
        winPage.delete();
        commonChecks.checkHistoryRowDeletedOk();
        commonChecks.checkHistoryRowDeletedErr();
    }



}
