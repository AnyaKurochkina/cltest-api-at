package ui.cloud.tests.orders.clickHouse;


import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.ClickHouse;
import models.orderService.products.PostgreSQL;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.uiExtesions.ConfigExtension;

@Log4j2
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({@Tag("ui_clickhouse")})
class UiClickHouseCheckUntilOrderTest extends Tests {

    ClickHouse product;

    //TODO: пока так :)
    public UiClickHouseCheckUntilOrderTest() {
        if (Configure.ENV.equals("prod"))
            product = ClickHouse.builder().productName("ClickHouse").env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            //product = ClickHouse.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/41ccc48d-5dd0-4892-ae5e-3f1f360885ac/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = ClickHouse.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();

    }

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
        orderPage.getOrderBtn().shouldBe(Condition.disabled);

        //Проверка Детали заказа
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getNameUser().setValue("at_user");
        orderPage.getGeneratePassButton1().shouldBe(Condition.enabled).click();
        orderPage.getGeneratePassButton2().shouldBe(Condition.enabled).click();
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        new ClickHouseOrderPage().checkOrderDetails();
    }

}
