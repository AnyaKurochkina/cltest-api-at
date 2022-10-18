package ui.cloud.tests.orders.clickHouseCluster;


import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.ClickHouse;
import models.orderService.products.ClickHouseCluster;
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
@Tags({@Tag("ui_clickhouse_cluster")})
class UiClickHouseClusterCheckUntilOrderTest extends Tests {

    ClickHouseCluster product;

    //TODO: пока так :)
    public UiClickHouseClusterCheckUntilOrderTest() {
        if (Configure.ENV.equals("prod"))
            product = ClickHouseCluster.builder().productName("ClickHouse Cluster").env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            //product = ClickHouse.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/41ccc48d-5dd0-4892-ae5e-3f1f360885ac/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = ClickHouseCluster.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1177391")
    @DisplayName("UI ClickHouse Cluster. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        ClickHouseClusterOrderPage orderPage = new ClickHouseClusterOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.getOrderBtn().shouldBe(Condition.disabled);

//        //Проверка поля Кол-во
//        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "0", "10");
//        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "100", "30");
//        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "N", "1");
//        orderPage.autoChangeableFieldCheck(orderPage.getCountVm(), "", "1");

        //Проверка Детали заказа
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getNameCluster().setValue("0001");
        orderPage.getNameUser().setValue("at_user");
        orderPage.getGeneratePassButton1().shouldBe(Condition.enabled).click();
        orderPage.getNameDB().setValue("at_db");
        orderPage.getGeneratePassButton2().shouldBe(Condition.enabled).click();
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        orderPage.getGroup2().select(accessGroup.getPrefixName());
        orderPage.getGroup3().select(accessGroup.getPrefixName());
        orderPage.getGroup4().select(accessGroup.getPrefixName());
        new ClickHouseClusterOrderPage().checkOrderDetails();
    }

}
