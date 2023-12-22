package ui.cloud.tests.orders.apacheKafkaClusterAstra;//package ui.cloud.tests.orders.apacheKafkaClusterAstra;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ApacheKafkaCluster;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.ApacheKafkaClusterOrderPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;


@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("ApacheKafkaCluster")
@Tags({@Tag("ui"), @Tag("ui_ApacheKafkaCluster")})
class UiApacheKafkaClusterCheckUntilOrderTest extends Tests {

    private ApacheKafkaCluster product;
    //= ApacheKafkaCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/application_integration/orders/25771046-8bce-407d-bbcd-7ca3fe38a051/main?context=proj-1oob0zjo5h&type=project&org=vtb");


    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1235642")
    @DisplayName("UI ApacheKafkaCluster. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        ApacheKafkaClusterOrderPage orderPage = new ApacheKafkaClusterOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();

        //Проверка Детали заказа
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
        String accessGroup = product.accessGroup();
        orderPage.getGroupSelect().set(accessGroup);
        new ApacheKafkaClusterOrderPage().checkOrderDetails();
    }

}
