package ui.cloud.tests.orders.rabbitMqClusterAstra;//package ui.cloud.tests.orders.apacheKafkaClusterAstra;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.RabbitMQClusterAstra;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.cloud.pages.orders.RabbitMqClusterAstraOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;


@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("RabbitMqClusterAstraCheck")
@Tags({@Tag("ui"), @Tag("ui_RabbitMqClusterAstraCheck")})
class UiRabbitMqClusterAstraCheckUntilOrderTest extends Tests {

    RabbitMQClusterAstra product;
    //= RabbitMQClusterAstra.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/application_integration/orders/25771046-8bce-407d-bbcd-7ca3fe38a051/main?context=proj-1oob0zjo5h&type=project&org=vtb");


    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1727718")
    @DisplayName("UI RabbitMQClusterAstra. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        RabbitMqClusterAstraOrderPage orderPage = new RabbitMqClusterAstraOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();

        //Проверка Детали заказа
        orderPage.getOsVersionSelect().set(product.getOsVersion());
        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getPlatformSelect().set(product.getPlatform());
        orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroupSelect().set(accessGroup.getPrefixName());
        orderPage.getGroup2Select().set(accessGroup.getPrefixName());
        new RabbitMqClusterAstraOrderPage().checkOrderDetails();
    }

}
