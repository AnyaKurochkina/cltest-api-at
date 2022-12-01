package ui.cloud.tests.orders.clickHouseCluster;


import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ClickHouseCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.extesions.UiProductTest;

@Epic("UI Продукты")
@Feature("ClickHouse Cluster")
@Tags({@Tag("ui"), @Tag("ui_clickhouse_cluster")})
class UiClickHouseClusterCheckUntilOrderTest extends UiProductTest {

    ClickHouseCluster product;
    //  product = ClickHouseCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/eb4e1177-30c7-4bdc-94e0-a5d65d5de1ae/main?context=proj-1oob0zjo5h&type=project&org=vtb");

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
        orderPage.getNameCluster().setValue("cluster");
        orderPage.getNameUser().setValue("at_user");
        orderPage.getGeneratePassButton1().shouldBe(Condition.enabled).click();
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
