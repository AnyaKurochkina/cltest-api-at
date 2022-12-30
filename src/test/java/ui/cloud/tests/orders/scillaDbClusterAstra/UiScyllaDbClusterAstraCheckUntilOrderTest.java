package ui.cloud.tests.orders.scillaDbClusterAstra;


import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ScyllaDbCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.extesions.UiProductTest;

@Epic("UI Продукты")
@Feature("ScyllaDbClusterAstra")
@Tags({@Tag("ui"), @Tag("ui_scylla_db_cluster_astra")})
class UiScyllaDbClusterAstraCheckUntilOrderTest extends Tests {

    ScyllaDbCluster product;
    // = ScyllaDbCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/3a445f64-a939-4d92-b967-5b545d83fb5f/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1368045")
    @DisplayName("UI Scylla_db_cluster_astra. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        ScyllaDbClusterOrderPage orderPage = new ScyllaDbClusterOrderPage();

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
        orderPage.getConfigure().set(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        new ScyllaDbClusterOrderPage().checkOrderDetails();
    }

}
