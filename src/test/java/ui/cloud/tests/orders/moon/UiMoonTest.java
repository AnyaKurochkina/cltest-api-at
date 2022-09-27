package ui.cloud.tests.orders.moon;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.Moon;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.time.Duration;

@ExtendWith(InterceptTestExtension.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_moon")})
@Log4j2
public class UiMoonTest extends Tests {
    Moon product;

    //TODO: пока так :)
    public UiMoonTest() {
        if (Configure.ENV.equals("prod"))
            product = Moon.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
//            product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/vm/orders/761a5b34-ecfb-4033-ab66-a2a65cf205ec/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = Moon.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        //Configuration.browserSize = "1366x768";
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("850206")
    @Order(1)
    @DisplayName("UI Moon. Заказ")
    void orderMoon() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            MoonOrderPage orderPage = new MoonOrderPage();
            //orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getDataCentre().selectByValue(product.getDataCentre());
            orderPage.getProjectName().setValue(new Generex("moon-[a-z]{5,15}").random());

            //orderPage.getPlatform().selectByValue(product.getPlatform());
            //orderPage.getRoleServer().selectByValue(product.getRole());
            //orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
            //AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            //orderPage.getGroup().select(accessGroup.getPrefixName());
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = IProductPage.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowElementByColumnValue("Продукт",
                            orderPage.getLabelValue())
                    .hover()
                    .click();
            MoonPage moonPages = new MoonPage(product);
            moonPages.waitChangeStatus(Duration.ofMinutes(25));
            moonPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        MoonPage moonPage = new MoonPage(product);
        Assertions.assertEquals(preBillingProductPrice, moonPage.getCostOrder(), 0.01);
    }

    @Test
    @Order(100)
    @TmsLink("850205")
    @DisplayName("UI Moon. Удалить")
    void deleteMoon() {
        MoonPage moonPage = new MoonPage(product);
        moonPage.runActionWithCheckCost(CompareType.LESS, moonPage::delete);
    }

}
