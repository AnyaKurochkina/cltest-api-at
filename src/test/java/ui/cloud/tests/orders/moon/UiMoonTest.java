package ui.cloud.tests.orders.moon;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.Moon;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.extesions.UiProductTest;

import java.time.Duration;

import static ui.cloud.pages.EntitiesUtils.checkOrderCost;

@Tags({@Tag("ui_moon")})
@Log4j2
public class UiMoonTest extends UiProductTest {
    Moon product;

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("850206")
    @Order(1)
    @DisplayName("UI Moon. Заказ")
    void orderMoon() {
        double prebillingCost;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            MoonOrderPage orderPage = new MoonOrderPage();
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getProjectName().setValue(new Generex("moon-[a-z]{5,15}").random());
            prebillingCost = EntitiesUtils.getCostValue(orderPage.getPrebillingCostElement());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт",
                            orderPage.getLabelValue())
                    .get()
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
        checkOrderCost(prebillingCost, moonPage);
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
