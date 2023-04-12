package ui.cloud.tests.orders.astraLinux;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.Astra;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.cloud.pages.orders.AstraLinuxOrderPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("Astra Linux")
@Tags({@Tag("ui"), @Tag("ui_astra_linux")})
class UiAstraLinuxCheckUntilOrderTest extends Tests {

    Astra product;
    //product = Astra.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/eb4e1177-30c7-4bdc-94e0-a5d65d5de1ae/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1235787")
    @DisplayName("UI AstraLinux. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        AstraLinuxOrderPage orderPage = new AstraLinuxOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();

        //Проверка поля Кол-во
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "0", "10");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "100", "30");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "N", "1");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "", "1");

        //Проверка Детали заказа
        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
        orderPage.getGroupSelect().set(accessGroup.getPrefixName());
        new AstraLinuxOrderPage().checkOrderDetails();
    }
}
