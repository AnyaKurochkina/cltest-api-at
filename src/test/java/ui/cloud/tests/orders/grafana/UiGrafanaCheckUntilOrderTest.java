package ui.cloud.tests.orders.grafana;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.Grafana;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.GrafanaOrderPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("Grafana")
@Tags({@Tag("ui"), @Tag("ui_Grafanax")})
class UiGrafanaCheckUntilOrderTest extends Tests {

    private Grafana product;
    //product = Grafana.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/eb4e1177-30c7-4bdc-94e0-a5d65d5de1ae/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1742981")
    @DisplayName("UI Grafana. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        String accessGroup = product.accessGroup();
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        GrafanaOrderPage orderPage = new GrafanaOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();

        //Проверка поля Кол-во
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "0", "10");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "N", "1");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "", "1");

        //Проверка Детали заказа
        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getCreateUser().setValue("user");
        orderPage.getGeneratePassButton().click();
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
        orderPage.getGroupSelect().set(accessGroup);
        new GrafanaOrderPage().checkOrderDetails();
    }
}
