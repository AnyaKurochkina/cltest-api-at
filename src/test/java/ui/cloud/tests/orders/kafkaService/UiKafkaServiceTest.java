package ui.cloud.tests.orders.kafkaService;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ApacheKafkaCluster;
import models.cloud.orderService.products.KafkaService;
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
@Feature("KafkaService")
@Tags({@Tag("ui"), @Tag("KafkaService")})
public class UiKafkaServiceTest extends UiProductTest {

    String name="acl";

    KafkaService product;// = KafkaService.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/1e521f86-97f9-4bef-bea4-136aa41d5053/main?context=proj-ln4zg69jek&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("")
    @Order(1)
    @DisplayName("UI KafkaService. Заказ")
    void orderKafkaService() {
        double preBillingProductPrice;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            KafkaServiceOrderPage orderPage = new KafkaServiceOrderPage();
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getNameTopic().setValue(name);
            orderPage.getDomain().set(product.getDomain());
            orderPage.getDataCentreSelect().setByDataValue(product.getDataCentre());
            preBillingProductPrice = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            KafkaServicePage pSqlPages = new KafkaServicePage(product);
            pSqlPages.waitChangeStatus(Duration.ofMinutes(25));
            pSqlPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        checkOrderCost(preBillingProductPrice, new KafkaServicePage(product));
    }

    @Test
    @TmsLink("")
    @Order(2)
    @DisplayName("UI KafkaService. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        KafkaServicePage servicePage = new KafkaServicePage(product);
        servicePage.getBtnGeneralInfo().click();
        servicePage.checkHeadersHistory();
        servicePage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }


    @Test
    @Order(3)
    @TmsLink("")
    @DisplayName("UI KafkaService.Пакетное создание ACL")
    void createAclTrans() {
        KafkaServicePage servicePage = new KafkaServicePage(product);
        servicePage.runActionWithCheckCost(CompareType.EQUALS, () ->servicePage.createBatchAcl(name));
    }

    @Test
    @Order(4)
    @TmsLink("")
    @DisplayName("UI KafkaService.Пакетное создание групповой ACL")
    void createGroupAcl() {
        KafkaServicePage servicePage = new KafkaServicePage(product);
        servicePage.runActionWithCheckCost(CompareType.EQUALS, () ->servicePage.createGroupAcl(name));
    }


    @Test
    @Order(5)
    @TmsLink("")
    @DisplayName("UI KafkaService.Пакетное удаление ACL")
    void deleteAclTrans() {
        KafkaServicePage servicePage = new KafkaServicePage(product);
        servicePage.runActionWithCheckCost(CompareType.EQUALS, servicePage::deleteBatchAcl);
    }

    @Test
    @Order(6)
    @TmsLink("")
    @DisplayName("UI KafkaService.Пакетное удаление ACL на группу")
    void deleteGroupAcl() {
        KafkaServicePage servicePage = new KafkaServicePage(product);
        servicePage.runActionWithCheckCost(CompareType.EQUALS, servicePage::deleteGroupAcl);
    }

    @Test
    @Order(100)
    @TmsLink("")
    @DisplayName("UI KafkaService. Удаление продукта")
    void delete() {
        KafkaServicePage servicePage = new KafkaServicePage(product);
        servicePage.runActionWithCheckCost(CompareType.ZERO, servicePage::delete);
    }
 }
