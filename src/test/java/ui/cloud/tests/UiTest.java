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
        open("https://ift-portal-front.apps.d0-oscp.corp.dev.vtb/vm/orders/ff5776ca-2252-4b65-832a-e7a3fc169ea6/main?context=proj-xazpppulba&type=project&org=vtb");
        WindowsPage page = new WindowsPage();
        System.out.println(1);
    }


}
