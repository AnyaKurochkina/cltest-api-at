package ui.cloud.tests;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.util.Objects;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.CONTROL;


@ExtendWith(InterceptTestExtension.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({@Tag("ui_windows")})
@Log4j2

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
        Configuration.browserSize = "1366x768";
        new LoginPage(product.getProjectId())
                .singIn();
    }

    @Test
    @TmsLink("975914")
    @Order(1)
    @DisplayName("UI Windows. Проверка поля Количество VM до заказа продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        WindowsPage winPage = new WindowsPage(product);
        winPage.checkFieldVmNumber();
    }

    @Test
    @TmsLink("976622")
    @Order(2)
    @DisplayName("UI Windows. Проверка поля Метка до заказа продукта")
    void checkFieldMark() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getMark().sendKeys(CONTROL + "a");
        winPage.getMark().sendKeys(BACK_SPACE);
        $(byText("Поле должно содержать от 3 до 64 символов")).should(Condition.exist);
        log.info("Проверка поля метка должно содержать от 3 до 64 символов");
    }

    @Test
    @TmsLink("976626")
    @Order(3)
    @DisplayName("UI Windows. Проверка кнопки Заказать на неактивность, до заполнения полей")
    void checkBtnOrderDisabled() {
        WindowsPage winPage = new WindowsPage(product);
        winPage.getOrderProduct().shouldBe(Condition.disabled);
    }

    @Test
    @TmsLink("976629")
    @Order(4)
    @DisplayName("UI Windows. Проверка атрибута \"textContent\" на содержание символа \"— ₽\"")
    void checkFieldAtrTextContentSymbol() {
        WindowsPage winPage = new WindowsPage(product);
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        Objects.requireNonNull(winPage.getOrderPricePerDay().getAttribute("textContent")).contains("— ₽");
    }

    @Test
    @TmsLink("976722")
    @Order(5)
    @DisplayName("UI Windows. Проверка у элемента \"Стоимость в сутки\" атрибут \"textContent\" содержит значение \"≈\"")
    void checkElementAtrTextContentSymbol() {
        WindowsPage winPage = new WindowsPage(product);
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
        winPage.isCostDayContains("≈");
    }

    @Test
    @TmsLink("976724")
    @Order(6)
    @DisplayName("UI Windows. Проверка Детали заказа.")
    void checkDetailsOrder() {
        WindowsPage winPage = new WindowsPage(product);
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
        winPage.checkOrderDetails(winPage.getCalculationDetails(), product.getProductName());
    }
}
