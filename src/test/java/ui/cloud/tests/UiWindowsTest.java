package ui.cloud.tests;

import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
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
public class UiWindowsTest extends Tests {
    Windows product;

    //TODO: пока так :)
    public UiWindowsTest() {
        if (Configure.ENV.equals("prod"))
            product = Windows.builder().env("DEV").projectId("proj-evw9xv5qao").platform("OpenStack").segment("dev-srv-app").build();
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
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getRoleServer().selectByValue(product.getRole());
        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select("cloud-zorg-group3");//accessGroup.getPrefixName()
        CommonChecks commonChecks = new CommonChecks();
        commonChecks.isCostDayContains("≈");
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
