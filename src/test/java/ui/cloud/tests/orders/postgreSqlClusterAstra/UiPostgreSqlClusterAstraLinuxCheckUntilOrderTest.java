package ui.cloud.tests.orders.postgreSqlClusterAstra;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.PostgresSQLCluster;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.cloud.pages.orders.PostgreSqlClusterAstraOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("PostgreSQL Cluster Astra Linux")
@Tags({@Tag("ui"), @Tag("ui_postgre_sql_cluster_astra")})
class UiPostgreSqlClusterAstraLinuxCheckUntilOrderTest extends Tests {

    PostgresSQLCluster product;
    //= PostgresSQLCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/714818a6-66f0-4939-830e-73cb627c5acc/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1151310")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
        new IndexPage()
                .clickOrderMore()
                .expandProductsList()
                .selectProduct(product.getProductName());
        PostgreSqlClusterAstraOrderPage orderPage = new PostgreSqlClusterAstraOrderPage();
        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();
        //Проверка Детали заказа
        if (product.isDev() || product.isTest() )
            orderPage.getSegmentSelect().set(product.getSegment());
        if (product.isProd())
            orderPage.getSegmentSelect().set("PROD-SRV-APP");
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
        if (product.isDev() || product.isTest() )
            orderPage.getGroupSelect().set(accessGroup);
        new PostgreSqlClusterAstraOrderPage().checkOrderDetails();
    }
}
