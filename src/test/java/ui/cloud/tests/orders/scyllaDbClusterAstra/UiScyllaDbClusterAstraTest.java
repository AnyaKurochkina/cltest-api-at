package ui.cloud.tests.orders.scyllaDbClusterAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.ScyllaDbCluster;
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

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("ScyllaDbClusterAstra")
@Tags({@Tag("ui"), @Tag("ui_scylla_db_cluster_astra")})
public class UiScyllaDbClusterAstraTest extends UiProductTest {

    private ScyllaDbCluster product; // =ScyllaDbCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/ba6960f7-b68c-4920-8d14-d2819060424f/main?context=proj-ln4zg69jek&type=project&org=vtb");
    private final String nameDb = "at_db";
    private final String shortNameUserDB = "at_user";

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        //product.setProductName("ScyllaDB Cluster RHEL"); //Для RHEL версии
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1335483")
    @Order(1)
    @DisplayName("UI Scylla_db_cluster_astra. Заказ")
    void orderScyllaDB() {
        double preBillingProductPrice;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .expandProductsList()
                    .selectProduct(product.getProductName());
            ScyllaDbClusterOrderPage orderPage = new ScyllaDbClusterOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            if (product.isDev())
                orderPage.getGroupSelect().set(accessGroup);
            preBillingProductPrice = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            ScyllaDbClusterPage scyllaPages = new ScyllaDbClusterPage(product);
            scyllaPages.waitChangeStatus(Duration.ofMinutes(30));
            scyllaPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        checkOrderCost(preBillingProductPrice, new ScyllaDbClusterPage(product));
    }


    @Test
    @TmsLink("1368046")
    @Order(2)
    @DisplayName("UI Scylla_db_cluster_astra. Проверка графа в истории действий")
    void checkHeaderHistoryTable() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.checkHeadersHistory();
        scyllaPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(9)
    @TmsLink("1335489")
    @DisplayName("UI Scylla_db_cluster_astra. Расширить точку монтирования")
    void expandDisk() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.MORE, () -> scyllaPage
                .enlargeDisk("/app/scylla/data", "20", new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(11)
    @TmsLink("1335485")
    @DisplayName("UI Scylla_db_cluster_astra. Проверить конфигурацию")
    void vmActCheckConfig() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.checkConfiguration(new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Order(12)
    @TmsLink("1335490")
    @DisplayName("UI Scylla_db_cluster_astra. Добавить БД")
    void createDb() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
    }

    @Test
    @Order(16)
    @TmsLink("1335493")
    @DisplayName("UI Scylla_db_cluster_astra. Добавить пользователя")
    void addUserDb() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
    }

    @Test
    @Order(17)
    @TmsLink("1335484")
    @DisplayName("UI Scylla_db_cluster_astra. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.resetPasswordUserDb(shortNameUserDB));
    }

    @Test
    @Order(18)
    @TmsLink("1335486")
    @DisplayName("UI Scylla_db_cluster_astra. Удалить пользователя БД")
    void
    deleteUserDb() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.deleteUserDb(shortNameUserDB));
    }

    @Test
    @Order(19)
    @TmsLink("1335492")
    @DisplayName("UI Scylla_db_cluster_astra. Удаление БД")
    void removeDb() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.removeDb(nameDb));
    }

    @Test
    @Order(16)
    @TmsLink("1335494")
    @DisplayName("UI Scylla_db_cluster_astra. Добавить права доступа пользователю БД")
    void addRightsUser() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addRightsUser(nameDb, shortNameUserDB));
    }

    @Test
    @Order(16)
    @TmsLink("1335481")
    @DisplayName("UI Scylla_db_cluster_astra. Удалить права доступа пользователю БД")
    void deleteRightsUser() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addRightsUser(nameDb, shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.deleteRightsUser(shortNameUserDB));

    }

    @Test
    @Order(22)
    @TmsLink("1368052")
    @DisplayName("UI Scylla_db_cluster_astra. Мониторинг ОС")
    void monitoringOs() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.checkClusterMonitoringOs();
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @TmsLinks({@TmsLink(""), @TmsLink("")})
    @Order(25)
    @DisplayName("UI Scylla_db_cluster_astra. Добавление/удаление группы доступа")
    void deleteGroup() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.deleteGroup("superuser");
        scyllaPage.addGroup("superuser", Collections.singletonList(product.accessGroup()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @TmsLink("")
    @Order(26)
    @DisplayName("UI Scylla_db_cluster_astra. Изменение группы доступа")
    void updateGroup() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.updateGroup("superuser",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @TmsLink("")
    @Order(27)
    @DisplayName("UI Scylla_db_cluster_astra. Обновить ОС")
    void updateOs() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, scyllaPage::updateOs);
    }

    @Test
    @Order(100)
    @TmsLink("1335480")
    @DisplayName("UI Scylla_db_cluster_astra. Удаление продукта")
    void delete() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.delete();
    }
}
