package ui.cloud.tests.orders.postgreSqlAstraLinux;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.PostgreSQL;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.elements.Alert;
import ui.elements.Graph;
import ui.elements.Table;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.time.Duration;

@ExtendWith(ConfigExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_postgre_sql_astra")})
@Log4j2
public class UiPostgreSqlAstraLinuxTest extends Tests {

    PostgreSQL product;
    String nameDb = "at_db";
    String shortNameUserDB = "at_user";
    String fullNameUserDB = "at_db_at_user";

    public UiPostgreSqlAstraLinuxTest() {
        if (Configure.ENV.equals("prod"))
            product = PostgreSQL.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            //product = PostgreSQL.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/486a999a-8e2c-4a98-afc3-6e03861444c4/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = PostgreSQL.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();

    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("988624")
    @Order(1)
    @DisplayName("UI PostgreSQLAstra. Заказ")
    void orderPostgreSQL() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            PostgreSQLAstraOrderPage orderPage = new PostgreSQLAstraOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            orderPage.getGroup().select(accessGroup.getPrefixName());
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = IProductPage.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new Alert().checkColor(Alert.Color.GREEN).checkText("Заказ успешно создан");
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            PostgreSqlAstraPage pSqlPages = new PostgreSqlAstraPage(product);
            pSqlPages.waitChangeStatus(Duration.ofMinutes(25));
            pSqlPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        Assertions.assertEquals(preBillingProductPrice, pSqlPage.getCostOrder(), 0.01);
    }


    @Test
    @TmsLink("1156591")
    @Order(2)
    @DisplayName("UI PostgreSQLAstra. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        pSqlPage.checkHeadersHistory();
        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(5)
    @TmsLink("993396")
    @DisplayName("UI PostgreSQLAstra. Перезагрузить по питанию")
    void restart() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::restart);
    }

    @Test
    @Order(6)
    @TmsLink("993391")
    @DisplayName("UI PostgreSQLAstra. Получить актуальную конфигурацию")
    void getActualConfiguration() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::getActualConfiguration);
    }

    @Test
    @Order(7)
    @TmsLink("993402")
    @DisplayName("UI PostgreSQLAstra. Изменить default_transaction_isolation на REPEATABLE READ")
    void changeTransactionIsolation() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeTransactionIsolation("REPEATABLE READ"));
    }

    @Test
    @Order(8)
    @TmsLink("993392")
    @DisplayName("UI PostgreSQLAstra. Изменить max_connections")
    void changeMaxConnections() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeMaxConnections("145"));
    }


    @Test
    @Order(9)
    @TmsLink("993389")
    @DisplayName("UI PostgreSQLAstra. Расширить диск")
    void expandDisk() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.enlargeDisk("/pg_data", "20", new Table("Роли узла").getRowByIndex(0)));
    }


    @Test
    @Order(10)
    @TmsLink("993393")
    @DisplayName("UI PostgreSQLAstra. Изменить конфигурацию")
    void changeConfiguration() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::changeConfiguration);
    }

    @Test
    @Order(11)
    @TmsLink("994656")
    @DisplayName("UI PostgreSQLAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::checkConfiguration);
    }

    @Test
    @Order(12)
    @TmsLink("993398")
    @DisplayName("UI PostgreSQLAstra. Создание БД")
    void createDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
    }


    @Test
    @Order(15)
    @TmsLink("993388")
    @DisplayName("UI PostgreSQLAstra. Сбросить пароль владельца БД")
    void resetPasswordDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::resetPasswordDb);
    }

    @Test
    @Order(16)
    @TmsLink("993394")
    @DisplayName("UI PostgreSQLAstra. Добавить пользователя")
    void addUserDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
    }

    @Test
    @Order(17)
    @TmsLink("993387")
    @DisplayName("UI PostgreSQLAstra. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.resetPasswordUserDb(fullNameUserDB));
    }

    @Test
    @Order(18)
    @TmsLink("993395")
    @DisplayName("UI PostgreSQLAstra. Удалить пользователя БД")
    void deleteUserDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deleteUserDb(fullNameUserDB));
    }

    @Test
    @Order(19)
    @TmsLink("993400")
    @DisplayName("UI PostgreSQLAstra. Удаление БД")
    void removeDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.removeDb(nameDb));
    }

    @Test
    @Order(20)
    @TmsLinks({@TmsLink("993397"), @TmsLink("993401")})
    @DisplayName("UI PostgreSQLAstra. Выключить принудительно / Включить")
    void stopHard() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopHard);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::start);
    }

    @Test
    @Order(21)
    @TmsLink("993390")
    @DisplayName("UI PostgreSQLAstra. Выключить")
    void stopSoft() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopSoft);
    }

    @Test
    @Order(100)
    @TmsLink("993399")
    @DisplayName("UI PostgreSQLAstra. Удаление продукта")
    void delete() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.delete();
    }

}
