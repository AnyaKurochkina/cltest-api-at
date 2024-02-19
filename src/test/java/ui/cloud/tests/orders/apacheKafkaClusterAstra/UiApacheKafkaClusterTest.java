package ui.cloud.tests.orders.apacheKafkaClusterAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ApacheKafkaCluster;
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
import java.util.List;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;
import static ui.elements.TypifiedElement.scrollCenter;

@Epic("UI Продукты")
@Feature("ApacheKafkaCluster")
@Tags({@Tag("ui"), @Tag("ui_ApacheKafkaCluster")})
public class UiApacheKafkaClusterTest extends UiProductTest {
    private final List<String> name = Arrays.asList("name1", "name2");
    private final List<Acl> args = Arrays.asList(
            Acl.builder().certificate("cert1").type(Acl.Type.BY_NAME).mask("name1").build(),
            Acl.builder().certificate("cert2").type(Acl.Type.BY_MASK).mask("mask").build());

    private ApacheKafkaCluster product;// =ApacheKafkaCluster.builder().platform("OpenStack").segment("dev-srv-app").build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/application_integration/orders/e2eb9edf-4f33-4dad-b1a1-8afc5145fc14/main?context=proj-ahjjqmlgnm&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("851992")
    @Order(1)
    @DisplayName("UI ApacheKafkaCluster. Заказ")
    void orderApacheKafkaCluster() {
        double preBillingProductPrice;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            ApacheKafkaClusterOrderPage orderPage = new ApacheKafkaClusterOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getOsVersionKafka().set("2.13-2.4.1");
            orderPage.getConfigCluster().set("one_dc:kafka-4:zookeeper-3");
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(accessGroup);
            preBillingProductPrice = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            ApacheKafkaClusterPage pSqlPages = new ApacheKafkaClusterPage(product);
            pSqlPages.waitChangeStatus(Duration.ofMinutes(25));
            pSqlPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        checkOrderCost(preBillingProductPrice, new ApacheKafkaClusterPage(product));
    }

    @Test
    @TmsLink("1319683")
    @Order(2)
    @DisplayName("UI ApacheKafkaCluster. Проверка графа в истории действий")
    void checkHeaderHistoryTable() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.getBtnGeneralInfo().click();
        pSqlPage.checkHeadersHistory();
        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }


    @Test
    @Order(4)
    @TmsLink("852003")
    @Disabled
    @DisplayName("UI ApacheKafkaCluster. Расширить диск")
    void enlargeDisk() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.enlargeDisk("/app", "20", new Table("Роли узла").getRowByIndex(0)));
    }


    @Test
    @Order(5)
    @TmsLink("1291819")
    @DisplayName("UI ApacheKafkaCluster. Обновить кластерный сертификат")
    void updateCertificate() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateCertificate);
    }

    @Test
    @Order(6)
    @TmsLink("1305356")
    @DisplayName("UI ApacheKafkaCluster. Обновить кластерный сертификат(аварийно)")
    void updateCertificateEmergency() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateCertificateEmergency);
    }


    @Disabled
    @Test
    @Order(7)
    @TmsLink("1161546")
    @DisplayName("UI ApacheKafkaCluster. Обновить ОС на кластере Kafka")
    void updateOs() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateOs);
    }

    @Test
    @Order(8)
    @TmsLink("915049")
    @DisplayName("UI ApacheKafkaCluster. Обновление дистрибутива ВТБ-Kafka")
    void updateDistributionVtb() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateDistributionVtb);
    }

    @Test
    @Order(99)
    @TmsLink("1091895")
    @DisplayName("UI ApacheKafkaCluster. Обновление ядра Kafka до версии 2.8.2")
    void updateKernelVtb() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::updateKernelVtb);
    }

    @Test
    @Order(10)
    @TmsLink("852008")
    @DisplayName("UI ApacheKafkaCluster. Прислать конфигурацию брокера Kafka")
    void sendConfiguration() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::sendConfiguration);
    }

    @Test
    @Order(11)
    @TmsLink("852005")
    @DisplayName("UI ApacheKafkaCluster. Синхронизировать конфигурацию кластера Kafka")
    void synchronizeCluster() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::synchronizeCluster);
    }

    @Test
    @Order(12)
    @TmsLink("1091986")
    @DisplayName("UI ApacheKafkaCluster. Вертикальное масштабирование")
    void changeConfiguration() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::changeConfiguration);
    }

    @Test
    @Order(13)
    @TmsLink("1092021")
    @DisplayName("UI ApacheKafkaCluster. Увеличить дисковое пространство")
    void expandDisk() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.enlargeDisk("/app", "20", new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Order(14)
    @TmsLink("851993")
    @DisplayName("UI ApacheKafkaCluster.Пакетное создание Topic-ов")
    void createTopics() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createTopics(name));
    }

    @Test
    @Order(15)
    @TmsLink("852010")
    @DisplayName("UI ApacheKafkaCluster.Изменить параметр топиков Kafka Cluster")
    void changeParamTopics() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeParamTopics(name));
    }

    @Test
    @Order(16)
    @TmsLink("915047")
    @DisplayName("UI ApacheKafkaCluster. Изменить имя кластера")
    void changeNameCluster() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.changeNameCluster("kafka-clust-914"));
    }

    @Test
    @Order(16)
    @TmsLink("852011")
    @DisplayName("UI ApacheKafkaCluster.Проверка имени кластера на уникальность")
    void checkNameCluster() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::checkNameCluster);
    }


    @Test
    @Order(17)
    @TmsLink("851995")
    @DisplayName("UI ApacheKafkaCluster.Пакетное создание ACL Kafka на топики")
    void createAclTopics() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->
                new ApacheKafkaClusterPage(ApacheKafkaCluster.builder().build()).createAclTopics(args));
    }

    @Test
    @Order(18)
    @TmsLink("851996")
    @DisplayName("UI ApacheKafkaCluster.Пакетное удаление ACL Kafka на топики")
    void dellAclTopics() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> new ApacheKafkaClusterPage(ApacheKafkaCluster.builder().build()).dellAclTopics(args));
    }

    @Test
    @Order(19)
    @TmsLink("851999")
    @DisplayName("UI ApacheKafkaCluster.Пакетное создание ACL на транзакцию Kafka")
    void createAclTrans() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->
                new ApacheKafkaClusterPage(ApacheKafkaCluster.builder().build()).createAclTransaction(args));
    }

    @Test
    @Order(20)
    @TmsLink("852000")
    @DisplayName("UI ApacheKafkaCluster.Пакетное удаление ACL на транзакцию Kafka")
    void dellAclTrans() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> new ApacheKafkaClusterPage(ApacheKafkaCluster.builder().build()).dellAclTransaction(args));
    }

    @Test
    @Order(21)
    @TmsLink("851994")
    @DisplayName("UI ApacheKafkaCluster.Пакетное удаление Topic-ов Kafka")
    void dellTopics() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.dellTopics(name));
    }

    @Test
    @Order(22)
    @TmsLink("982962")
    @DisplayName("UI ApacheKafkaCluster.Создание идемпотентных ACL Kafka")
    void createAclIdempotent() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createAclIdempotent("1"));
    }

    @Test
    @Order(23)
    @TmsLink("1549052")
    @DisplayName("UI ApacheKafkaCluster.Пакетное создание квот Kafka")
    void createQuotas() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.createQuotas("131072"));
    }

    @Test
    @Order(24)
    @TmsLink("1549051")
    @DisplayName("UI ApacheKafkaCluster.Пакетное удаление квот Kafka")
    void deleteQuotas() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.deleteQuotas("131072"));
    }


    @Test
    @Order(25)
    @TmsLink("982961")
    @DisplayName("UI ApacheKafkaCluster.Удаление идемпотентных ACL Kafka")
    void dellAclIdempotent() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () -> pSqlPage.dellAclIdempotent("1"));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(26)
    @TmsLink("852012")
    @DisplayName("UI ApacheKafkaCluster.Проверить конфигурацию")
    void checkConfiguration() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::checkConfiguration);
    }

    @Test
    @Order(27)
    @TmsLink("1216972")
    @DisplayName("UI ApacheKafkaCluster. Мониторинг ОС")
    void monitoringOs() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        pSqlPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(28)
    @TmsLink("")
    @DisplayName("UI ApacheKafkaCluster. Горизонтальное масштабирование")
    void horizontalScaling() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::horizontalScaling);
    }

    @Test
    @Order(100)
    @TmsLink("852007")
    @DisplayName("UI ApacheKafkaCluster. Удаление продукта")
    void delete() {
        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.ZERO, pSqlPage::delete);
    }
}
