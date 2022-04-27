package ui.cloud.tests;

import models.orderService.products.Windows;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.WindowsOrderPage;
import ui.uiExtesions.ConfigExtension;

@ExtendWith(ConfigExtension.class)
public class UiWindowsTest {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Заказ Windows {0}")
    void orderWindows(Windows product) {
        product.init();
        new LoginPage(product.getProjectId())
                .singIn()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsOrderPage orderPage = new WindowsOrderPage();
        orderPage.getOsVersion().select("2019");
//        orderPage.getGroup().select("cloud-zorg-cywrjgl");
//        orderPage.getGroup().select("cloud-zorg-xfgbboi");
//        orderPage.getSegment().select("DEV-SRV-APP");
//        orderPage.getPlatform().select("OpenStack");
        orderPage.getRoleServer().select("Proxy Server (px)");
        System.out.println(1);
    }


}
