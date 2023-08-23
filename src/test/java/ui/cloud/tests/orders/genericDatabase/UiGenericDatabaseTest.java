package ui.cloud.tests.orders.genericDatabase;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.GenericDatabase;
import models.cloud.portalBack.AccessGroup;
import org.junit.DisabledIfEnv;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
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
@Feature("GenericDatabase")
@Tags({@Tag("ui"), @Tag("ui_generic_database")})
public class UiGenericDatabaseTest extends UiProductTest {

    GenericDatabase product;// = GenericDatabase.builder().build().buildFromLink("https://ift2-portal-front.oslb-dev01.corp.dev.vtb/all/orders/9ac48835-1015-4ee4-ac82-cef03653cef9/main?context=proj-pkvckn08w9&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1687204")
    @Order(1)
    @DisplayName("UI GenericDatabase. Заказ")
    void orderGenericDatabase() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            GenericDatabaseOrderPage orderPage = new GenericDatabaseOrderPage();
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
            GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
            genericDatabasePage.waitChangeStatus(Duration.ofMinutes(25));
            genericDatabasePage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        checkOrderCost(prebillingCost, genericDatabasePage);
    }

    @Test
    @TmsLink("1742157")
    @Order(2)
    @DisplayName("UI GenericDatabase. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.getGeneralInfoTab().switchTo();
        genericDatabasePage.checkHeadersHistory();
        genericDatabasePage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(3)
    @TmsLink("1687212")
    @DisplayName("UI GenericDatabase. Расширить точку монтирования")
    void expandDisk() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.MORE, () -> genericDatabasePage
                .enlargeDisk("/app", "20", new Table("Размер, ГБ").getRowByIndex(0)));
    }

    @Test
    @Order(4)
    @TmsLink("1687208")
    @DisplayName("UI GenericDatabase. Проверить конфигурацию")
    void vmActCheckConfig() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.EQUALS, genericDatabasePage::checkConfiguration);
    }

    @Test
    @Order(5)
    @TmsLinks({@TmsLink("1687207"), @TmsLink("1687211")})
    @DisplayName("UI GenericDatabase. Удалить и добавить группу доступа")
    void addGroup() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.EQUALS, () -> genericDatabasePage.deleteGroup("user"));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        genericDatabasePage.runActionWithCheckCost(CompareType.EQUALS, () -> genericDatabasePage.addGroup("user", Collections.singletonList(accessGroup.getPrefixName())));

    }

    @Test
    @Order(6)
    @TmsLink("1687206")
    @DisplayName("UI GenericDatabase. Изменить состав группы")
    void changeGroup() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        genericDatabasePage.runActionWithCheckCost(CompareType.EQUALS, () -> genericDatabasePage.updateGroup("user",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @Order(7)
    @TmsLink("1687209")
    @DisplayName("UI GenericDatabase. Изменить конфигурацию")
    void changeConfiguration() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.MORE, genericDatabasePage::changeConfiguration);
    }

    @Test
    @Order(8)
    @TmsLink("")
    @DisplayName("UI GenericDatabase. Создать снапшот")
    void сreateSnapshot() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.EQUALS, genericDatabasePage::сreateSnapshot);
    }

    @Test
    @Order(9)
    @TmsLink("")
    //@EnabledIfEnv("blue")
    @DisplayName("UI GenericDatabase. Удалить снапшот")
    void deleteSnapshot() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.EQUALS, genericDatabasePage::deleteSnapshot);
    }

    @Test
    @Order(10)
    @TmsLink("1687214")
    //@EnabledIfEnv("blue")
    @DisplayName("UI GenericDatabase.  Реинвентаризация ВМ (Linux)")
    void reInventory() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.EQUALS, genericDatabasePage::reInventory);
    }

    @Test
    @Order(11)
    @TmsLink("1687213")
    @DisplayName("UI GenericDatabase. Мониторинг ОС")
    void monitoringOs() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.checkMonitoringOs();
    }

    @Test
    @Order(12)
    @TmsLink("")
    @EnabledIfEnv("blue")
    @DisplayName("UI GenericDatabase.  Выпустить клиентский сертификат")
    void issueClientCertificate() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.EQUALS, ()-> genericDatabasePage.issueClientCertificate("Certificate"));
    }

    @Test
    @Order(100)
    @TmsLink("1687205")
    @DisplayName("UI GenericDatabase. Удаление продукта")
    void delete() {
        GenericDatabasePage genericDatabasePage = new GenericDatabasePage(product);
        genericDatabasePage.runActionWithCheckCost(CompareType.LESS, genericDatabasePage::delete);
    }
}
