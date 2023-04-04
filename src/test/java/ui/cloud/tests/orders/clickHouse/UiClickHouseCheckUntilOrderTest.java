package ui.cloud.tests.orders.clickHouse;


import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ClickHouse;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.ClickHouseOrderPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.NewOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("ClickHouse")
@Tags({@Tag("ui"), @Tag("ui_clickHouse")})
class UiClickHouseCheckUntilOrderTest extends Tests {

    ClickHouse product;
    //= ClickHouse.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/eb4e1177-30c7-4bdc-94e0-a5d65d5de1ae/main?context=proj-1oob0zjo5h&type=project&org=vtb");


    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
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
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().set(accessGroup.getPrefixName());
        orderPage.getGroup2().set(accessGroup.getPrefixName());
        orderPage.getGroup3().set(accessGroup.getPrefixName());
        orderPage.getGroup4().set(accessGroup.getPrefixName());

        new ClickHouseOrderPage().checkOrderDetails();
    }

}
