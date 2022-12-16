package ui.cloud.tests.orders.scillaDbClusterAstra;


import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;

import models.cloud.orderService.products.ScyllaDbCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
@Epic("UI Продукты")
@Feature("ScyllaDbClusterAstra")
@Tags({@Tag("ui"), @Tag("ui_scylla_db_cluster_astra")})
public class UiScyllaDbClusterAstraTest extends UiProductTest{

    ScyllaDbCluster product =ScyllaDbCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/db/orders/40aa4245-c7ce-4e2a-95bf-d57de0c4d170/main?context=proj-1oob0zjo5h&type=project&org=vtb");
    String nameDb = "at_db";
    String shortNameUserDB = "at_user";

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
       /// product.setProductName(""); Для Rhel версии
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }
//
//    @Test
//    @TmsLink("")
//    @Order(1)
//    @DisplayName("UI Scylla_db_cluster_astra. Заказ")
//    void orderScyllaDB() {
//        double preBillingProductPrice;
//        try {
//            new IndexPage()
//                    .clickOrderMore()
//                    .selectProduct(product.getProductName());
//            ScyllaDbClusterOrderPage orderPage = new ScyllaDbClusterOrderPage();
//            orderPage.getOsVersion().select(product.getOsVersion());
//            orderPage.getSegment().selectByValue(product.getSegment());
//            orderPage.getPlatform().selectByValue(product.getPlatform());
//            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
//            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
//            orderPage.getGroup().select(accessGroup.getPrefixName());
//            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
//            preBillingProductPrice = EntitiesUtils.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
//            orderPage.orderClick();
//            new OrdersPage()
//                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
//                    .getElementByColumn("Продукт")
//                    .hover()
//                    .click();
//            ScyllaDbClusterPage scyllaPages = new ScyllaDbClusterPage(product);
//            scyllaPages.waitChangeStatus(Duration.ofMinutes(25));
//            scyllaPages.checkLastAction("Развертывание");
//        } catch (Throwable e) {
//            product.setError(e.toString());
//            throw e;
//        }
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        Assertions.assertEquals(preBillingProductPrice, scyllaPage.getCostOrder(), 0.01);
//    }
//
//
//    @Test
//    @TmsLink("1236730")
//    @Order(2)
//    @DisplayName("UI Scylla_db_cluster_astra. Проверка полей заказа")
//    void checkHeaderHistoryTable() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
//        scyllaPage.checkHeadersHistory();
//        scyllaPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
//        new Graph().checkGraph();
//    }
//
//    @Test
//    @Order(9)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Расширить диск")
//    void expandDisk() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.runActionWithCheckCost(CompareType.MORE, () -> scyllaPage.enlargeDisk("/app/scylla/data", "20", new Table("Роли узла").getRow(0).get()));
//    }
//
//    @Test
//    @Order(11)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Проверить конфигурацию")
//    void vmActCheckConfig() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.checkConfiguration(new Table("Роли узла").getRow(0).get()));
//    }
//
//    @Test
//    @Order(12)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Создание БД")
//    void createDb() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
//    }
//
//    @Test
//    @Order(16)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Добавить пользователя")
//    void addUserDb() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
//    }
//
//    @Test
//    @Order(17)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Сбросить пароль пользователя БД")
//    void resetPasswordUserDb() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.resetPasswordUserDb(shortNameUserDB));
//    }
//
//    @Test
//    @Order(18)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Удалить пользователя БД")
//    void
//    deleteUserDb() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.deleteUserDb(shortNameUserDB));
//    }
//
//    @Test
//    @Order(19)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Удаление БД")
//    void removeDb() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.removeDb(nameDb));
//    }

//    @Test
//    @Order(16)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Добавить права доступа пользователю БД")
//    void addRightsUser() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
//        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addRightsUser(nameDb,shortNameUserDB));
//
//    }

    @Test
    @Order(16)
    @TmsLink("")
    @DisplayName("UI Scylla_db_cluster_astra. Добавить права доступа пользователю БД")
    void deleteRightsUser() {
        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addRightsUser(nameDb,shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.deleteRightsUser(shortNameUserDB));

    }
//
//    @Test
//    @Order(17)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Мониторинг ОС")
//    void monitoringOs() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.checkClusterMonitoringOs();
//    }

//    @Test
//    @Order(100)
//    @TmsLink("")
//    @DisplayName("UI Scylla_db_cluster_astra. Удаление продукта")
//    void delete() {
//        ScyllaDbClusterPage scyllaPage = new ScyllaDbClusterPage(product);
//        scyllaPage.delete();
//    }

}
