package ui.cloud.tests.orders.kafkaService;//package ui.cloud.tests.orders.apacheKafkaClusterAstra;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.KafkaService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.KafkaServiceOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;


@Epic("UI Продукты")
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@Feature("KafkaService")
@Tags({@Tag("ui"), @Tag("ui_Kafka_service")})
class UiKafkaServiceCheckUntilOrderTest extends Tests {

    private KafkaService product; // = KafkaService.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/1e521f86-97f9-4bef-bea4-136aa41d5053/main?context=proj-ln4zg69jek&type=project&org=vtb");


    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("")
    @DisplayName("UI KafkaService. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        KafkaServiceOrderPage orderPage = new KafkaServiceOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.checkOrderDisabled();

        //Проверка Детали заказа

        orderPage.getSegmentSelect().set(product.getSegment());
        orderPage.getNameTopic().setValue("topic");
        orderPage.getDomain().set(product.getDomain());
    }

}
