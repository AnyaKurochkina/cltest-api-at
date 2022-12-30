package ui.cloud.tests.orders.postgreSqlClusterAstra;


import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.PostgresSQLCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.extesions.UiProductTest;

@Epic("UI Продукты")
@Feature("PostgreSQL Cluster Astra Linux")
@Tags({@Tag("ui"), @Tag("ui_postgre_sql_cluster_astra")})
class UiPostgreSqlClusterAstraLinuxCheckUntilOrderTest extends Tests {

    PostgresSQLCluster product;
    //= PostgresSQLCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/714818a6-66f0-4939-830e-73cb627c5acc/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1151310")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        PostgreSqlClusterAstraOrderPage orderPage = new PostgreSqlClusterAstraOrderPage();
        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.getOrderBtn().shouldBe(Condition.disabled);
        //Проверка Детали заказа
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getConfigure().set(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        new PostgreSqlClusterAstraOrderPage().checkOrderDetails();
    }
}
