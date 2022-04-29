package ui.cloud.tests;

import com.codeborne.selenide.Condition;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import ui.cloud.pages.*;
import ui.uiExtesions.ConfigExtension;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith(ConfigExtension.class)
public class UiWindowsTest{

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Заказ Windows {0}")
    void orderWindows(Windows product) {
//        new LoginPage(product.getProjectId())
//                .singIn();
//        open("https://cloud.vtb.ru/vm/orders?page=2&perPage=10&f[category]=vm&f[status][]=success&f[status][]=changing&f[status][]=damaged&f[status][]=pending&context=proj-frybyv41jh&type=project&org=vtb");
//        WindowsPage winPage = new WindowsPage();
//        winPage.waitPending();
//        winPage.checkLastAction();
//        new ProductsPage();

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

        new ProductsPage().getRowByColumn("Продукт", orderPage.getLabel()).hover().click();
        WindowsPage winPage = new WindowsPage();
        winPage.waitPending();
        winPage.checkLastAction();
//        winPage.
//        System.out.println(0);
    }


}
