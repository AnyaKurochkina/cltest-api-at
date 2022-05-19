package ui.cloud.tests;

import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.orderService.OrderServiceSteps;
import tests.Tests;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.ProductsPage;
import ui.cloud.pages.WindowsOrderPage;
import ui.cloud.pages.WindowsPage;
import ui.uiExtesions.ConfigExtension;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

@ExtendWith(CustomBeforeAllAndAfterAll.class)

@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_windows")})
public class UiWindowsTest extends Tests {
    Windows product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();

    @BeforeAll
    void beforeAll(){
        product.init();
        new LoginPage(product.getProjectId())
                .singIn()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsOrderPage orderPage = new WindowsOrderPage();
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getRoleServer().selectByValue(product.getRole());
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        orderPage.orderClick();
        new ProductsPage()
                .getRowByColumn("Продукт",
                        orderPage.getLabel())
                .hover()
                .click();
        WindowsPage winPage = new WindowsPage(product);
        winPage.waitChangeStatus();
        winPage.checkLastAction();
        closeWebDriver();
    }

    @AfterAll
    void afterAll(){
        if(Objects.nonNull(product.getLink())) {
            OrderServiceSteps.deleteProduct(product);
        }
    }

    @BeforeEach
    void beforeEach(){
        new LoginPage(product.getProjectId())
                .singIn();
        open(product.getLink());
    }

    @Test
    @TmsLink("872651")
    @Order(1)
    @DisplayName("UI Windows. Заказ")
    void orderWindows() {}

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
