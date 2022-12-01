package ui.cloud.tests.orders.apacheKafkaClusterAstra;//package ui.cloud.tests.orders.apacheKafkaClusterAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ApacheKafkaCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.extesions.UiProductTest;


@Epic("UI Продукты")
@Feature("ApacheKafkaCluster")
@Tags({@Tag("ui"), @Tag("ui_ApacheKafkaCluster")})
class UiApacheKafkaClusterCheckUntilOrderTest extends UiProductTest {

    ApacheKafkaCluster product;
    //= ApacheKafkaCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/application_integration/orders/25771046-8bce-407d-bbcd-7ca3fe38a051/main?context=proj-1oob0zjo5h&type=project&org=vtb");


    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1235642")
    @DisplayName("UI RedisAstra. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        ApacheKafkaClusterOrderPage orderPage = new ApacheKafkaClusterOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.getOrderBtn().shouldBe(Condition.disabled);


        //Проверка Детали заказа
        orderPage.getOsVersion().select(product.getOsVersion());
        orderPage.getSegment().selectByValue(product.getSegment());
        orderPage.getPlatform().selectByValue(product.getPlatform());
        orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        orderPage.getGroup().select(accessGroup.getPrefixName());
        new ApacheKafkaClusterOrderPage().checkOrderDetails();
    }

}
