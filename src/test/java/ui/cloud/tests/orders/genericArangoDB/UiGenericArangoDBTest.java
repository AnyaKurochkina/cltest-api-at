package ui.cloud.tests.orders.genericArangoDB;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.GenericArangoDB;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.*;
import ui.elements.Graph;
import ui.extesions.UiProductTest;
import java.time.Duration;
import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("GenericArangoDB")
@Tags({@Tag("ui"), @Tag("ui_generic_arangodb")})
public class UiGenericArangoDBTest extends UiProductTest {

    GenericArangoDB product;// = GenericArangoDB.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/86a26687-6f63-4ec2-be16-415e8d8ac6a1/main?context=proj-ln4zg69jek&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1687204")
    @Order(1)
    @DisplayName("UI GenericArangoDB. Заказ")
    void orderGenericArangoDB() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            GenericArangoDBOrderPage orderPage = new GenericArangoDBOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            GenericArangoDBPage genericArangoDBPage = new GenericArangoDBPage(product);
            genericArangoDBPage.waitChangeStatus(Duration.ofMinutes(25));
            genericArangoDBPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        GenericArangoDBPage genericArangoDBPage = new GenericArangoDBPage(product);
        checkOrderCost(prebillingCost, genericArangoDBPage);
    }

    @Test
    @TmsLink("1742157")
    @Order(2)
    @DisplayName("UI GenericArangoDB. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        GenericArangoDBPage genericArangoDBPage = new GenericArangoDBPage(product);
        genericArangoDBPage.getGeneralInfoTab().switchTo();
        genericArangoDBPage.checkHeadersHistory();
        genericArangoDBPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[2]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(11)
    @TmsLink("1687213")
    @DisplayName("UI GenericArangoDB. Мониторинг ОС")
    void monitoringOs() {
        GenericArangoDBPage genericArangoDBPage = new GenericArangoDBPage(product);
        genericArangoDBPage.checkMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("1687205")
    @DisplayName("UI GenericArangoDB. Удаление продукта")
    void delete() {
        GenericArangoDBPage genericArangoDBPage = new GenericArangoDBPage(product);
        genericArangoDBPage.runActionWithCheckCost(CompareType.LESS, genericArangoDBPage::delete);
    }
}
