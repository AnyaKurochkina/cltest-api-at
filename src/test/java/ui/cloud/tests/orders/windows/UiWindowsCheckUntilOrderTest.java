package ui.cloud.tests.orders.windows;


import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.Windows;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import api.Tests;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.Product;
import ui.cloud.pages.WindowsOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.UiProductTest;

import static com.codeborne.selenide.Selenide.$;

@Epic("UI Продукты")
@Feature("Windows")
@Tags({@Tag("ui"), @Tag("ui_windows")})
class UiWindowsCheckUntilOrderTest extends UiProductTest {
    Windows product;
    //= Windows.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/compute/orders/8f8ca2bb-242a-46dc-8699-09f5c7fb373f/main?context=proj-ln4zg69jek&type=project&org=vtb");


    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("975914")
    @Order(1)
    @DisplayName("UI Windows. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsOrderPage orderPage = new WindowsOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.getOrderBtn().shouldBe(Condition.disabled);

        //Проверка поля Кол-во
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "0", "10");
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "100", "30");
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "N", "1");
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "", "1");

        //Проверка Детали заказа
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getRoleServer().selectByValue(product.getRole());
        orderPage.getConfigure().set(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        new WindowsOrderPage().checkOrderDetails();
    }

}
