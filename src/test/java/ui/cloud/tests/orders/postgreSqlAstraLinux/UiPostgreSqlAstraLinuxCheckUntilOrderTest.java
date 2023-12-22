package ui.cloud.tests.orders.postgreSqlAstraLinux;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.PostgreSQL;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.NewOrderPage;
import ui.cloud.pages.orders.PostgreSqlAstraOrderPage;
import ui.extesions.ConfigExtension;
import ui.extesions.ProductInjector;

@Log4j2
@ExtendWith(ConfigExtension.class)
@ExtendWith(ProductInjector.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({@Tag("ui_postgre_sql_astra")})
class UiPostgreSqlAstraLinuxCheckUntilOrderTest extends Tests {

    private PostgreSQL product;
    //= PostgreSQL.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/eb4e1177-30c7-4bdc-94e0-a5d65d5de1ae/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1139488")
    @DisplayName("UI PostgreSQLAstra. Проверка полей при заказе продукта")
    void checkFieldVmNumber() {
        String accessGroup = product.accessGroup();
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        PostgreSqlAstraOrderPage orderPage = new PostgreSqlAstraOrderPage();

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
        orderPage.getGroupSelect().set(accessGroup);
        new PostgreSqlAstraOrderPage().checkOrderDetails();
    }
}
