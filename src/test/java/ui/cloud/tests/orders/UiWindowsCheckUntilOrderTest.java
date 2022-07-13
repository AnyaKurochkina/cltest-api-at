package ui.cloud.tests.orders;


import com.codeborne.selenide.Condition;
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
            product = Windows.builder().env("DSO").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
//        Configuration.browserSize = "1366x768";
        new LoginPage(product.getProjectId())
                .signIn();
    }

    @Test
    @TmsLink("975914")
    @Order(1)
    @DisplayName("UI Windows. Проверка поля Количество VM до заказа продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsOrderPage orderPage = new WindowsOrderPage();
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "0", "10");
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "100", "30");
        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "N", "1");
    }

    @Test
    @TmsLink("976622")
    @DisplayName("UI Windows. Проверка поля Метка до заказа продукта")
    void checkFieldMark() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsOrderPage orderPage = new WindowsOrderPage();
        orderPage.getLabel().setValue("");
        $(byText("Поле должно содержать от 3 до 64 символов")).should(Condition.exist);
    }

    @Test
    @TmsLink("976626")
    @DisplayName("UI Windows. Проверка кнопки Заказать на неактивность, до заполнения полей")
    void checkBtnOrderDisabled() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        new WindowsOrderPage().getOrderBtn().shouldBe(Condition.disabled);
    }

    @Test
    @TmsLink("976724")
    @DisplayName("UI Windows. Проверка Детали заказа.")
    void checkDetailsOrder() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsOrderPage orderPage = new WindowsOrderPage();
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
