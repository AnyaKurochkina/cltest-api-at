package ui.cloud.tests.orders.postgreSqlClusterAstra;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.PostgresSQLCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.*;
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
@Feature("PostgreSQL Cluster Astra Linux")
@Tags({@Tag("ui"), @Tag("ui_postgre_sql_cluster_astra")})
public class UiPostgreSqlClusterAstraLinuxTest extends UiProductTest {

    PostgresSQLCluster product = PostgresSQLCluster.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/db/orders/e3cc489c-0e82-4ccf-8a8d-fdfc4ca1aa84/main?context=proj-iv550odo9a&type=project&org=vtb");
    String nameDb = "at_db";
    String nameSlot = "at_slot";
    String limit = "20";
    String shortNameUserDB = "at_user";
    String fullNameUserDB = "at_db_at_user";
    SelenideElement node = $x("(//td[.='postgresql'])[1]");
    SelenideElement node2 = $x("(//td[.='at_db'])[1]");


    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("851404")
    @Order(1)
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Заказ")
    void orderPostgreSqlCluster() {
        double preBillingProductPrice;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectCategory("Базы данных")
                    .expandProductsList()
                    .selectProduct(product.getProductName());
            PostgreSqlClusterAstraOrderPage orderPage = new PostgreSqlClusterAstraOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(accessGroup);
            orderPage.getRoleSelect().set("user");
            orderPage.getPrebillingCostElement().shouldBe(Condition.visible);
            preBillingProductPrice = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowElementByColumnValue("Продукт",
                            orderPage.getLabelValue())
                    .hover()
                    .click();
            PostgreSqlClusterAstraPage pSqlPages = new PostgreSqlClusterAstraPage(product);
            pSqlPages.waitChangeStatus(Duration.ofMinutes(30));
            pSqlPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        checkOrderCost(preBillingProductPrice, new PostgreSqlClusterAstraPage(product));
    }


    @Test
    @TmsLink("1236731")
    @Order(2)
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.checkHeadersHistory();
        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(5)
    @TmsLink("851706")
    @Disabled
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Перезагрузить по питанию")
    void restart() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::restart);
    }

    @Test
    @Order(6)
    @TmsLink("851809")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Получить актуальную конфигурацию")
    void getActualConfiguration() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::getActualConfiguration);
    }

    @Test
    @Order(7)
    @TmsLink("851813")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Изменить default_transaction_isolation на REPEATABLE READ")
    void changeTransactionIsolation() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeTransactionIsolation("REPEATABLE READ"));
    }

    @Test
    @Order(8)
    @TmsLink("851811")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Максимизировать max_connections")
    void changeMaxConnections() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeMaxConnections("284"));
    }


    @Test
    @Order(9)
    @TmsLink("851714")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Расширить точку монтирования /pg_data")
    void expandDisk() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.enlargeDisk("/pg_data", "20", node));
    }

    @Test
    @Order(11)
    @TmsLink("852936")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Проверить конфигурацию")
    void vmActCheckConfig() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::checkConfiguration);
    }

    @Test
    @Order(12)
    @TmsLink("851716")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Добавить БД")
    void createDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
    }


    @Test
    @Order(13)
    @TmsLink("1171491")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Актуализировать extensions")
    void updateExtensions() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.updateExtensions(nameDb));
    }

    @Test
    @Order(14)
    @TmsLink("1171492")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Изменить extensions")
    void changeExtensions() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeExtensions(nameDb));
    }

    @Test
    @Order(15)
    @TmsLink("1087974")
    @EnabledIfEnv("prod")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Назначить предел подключений")
    void setLimitConnection() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.setLimitConnection(limit));
    }

    @Test
    @Order(16)
    @TmsLink("1111764")
    @EnabledIfEnv("prod")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удалить предел подключений")
    void deleteLimitConnection() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.setLimitConnection(limit));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deleteLimitConnection(limit));
    }

    @Test
    @Order(17)
    @TmsLink("851771")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Сбросить пароль владельца БД")
    void resetPasswordDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::resetPasswordDb);
    }

    @Test
    @Order(18)
    @TmsLink("851725")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Добавить пользователя")
    void addUserDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
    }

    @Test
    @Order(19)
    @TmsLink("851793")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.resetPasswordUserDb(fullNameUserDB));

    }

    @Test
    @Order(20)
    @TmsLink("851797")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удалить пользователя БД")
    void deleteUserDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deleteUserDb(fullNameUserDB));
    }

    @Test
    @Order(21)
    @TmsLink("851796")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удалить БД")
    void removeDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.removeDb(nameDb));
    }

    @Test
    @Order(22)
    @TmsLinks({@TmsLink("851703"), @TmsLink("851698")})
    @Disabled
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Выключить принудительно / Включить")
    void stopHard() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopHard);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::start);
    }

    @Test
    @Order(23)
    @TmsLink("851702")
    @Disabled
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Выключить")
    void stopSoft() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopSoft);
    }

    @Test
    @TmsLinks({@TmsLink("1091089"), @TmsLink("1091067")})
    @Order(24)
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Добавление/удаление группы доступа")
    void deleteGroup() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.deleteGroup("user");
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        pSqlPage.addGroup("superuser", Collections.singletonList(accessGroup.getPrefixName()));
    }

    @Test
    @TmsLink("1091118")
    @Order(25)
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Изменение группы доступа")
    void updateGroup() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.updateGroup("superuser",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @Order(26)
    @TmsLink("1429760")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Обновить ОС")
    void updateOs() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateOs);
    }

    @Test
    @Order(27)
    @TmsLink("1151320")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Изменить конфигурацию нод СУБД")
    void changeConfiguration() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::changeConfiguration);
    }

    @Test
    @Order(28)
    @TmsLink("1429759")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Обновить минорную версию СУБД")
    void updateMinorVersion() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateMinorVersion);
    }

    @Test
    @Order(29)
    @TmsLink("1296732")
    @EnabledIfEnv("prod")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Мониторинг ОС")
    void monitoringOs() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        pSqlPage.checkClusterMonitoringOs();
    }
    @Test
    @Order(30)
    @TmsLink("")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Настроить кластер для интеграции с Debezium")
    void setIntegrationDebezium() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::setIntegrationDebezium);
    }

        @Test
    @Order(31)
    @TmsLink("")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Настроить БД для интеграции с Debezium")
    void setDbIntegrationDebezium() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.setDbIntegrationDebezium("qa_order_service_admin"));
    }
    @Test
    @Order(32)
    @TmsLink("")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Создать логический слот")
    void createLogicSlot() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createLogicSlot(nameDb,nameSlot,node2));
    }
    @Test
    @Order(33)
    @TmsLink("")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Создать публикацию")
    void createPost() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createPost(nameDb,node2));
    }
    @Test
    @Order(34)
    @TmsLink("")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удалить логический слот")
    void deleteLogicSlot() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createLogicSlot(nameDb,nameSlot,node2));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deleteLogicSlot(node2,nameSlot));
    }
    @Test
    @Order(35)
    @TmsLink("")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удалить публикацию")
    void deletePost() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        //pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createPost(nameDb,node2));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deletePost(node2));
    }


    @Test
    @Order(100)
    @TmsLink("851707")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удаление продукта")
    void delete() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.delete();
    }

}
