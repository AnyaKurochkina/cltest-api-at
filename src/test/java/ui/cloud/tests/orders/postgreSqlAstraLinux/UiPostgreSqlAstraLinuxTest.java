package ui.cloud.tests.orders.postgreSqlAstraLinux;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.PostgreSQL;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static core.helper.StringUtils.$x;
import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;
import static ui.elements.TypifiedElement.scrollCenter;

@Epic("UI Продукты")
@Feature("PostgreSQL (Astra Linux)")
@Tags({@Tag("ui"), @Tag("ui_postgre_sql_astra")})
public class UiPostgreSqlAstraLinuxTest extends UiProductTest {

    private PostgreSQL product;// = PostgreSQL.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/db/orders/d4ac7f9d-60fc-480e-ba5f-81273f22b273/main?context=proj-iv550odo9a&type=project&org=vtb");

    String nameDb = "at_db";
    String limit = "20";
    String shortNameUserDB = "at_user";
    String fullNameUserDB = "at_db_at_user";

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
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
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            PostgreSqlAstraOrderPage orderPage = new PostgreSqlAstraOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(product.accessGroup());
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
    @Order(3)
    @TmsLink("993396")
//    @Disabled
    @DisplayName("UI PostgreSQLAstra. Перезагрузить")
    void restart() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::restart);
    }

    @Test
    @Order(4)
    @TmsLink("993391")
    @DisplayName("UI PostgreSQLAstra. Получить актуальную конфигурацию")
    void getActualConfiguration() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::getActualConfiguration);
    }

    @Test
    @Order(5)
    @TmsLink("993402")
    @DisplayName("UI PostgreSQLAstra. Изменить default_transaction_isolation на REPEATABLE READ")
    void changeTransactionIsolation() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeTransactionIsolation("REPEATABLE READ"));
    }

    @Test
    @Order(6)
    @TmsLink("993392")
    @DisplayName("UI PostgreSQLAstra. Максимизировать max_connections")
    void changeMaxConnections() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeMaxConnections("367"));
    }

    @Test
    @Order(7)
    @TmsLink("993389")
    @DisplayName("UI PostgreSQLAstra. Расширить точку монтирования")
    void expandDisk() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.enlargeDisk("/pg_data", "20", $x("(//td[.='vm'])[1]")));
    }

    @Test
    @Order(8)
    @TmsLink("993393")
    @DisplayName("UI PostgreSQLAstra. Изменить конфигурацию")
    void changeConfiguration() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::changeConfiguration);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(9)
    @TmsLink("994656")
    @DisplayName("UI PostgreSQLAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::checkConfiguration);
    }

    @Test
    @Order(10)
    @TmsLink("993398")
    @DisplayName("UI PostgreSQLAstra. Добавить БД")
    void createDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
    }

    @Test
    @Order(11)
    @TmsLink("993388")
    @DisplayName("UI PostgreSQLAstra. Сбросить пароль владельца БД")
    void resetPasswordDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::resetPasswordDb);
    }

    @Test
    @Order(12)
    @TmsLink("993394")
    @DisplayName("UI PostgreSQLAstra. Добавить пользователя БД")
    void addUserDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
    }

    @Test
    @Order(13)
    @TmsLink("993387")
    @DisplayName("UI PostgreSQLAstra. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.resetPasswordUserDb(fullNameUserDB));
    }

    @Test
    @Order(14)
    @TmsLink("993395")
    @DisplayName("UI PostgreSQLAstra. Удалить пользователя БД")
    void deleteUserDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deleteUserDb(fullNameUserDB));
    }

    @Test
    @Order(15)
    @TmsLink("1171237")
    @DisplayName("UI PostgreSQLAstra. Актуализировать extensions")
    void updateExtensions() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.updateExtensions(nameDb));
    }

    @Test
    @Order(16)
    @TmsLink("1171241")
    @DisplayName("UI PostgreSQLAstra. Изменить extensions")
    void changeExtensions() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeExtensions(nameDb));
    }


    @Test
    @Order(17)
    @TmsLink("")
    @EnabledIfEnv("prod")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Назначить предел подключений")
    void setLimitConnection() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.setLimitConnection(limit));
    }

    @Test
    @Order(18)
    @TmsLink("")
    @EnabledIfEnv("prod")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удалить предел подключений")
    void deleteLimitConnection() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.setLimitConnection(limit));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deleteLimitConnection(limit));
    }

    @Test
    @Order(19)
    @TmsLink("993400")
    @DisplayName("UI PostgreSQLAstra. Удалить БД")
    void removeDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.removeDb(nameDb));
    }

    @Test
    @Order(20)
    @TmsLinks({@TmsLink("993397"), @TmsLink("993401")})
//    @Disabled
    @DisplayName("UI PostgreSQLAstra. Выключить принудительно / Включить")
    void stopHard() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopHard);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::start);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @TmsLinks({@TmsLink("1091014"), @TmsLink("1091010")})
    @Order(21)
    @DisplayName("UI PostgreSQLAstra. Удалить и добавить группу доступа")
    void deleteGroup() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.deleteGroup("superuser");
        pSqlPage.addGroup("superuser", Collections.singletonList(product.accessGroup()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @TmsLink("1091055")
    @Order(22)
    @DisplayName("UI PostgreSQLAstra. Изменить состав группы доступа")
    void updateGroup() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.updateGroup("superuser",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @Order(23)
    @TmsLink("1429077")
    @DisplayName("UI PostgreSQLAstra. Обновить минорную версию СУБД")
    void updateMinorVersion() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateMinorVersion);
    }

    @Test
    @Order(24)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Актуализировать версию СУБД")
    void updateVersionDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateVersionDb
        );
    }

    @Test
    @Order(25)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Добавить точку монтирования /pg_backup")
    void adPgBackup
            () {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::adPgBackup
        );
    }

    @Test
    @Order(26)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Добавить точку монтирования /pg_audit")
    void addPgAudit
            () {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::addPgAudit
        );
    }

    @Test
    @Order(27)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Добавить точку монтирования /pg_walarchive")
    void addPgWalarchive
            () {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::addPgWalarchive
        );
    }

    @Test
    @Order(28)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Обновить ОС")
    void updateOs
            () {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateOs
        );
    }

    @Test
    @Order(29)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Показать удаленные БД")
    void showDeleteDB() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.showDeleteDB(nameDb);
    }

    @Test
    @Order(30)
    @TmsLink("1296731")
    @DisplayName("UI PostgreSQLAstra. Мониторинг ОС")
    void monitoringOs() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        pSqlPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(31)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Установить Ключ-Астром")
    void addKeyAstrom() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::addKeyAstrom);
    }

    @Test
    @Order(32)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Удалить Ключ-Астром")
    void delKeyAstrom() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::delKeyAstrom);
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