package ui.cloud.tests.orders.postgreSqlClusterAstra;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.PostgresSQLCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import api.Tests;
import ui.cloud.pages.*;
import ui.elements.Alert;
import ui.elements.Graph;
import ui.extesions.ConfigExtension;
import ui.extesions.InterceptTestExtension;
import ui.extesions.UiProductTest;

import java.time.Duration;

import static core.helper.StringUtils.$x;
@Epic("UI Продукты")
@Feature("PostgreSQL Cluster Astra Linux")
@Tags({@Tag("ui"), @Tag("ui_postgre_sql_cluster_astra")})
@ExtendWith(ConfigExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
public class UiPostgreSqlClusterAstraLinuxTest extends UiProductTest {

    PostgresSQLCluster product;
    //product = PostgresSQLCluster.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/4ae82e4a-50fd-4786-afc5-cc4ff8f0bab5/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
    String nameDb = "at_db";
    String shortNameUserDB = "at_user";
    String fullNameUserDB = "at_db_at_user";
    SelenideElement node = $x("(//td[.='postgresql'])[1]");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("851404")
    @Order(1)
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Заказ")
    void orderPostgreSqlCluster() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            PostgreSqlClusterAstraOrderPage orderPage = new PostgreSqlClusterAstraOrderPage();
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
                    .getRowElementByColumnValue("Продукт",
                            orderPage.getLabelValue())
                    .hover()
                    .click();
            PostgreSqlClusterAstraPage pSqlPages = new PostgreSqlClusterAstraPage(product);
            pSqlPages.waitChangeStatus(Duration.ofMinutes(25));
            pSqlPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        Assertions.assertEquals(preBillingProductPrice, pSqlPage.getCostOrder(), 0.01);
    }


    @Test
    @TmsLink("1236731")
    @Order(2)
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        pSqlPage.checkHeadersHistory();
        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(5)
    @TmsLink("851706")
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
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Изменить max_connections")
    void changeMaxConnections() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeMaxConnections("284"));
    }


    @Test
    @Order(9)
    @TmsLink("851714")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Расширить диск")
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
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Создание БД")
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
    @TmsLink("851771")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Сбросить пароль владельца БД")
    void resetPasswordDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::resetPasswordDb);
    }

    @Test
    @Order(16)
    @TmsLink("851725")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Добавить пользователя")
    void addUserDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
    }

    @Test
    @Order(17)
    @TmsLink("851793")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.resetPasswordUserDb(fullNameUserDB));

    }

    @Test
    @Order(18)
    @TmsLink("851797")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удалить пользователя БД")
    void deleteUserDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.addUserDb(nameDb, shortNameUserDB, "Пользователь для тестов"));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deleteUserDb(fullNameUserDB));
    }

    @Test
    @Order(19)
    @TmsLink("851796")
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Удаление БД")
    void removeDb() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createDb(nameDb));
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.removeDb(nameDb));
    }

    @Test
    @Order(20)
    @TmsLinks({@TmsLink("851703"), @TmsLink("851698")})
    @Disabled
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Выключить принудительно / Включить")
    void stopHard() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopHard);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::start);
    }

    @Test
    @Order(21)
    @TmsLink("851702")
    @Disabled
    @DisplayName("UI PostgreSQL Cluster Astra Linux. Выключить")
    void stopSoft() {
        PostgreSqlClusterAstraPage pSqlPage = new PostgreSqlClusterAstraPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.LESS, pSqlPage::stopSoft);
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
