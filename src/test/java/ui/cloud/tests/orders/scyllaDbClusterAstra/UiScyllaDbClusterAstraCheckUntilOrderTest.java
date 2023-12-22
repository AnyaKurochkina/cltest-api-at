package ui.cloud.tests.orders.scyllaDbClusterAstra;


import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ScyllaDbCluster;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.cloud.pages.orders.ScyllaDbClusterOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("ScyllaDbClusterAstra")
@Tags({@Tag("ui"), @Tag("ui_scylla_db_cluster_astra")})
class UiScyllaDbClusterAstraCheckUntilOrderTest extends Tests {

    ScyllaDbCluster product;
    // = ScyllaDbCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/3a445f64-a939-4d92-b967-5b545d83fb5f/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        //product.setProductName("ScyllaDB Cluster RHEL"); //Для RHEL версии
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1368045")
    @DisplayName("UI Scylla_db_cluster_astra. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .expandProductsList()
                .selectProduct(product.getProductName());
        ScyllaDbClusterOrderPage orderPage = new ScyllaDbClusterOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();

        //Проверка поля Кол-во
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "0", "10");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "100", "30");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "N", "1");
        orderPage.autoChangeableFieldCheck(orderPage.getCountInput(), "", "1");

        //Проверка Детали заказа
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
        String accessGroup = product.accessGroup();
        orderPage.getGroupSelect().set(accessGroup);
        new ScyllaDbClusterOrderPage().checkOrderDetails();
    }
}
