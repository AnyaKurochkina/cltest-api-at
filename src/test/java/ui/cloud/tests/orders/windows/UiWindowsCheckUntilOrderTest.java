package ui.cloud.tests.orders.windows;


import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.Product;
import ui.cloud.pages.WindowsOrderPage;
import ui.uiExtesions.ConfigExtension;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@Log4j2
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({@Tag("ui_windows")})
class UiWindowsCheckUntilOrderTest extends Tests {
    Windows product;

    //TODO: пока так :)
    public UiWindowsCheckUntilOrderTest() {
        if (Configure.ENV.equals("prod"))
            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            //product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/vm/orders?page=0&perPage=10&f[category]=vm&f[status][]=success&f[status][]=changing&f[status][]=damaged&f[status][]=pending&context=proj-evw9xv5qao&type=project&org=vtb").build();
        else
            product = Windows.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("975914")
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
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "N", "30");
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "", "30");

        //Проверка Детали заказа
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getRoleServer().selectByValue(product.getRole());
        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        new WindowsOrderPage().checkOrderDetails();
    }

}
