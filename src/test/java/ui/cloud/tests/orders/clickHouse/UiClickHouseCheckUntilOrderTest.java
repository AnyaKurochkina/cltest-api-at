package ui.cloud.tests.orders.clickHouse;


import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ClickHouse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.ClickHouseOrderPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

import static steps.portalBack.PortalBackSteps.getRandomAccessGroup;

@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("ClickHouse")
@Tags({@Tag("ui"), @Tag("ui_clickHouse")})
class UiClickHouseCheckUntilOrderTest extends Tests {

   private ClickHouse product;
    //= ClickHouse.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/eb4e1177-30c7-4bdc-94e0-a5d65d5de1ae/main?context=proj-1oob0zjo5h&type=project&org=vtb");


    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1162642")
    @DisplayName("UI ClickHouse. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        ClickHouseOrderPage orderPage = new ClickHouseOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();

        //Проверка Детали заказа
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getNameUser().setValue("at_user");
        orderPage.getGeneratePassButton1().shouldBe(Condition.enabled).click();
        orderPage.getGeneratePassButton2().shouldBe(Condition.enabled).click();
        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMaxFlavor()));
        String accessGroup = getRandomAccessGroup(product.getProjectId(), "", "compute");
        orderPage.getGroup().set(accessGroup);
        orderPage.getGroup2().set(accessGroup);
        orderPage.getGroup3().set(accessGroup);
        orderPage.getGroup4().set(accessGroup);

        new ClickHouseOrderPage().checkOrderDetails();
    }

}
