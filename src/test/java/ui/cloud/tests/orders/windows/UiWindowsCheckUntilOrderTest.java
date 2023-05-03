package ui.cloud.tests.orders.windows;


import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.Windows;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.cloud.pages.orders.WindowsOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("Windows")
@Tags({@Tag("ui"), @Tag("ui_windows")})
class UiWindowsCheckUntilOrderTest extends Tests {
    Windows product;
    //= Windows.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/compute/orders/8f8ca2bb-242a-46dc-8699-09f5c7fb373f/main?context=proj-ln4zg69jek&type=project&org=vtb");


    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("975914")
    @DisplayName("UI Windows. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsOrderPage orderPage = new WindowsOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();

        //Проверка поля Кол-во
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "0", "10");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "100", "30");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "N", "1");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "", "1");

        //Проверка Детали заказа
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getRoleServer().set(product.getRole());
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
        orderPage.getGroupSelect().set(accessGroup);
        new WindowsOrderPage().checkOrderDetails();
    }

}
