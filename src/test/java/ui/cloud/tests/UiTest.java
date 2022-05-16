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
public class UiTest {
    static Windows product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();

    @Test
    void test() {
        new LoginPage("proj-xazpppulba").singIn();
        open("https://ift-portal-front.apps.d0-oscp.corp.dev.vtb/container/orders/328091e3-7f99-4525-95b3-0c6b2869db6b/main?context=proj-xazpppulba&type=project&org=vtb");
        WindowsPage page = new WindowsPage(product);
        System.out.println(1);
    }


}
