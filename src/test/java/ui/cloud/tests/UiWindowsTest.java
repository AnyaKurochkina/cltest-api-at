package ui.cloud.tests;

import com.codeborne.selenide.WebDriverRunner;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.ProductsPage;
import ui.cloud.pages.WindowsOrderPage;
import ui.cloud.pages.WindowsPage;
import ui.uiExtesions.ConfigExtension;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UiWindowsTest{
    static Windows product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();

    @BeforeAll
    static void beforeAll(){
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
        WindowsPage winPage = new WindowsPage();
        winPage.waitPending();
        winPage.checkLastAction();
        product.setLink(WebDriverRunner.getWebDriver().getCurrentUrl());
    }

    @AfterAll
    static void afterAll(){
        if(Objects.isNull(product.getLink()))
            return;
        open(product.getLink());
        WindowsPage winPage = new WindowsPage();
        winPage.delete();
    }

    @Test
    @Order(1)
    @DisplayName("UI Windows. Заказ")
    void orderWindows() {}

    @Test
    @Order(2)
    @DisplayName("UI Windows. Перезагрузить по питанию")
    void restart() {
        open(product.getLink());
        WindowsPage winPage = new WindowsPage();
        winPage.restart();
    }

    @Test
    @Order(3)
    @DisplayName("UI Windows. Выключить принудительно")
    void stopHard() {
        open(product.getLink());
        WindowsPage winPage = new WindowsPage();
        winPage.stopHard();
    }

    @Test
    @Order(4)
    @DisplayName("UI Windows. Выключить принудительно")
    void start() {
        open(product.getLink());
        WindowsPage winPage = new WindowsPage();
        winPage.start();
    }

    @Test
    @Order(100)
    @DisplayName("UI Windows. Удалить")
    void deleteWindows() {
        open(product.getLink());
        WindowsPage winPage = new WindowsPage();
        winPage.delete();
        product.setLink(null);
    }


}
