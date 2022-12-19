package ui.cloud.tests.orders.apacheKafkaClusterAstra;

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
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Epic("UI Продукты")
@Feature("ApacheKafkaCluster")
@Tags({@Tag("ui"), @Tag("ui_ApacheKafkaCluster")})
public class UiApacheKafkaClusterTest extends UiProductTest {
    List<String> name= Arrays.asList("1","2");
    ApacheKafkaCluster product=ApacheKafkaCluster.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/application_integration/orders/25fec085-7636-4fd1-9280-151afe496e4e/main?context=proj-ln4zg69jek&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

//    @Test
//    @TmsLink("851992")
//    @Order(1)
//    @DisplayName("UI ApacheKafkaCluster. Заказ")
//    void orderPostgreSQL() {
//        double preBillingProductPrice;
//        try {
//            new IndexPage()
//                    .clickOrderMore()
//                    .selectProduct(product.getProductName());
//            ApacheKafkaClusterOrderPage orderPage = new ApacheKafkaClusterOrderPage();
//            orderPage.getOsVersion().select(product.getOsVersion());
//            orderPage.getSegment().selectByValue(product.getSegment());
//            orderPage.getPlatform().selectByValue(product.getPlatform());
//            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
//            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
//            orderPage.getGroup().select(accessGroup.getPrefixName());
//            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
//            preBillingProductPrice = EntitiesUtils.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
//            EntitiesUtils.clickOrder();
//            new OrdersPage()
//                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
//                    .getElementByColumn("Продукт")
//                    .hover()
//                    .click();
//            ApacheKafkaClusterPage pSqlPages = new ApacheKafkaClusterPage(product);
//            pSqlPages.waitChangeStatus(Duration.ofMinutes(25));
//            pSqlPages.checkLastAction("Развертывание");
//        } catch (Throwable e) {
//            product.setError(e.toString());
//            throw e;
//        }
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        Assertions.assertEquals(preBillingProductPrice, pSqlPage.getCostOrder(), 0.01);
//    }


//    @Test
//    @TmsLink("1319683")
//    @Order(2)
//    @DisplayName("UI ApacheKafkaCluster. Проверка полей заказа")
//    void checkHeaderHistoryTable() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
//        pSqlPage.checkHeadersHistory();
//        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
//        new Graph().checkGraph();
//    }
//

//
//    @Test
//    @Order(4)
//    @TmsLink("852003")
//    @DisplayName("UI ApacheKafkaCluster. Расширить диск")
//    void enlargeDisk() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.MORE, () ->pSqlPage.enlargeDisk("/app", "20", new Table("Роли узла").getRowByIndex(0)));
//    }
//
//
//    @Test
//    @Order(5)
//    @TmsLink("1291819")
//    @DisplayName("UI ApacheKafkaCluster. Обновить кластерный сертификат")
//    void updateCertificate() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS,pSqlPage::updateCertificate);
//    }
//
//    @Test
//    @Order(6)
//    @TmsLink("1305356")
//    @DisplayName("UI ApacheKafkaCluster. Обновить кластерный сертификат(аварийно)")
//    void updateCertificateEmergency() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS,pSqlPage::updateCertificateEmergency);
//    }
//
//
//    @Disabled
//    @Test
//    @Order(7)
//    @TmsLink("1161546")
//    @DisplayName("UI ApacheKafkaCluster. Обновить ОС на кластере Kafka")
//    void updateOs() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS,pSqlPage::updateOs);
//    }
//
//    @Test
//    @Order(8)
//    @TmsLink("915049")
//    @DisplayName("UI ApacheKafkaCluster. Обновление дистрибутива ВТБ-Kafka")
//    void updateDistributionVtb() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS,pSqlPage::updateDistributionVtb);
//    }
//
//    @Test
//    @Order(9)
//    @TmsLink("1091895")
//    @DisplayName("UI ApacheKafkaCluster. Обновление ядра Kafka до версии 2.8.1")
//    void updateKernelVtb() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS,pSqlPage::updateKernelVtb);
//    }
//
//    @Test
//    @Order(10)
//    @TmsLink("852008")
//    @DisplayName("UI ApacheKafkaCluster. Прислать конфигурацию брокера Kafka")
//    void sendConfiguration() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS,pSqlPage::sendConfiguration);
//    }
//
//    @Test
//    @Order(11)
//    @TmsLink("852005")
//    @DisplayName("UI ApacheKafkaCluster. Синхронизировать конфигурацию кластера Kafka")
//    void synchronizeCluster() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS,pSqlPage::synchronizeCluster);
//    }
//
//    @Test
//    @Order(12)
//    @TmsLink("1091986")
//    @DisplayName("UI ApacheKafkaCluster. Вертикальное масштабирование")
//    void changeConfiguration() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.MORE,pSqlPage::changeConfiguration);
//    }
//
//    @Test
//    @Order(13)
//    @TmsLink("1092021")
//    @DisplayName("UI ApacheKafkaCluster. Увеличить дисковое пространство")
//    void expandDisk() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.enlargeDisk("/app", "20", new Table("Роли узла").getRow(0).get()));
//    }

//    @Test
//    @Order(14)
//    @TmsLink("851993")
//    @DisplayName("UI ApacheKafkaCluster.Пакетное создание Topic-ов")
//    void createTopics() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.createTopics2(name));
//    }

//    @Test
//    @Order(15)
//    @TmsLink("852010")
//    @DisplayName("UI ApacheKafkaCluster.Изменить параметр топиков Kafka Cluster")
//    void changeParamTopics() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.changeParamTopics(name));
//    }
    //    @Test
//    @Order(16)
//    @TmsLink("915047")
//    @DisplayName("UI ApacheKafkaCluster. Изменить имя кластера")
//    void changeNameCluster() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.changeNameCluster("kafka-clust-914"));
//    }

//    @Test
//    @Order(16)
//    @TmsLink("852011")
//    @DisplayName("UI ApacheKafkaCluster.Проверка имени кластера на уникальность")
//    void checkNameCluster() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS,pSqlPage::checkNameCluster);
//    }
//
//
    @Test
    @Order(17)
    @TmsLink("851995")
    @DisplayName("UI ApacheKafkaCluster.Пакетное создание ACL Kafka")
    void createAclTopics() {
      //  ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
      //  pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.createAclTopics2( AclTransaction ));
        new ApacheKafkaClusterPage(ApacheKafkaCluster.builder().build()).createAclTopics2(
                AclTransaction.builder().certificate("cert1").type(AclTransaction.Type.BY_NAME).mask("name1").build(),
                AclTransaction.builder().certificate("cert2").type(AclTransaction.Type.BY_MASK).mask("mask").build(),
                AclTransaction.builder().certificate("cert2").type(AclTransaction.Type.ALL_TRANSACTION).mask("*").build(),
                AclTransaction.builder().certificate("cert1").type(AclTransaction.Type.BY_NAME).mask("name2").build());
    }
//
//    @Test
//    @Order(18)
//    @TmsLink("851996")
//    @DisplayName("UI ApacheKafkaCluster.Пакетное удаление ACL Kafka")
//    void dellAclTopics() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.dellAclTopics(name));
//    }
//
//    @Test
//    @Order(19)
//    @TmsLink("851999")
//    @DisplayName("UI ApacheKafkaCluster.Пакетное создание ACL на транзакцию Kafka")
//    void createAclTrans() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.createAclTransaction(name));
//    }
//
//    @Test
//    @Order(20)
//    @TmsLink("852000")
//    @DisplayName("UI ApacheKafkaCluster.Пакетное удаление ACL на транзакцию Kafka")
//    void dellAclTrans() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.dellAclTransaction(name));
//    }
//
//    @Test
//    @Order(21)
//    @TmsLink("851994")
//    @DisplayName("UI ApacheKafkaCluster.Пакетное удаление Topic-ов Kafka")
//    void dellTopics() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.dellTopics(name));
//    }
//
//    @Test
//    @Order(22)
//    @TmsLink("982962")
//    @DisplayName("UI ApacheKafkaCluster.Создание идемпотентных ACL Kafka")
//    void createAclIdempotent() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.createAclIdempotent("1"));
//    }
//
//    @Test
//    @Order(23)
//    @TmsLink("982961")
//    @DisplayName("UI ApacheKafkaCluster.Удаление идемпотентных ACL Kafka")
//    void dellAclIdempotent() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, () ->pSqlPage.dellAclIdempotent("1"));
//    }
//
//    @Test
//    @Order(24)
//    @TmsLink("852012")
//    @DisplayName("UI ApacheKafkaCluster.Проверить конфигурацию")
//    void checkConfiguration() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::checkConfiguration);
//    }

//    @Test
//    @Order(100)
//    @TmsLink("852007")
//    @DisplayName("UI ApacheKafkaCluster. Удаление продукта")
//    void delete() {
//        ApacheKafkaClusterPage pSqlPage = new ApacheKafkaClusterPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.ZERO, pSqlPage::delete);
//    }

 }
