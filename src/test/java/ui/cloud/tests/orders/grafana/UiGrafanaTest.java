package ui.cloud.tests.orders.grafana;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Grafana;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.*;
import ui.elements.Alert;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("Grafana")
@Tags({@Tag("ui"), @Tag("ui_Grafana")})
public class UiGrafanaTest extends UiProductTest {

    Grafana product; // = Grafana.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/5b2343c6-7c16-4fdf-8a03-4f5a0295f16d/main?context=proj-ln4zg69jek&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1688711")
    @Order(1)
    @DisplayName("UI Grafana. Заказ")
    void orderGrafana() {
        double prebillingCost;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            GrafanaOrderPage orderPage = new GrafanaOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getCreateUser().setValue("user");
            orderPage.getGeneratePassButton().click();
            Alert.green("Значение скопировано");
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            GrafanaPage grafanaPage = new GrafanaPage(product);
            grafanaPage.waitChangeStatus(Duration.ofMinutes(25));
            grafanaPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        GrafanaPage grafanaPage = new GrafanaPage(product);
        checkOrderCost(prebillingCost, grafanaPage);
    }

    @Test
    @TmsLink("1742982")
    @Order(2)
    @DisplayName("UI Grafana. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.getGeneralInfoTab().switchTo();
        grafanaPage.checkHeadersHistory();
        grafanaPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[2]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(3)
    @TmsLink("1688709")
    @DisplayName("UI Grafana. Расширить точку монтирования")
    void expandDisk() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.MORE, () -> grafanaPage
                .enlargeDisk("/app", "20", new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(4)
    @TmsLink("1688714")
    @DisplayName("UI Grafana. Проверить конфигурацию")
    void vmActCheckConfig() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, grafanaPage::checkConfiguration);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(5)
    @TmsLinks({@TmsLink("1688713"), @TmsLink("1688716")})
    @DisplayName("UI Grafana. Удалить и добавить группу доступа")
    void addGroup() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, () -> grafanaPage.deleteGroup("user"));
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, () -> grafanaPage.addGroup("user", Collections.singletonList(product.accessGroup())));

    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(6)
    @TmsLink("1688707")
    @DisplayName("UI Grafana. Изменить состав группы")
    void changeGroup() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, () -> grafanaPage.updateGroup("user",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(7)
    @TmsLink("1688710")
    @EnabledIfEnv("blue")
    @DisplayName("UI Grafana. Изменить конфигурацию")
    void changeConfiguration() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.MORE, grafanaPage::changeConfiguration);
    }

    @Test
    @Order(8)
    @TmsLink("1688705")
    @DisplayName("UI Grafana. Мониторинг ОС")
    void monitoringOs() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.checkClusterMonitoringOs();
    }

    @Test
    @Disabled("Действие отсутствует")
    @Order(9)
    @TmsLink("")
    //@EnabledIfEnv("blue")
    @DisplayName("UI Grafana. Создать снапшот")
    void сreateSnapshot() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, grafanaPage::сreateSnapshot);
    }

    @Test
    @Disabled("Действие отсутствует")
    @Order(10)
    @TmsLink("")
    //@EnabledIfEnv("blue")
    @DisplayName("UI Grafana. Удалить снапшот")
    void deleteSnapshot() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, grafanaPage::deleteSnapshot);
    }

    @Test
    @Disabled("Действие отсутствует")
    @Order(11)
    @TmsLink("1688715")
    //@EnabledIfEnv("blue")
    @DisplayName("UI Grafana.  Реинвентаризация ВМ (Linux)")
    void reInventory() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, grafanaPage::reInventory);
    }

    @Test
    @Order(12)
    @TmsLink("1714371")
    @DisplayName("UI Grafana. Проверка доступа к Web интерфейсу управления через AD")
    void openAdminConsole() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, grafanaPage::openPointConnect);
    }

    @Test
    @Order(13)
    @TmsLink("")
    //@EnabledIfEnv({"blue","prod"})
    @DisplayName("UI Grafana. Сбросить пароль")
    void resetPassword() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS, grafanaPage::resetPassword);
    }

    @Test
    //@Disabled("Действие отсутствует")
    @Order(14)
    @TmsLink("")
    @DisplayName("UI Grafana. Выпустить клиентский сертификат")
    void issueClientCertificate() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS,  () -> grafanaPage.issueClientCertificate("Cert"));
    }
    @Test
    @Order(15)
    @TmsLink("")
    @DisplayName("UI Grafana. Обновить ОС")
    void updateOs() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.EQUALS,  grafanaPage::updateOs);
    }

    @Test
    @Order(100)
    @TmsLink("1688706")
    @DisplayName("UI Grafana. Удаление продукта")
    void delete() {
        GrafanaPage grafanaPage = new GrafanaPage(product);
        grafanaPage.runActionWithCheckCost(CompareType.LESS, grafanaPage::delete);
    }
}
