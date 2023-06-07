package ui.cloud.tests.orders.postgreSqlAstraLinux;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.PostgreSQL;
import models.cloud.portalBack.AccessGroup;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.*;
import ui.cloud.pages.orders.*;
import ui.cloud.tests.ActionParameters;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;
import static ui.elements.TypifiedElement.scrollCenter;

@Epic("UI Продукты")
@Feature("PostgreSQL (Astra Linux)")
@Tags({@Tag("ui"), @Tag("ui_postgre_sql_astra")})
public class UiPostgreSqlAstraLinuxTest extends UiProductTest {

    PostgreSQL product;// = PostgreSQL.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/825c387a-0daf-4da9-90aa-be77568d8f1f/main?context=proj-ln4zg69jek&type=project&org=vtb");

    String nameDb = "at_db";
    String shortNameUserDB = "at_user";
    String fullNameUserDB = "at_db_at_user";

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("988624")
    @Order(1)
    @DisplayName("UI PostgreSQLAstra. Заказ")
    void orderPostgreSQL() {
        double preBillingProductPrice;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            PostgreSqlAstraOrderPage orderPage = new PostgreSqlAstraOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(accessGroup);
            orderPage.getPrebillingCostElement().shouldBe(Condition.visible);
            preBillingProductPrice = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
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
        checkOrderCost(preBillingProductPrice, new PostgreSqlAstraPage(product));
    }

    @Test
    @TmsLink("1236733")
    @Order(2)
    @DisplayName("UI PostgreSQLAstra. Проверка развертывания заказа в истории действий")
    void checkHeaderHistoryTable() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.getBtnGeneralInfo().click();
        pSqlPage.checkHeadersHistory();
        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(5)
    @TmsLink("993396")
    @Disabled
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
    @DisplayName("UI PostgreSQLAstra. Максимизировать max_connections")
    void changeMaxConnections() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeMaxConnections("284"));
    }

    @Test
    @Order(9)
    @TmsLink("993389")
    @DisplayName("UI PostgreSQLAstra. Расширить точку монтирования")
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
    @DisplayName("UI PostgreSQLAstra. Добавить БД")
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
    @DisplayName("UI PostgreSQLAstra. Добавить пользователя БД")
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
    @TmsLink("1171237")
    @DisplayName("UI PostgreSQLAstra. Актуализировать extensions")
    void updateExtensions() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.updateExtensions(nameDb));
    }

    @Test
    @Order(20)
    @TmsLink("1171241")
    @DisplayName("UI PostgreSQLAstra. Изменить extensions")
    void changeExtensions() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeExtensions(nameDb));
    }


    @Test
    @Order(21)
    @TmsLink("993400")
    @DisplayName("UI PostgreSQLAstra. Удалить БД")
    void removeDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.removeDb(nameDb));
    }

    @Test
    @Order(22)
    @TmsLinks({@TmsLink("993397"), @TmsLink("993401")})
    @Disabled
    @DisplayName("UI PostgreSQLAstra. Выключить принудительно / Включить")
    void stopHard() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopHard);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::start);
    }

    @Test
    @TmsLinks({@TmsLink("1091014"), @TmsLink("1091010")})
    @Order(23)
    @DisplayName("UI PostgreSQLAstra. Удалить и добавить группу доступа")
    void deleteGroup() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.deleteGroup("user");
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        pSqlPage.addGroup("superuser", Collections.singletonList(accessGroup.getPrefixName()));
    }

    @Test
    @TmsLink("1091055")
    @Order(24)
    @DisplayName("UI PostgreSQLAstra. Изменить состав группы доступа")
    void updateGroup() {
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.updateGroup("superuser",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @Order(25)
    @TmsLink("1429077")
    @DisplayName("UI PostgreSQLAstra. Обновить минорную версию СУБД")
    void updateMinorVersion() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateMinorVersion);
    }

    @Test
    @Order(26)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Показать удаленные БД")
    void showDeleteDB() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.showDeleteDB(nameDb);
    }


    @Test
    @Order(27)
    @TmsLink("1296731")
    @EnabledIfEnv("prod")
    @DisplayName("UI PostgreSQLAstra. Мониторинг ОС")
    void monitoringOs() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        pSqlPage.checkClusterMonitoringOs();
    }

//    @Test
//    @Order(100)
//    @TmsLink("993399")
//    @DisplayName("UI PostgreSQLAstra. Удаление продукта")
//    void delete() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.delete();
//    }
}