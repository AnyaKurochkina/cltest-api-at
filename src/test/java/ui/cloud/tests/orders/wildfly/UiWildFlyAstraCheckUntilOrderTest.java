package ui.cloud.tests.orders.wildfly;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.WildFly;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.cloud.pages.orders.WildFlyAstraOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("WildFlyAstra")
@Tags({@Tag("ui"), @Tag("ui_wildfly_astra")})
class UiWildFlyAstraCheckUntilOrderTest extends Tests {

    WildFly product;
    //product = Astra.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/eb4e1177-30c7-4bdc-94e0-a5d65d5de1ae/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("")
    @DisplayName("UI WildFlyAstra. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WildFlyAstraOrderPage orderPage = new WildFlyAstraOrderPage();
        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();
        //Проверка Детали заказа
        orderPage.getVersionWildfly().set(product.getWildFlyVersion());
        orderPage.getVersionJava().set(product.getJavaVersion());
        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getVersionJava().set(product.getJavaVersion());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
        orderPage.getRoleSelect().set("user");
        String accessGroup = product.accessGroup();
        orderPage.getGroupSelect().set(accessGroup);
        orderPage.getGroupWildFly().set(accessGroup);
        new WildFlyAstraOrderPage().checkOrderDetails();
    }
}
