package ui.cloud.tests.orders.clickHouse;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.ClickHouse;
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

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;
import static ui.elements.TypifiedElement.scrollCenter;

@Epic("UI Продукты")
@Feature("ClickHouse")
@Tags({@Tag("ui"), @Tag("ui_clickHouse")})
public class UiClickHouseTest extends UiProductTest {

    private ClickHouse product;// = ClickHouse.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/2fab9e99-b1af-4269-b64b-f7cb5725d281/main?context=proj-ln4zg69jek&type=project&org=vtb");

    private final String nameAD = "at_ad_user";
    private final String userPasswordFullRight = "x7fc1GyjdMhUXXxgpGCube6jHWmn";
    private final String nameLocalAD = "userad";

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("330312")
    @Order(1)
    @DisplayName("UI ClickHouse. Заказ")
    void orderClickHouse() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            ClickHouseOrderPage orderPage = new ClickHouseOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            if (product.isDev())
                orderPage.getNameUser().setValue(nameAD);
            orderPage.getGeneratePassButton1().setValue(userPasswordFullRight);
            if (product.isDev())
                orderPage.getGeneratePassButton2().shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMaxFlavor()));
            orderPage.getGroup().set(accessGroup);
            orderPage.getGroup2().set(accessGroup);
            if (product.isDev()) {
                orderPage.getGroup3().set(accessGroup);
                orderPage.getGroup4().set(accessGroup);
            }
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            ClickHousePage clickHousePages = new ClickHousePage(product);
            clickHousePages.waitChangeStatus(Duration.ofMinutes(25));
            clickHousePages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        ClickHousePage clickHousePage = new ClickHousePage(product);
        checkOrderCost(prebillingCost, clickHousePage);
    }

    @Test
    @TmsLink("1236735")
    @Order(2)
    @DisplayName("UI ClickHouse. Проверка графа в истории действий")
    void
    checkHeaderHistoryTable() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.getBtnGeneralInfo().click();
        clickHousePage.checkHeadersHistory();
        clickHousePage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(3)
    @TmsLink("330327")
    @Disabled
    @DisplayName("UI ClickHouse. Перезагрузить")
    void restart() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::restart);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(4)
    @TmsLink("330329")
    @DisplayName("UI ClickHouse. Расширить точку монтирования")
    void expandDisk() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.MORE, () -> clickHousePage.enlargeDisk("/app/clickhouse", "20", new Table("Тип").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(5)
    @TmsLink("1177396")
    @DisplayName("UI ClickHouse. Проверить конфигурацию")
    void vmActCheckConfig() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.checkConfiguration(new Table("Тип").getRow(0).get()));
    }

    @Test
    @Order(6)
    @TmsLink("1162627")
    @DisplayName("UI ClickHouse. Сбросить пароль владельца БД")
    void resetPasswordDb() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::resetPasswordDb);
    }

    @Test
    @Order(7)
    @TmsLink("1422751")
    @DisplayName("UI ClickHouse . Cбросить пароль ТУЗ с полными правами")
    void resetPasswordFullRights() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.resetPasswordFullRights(nameAD));
    }

    @Test
    @Order(8)
    @TmsLink("1419321")
    @DisplayName("UI ClickHouse. Cоздание локальной УЗ")
    void createLocalAccount() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.createLocalAccount(nameLocalAD));
    }

    @Test
    @Order(9)
    @TmsLink("1422714")
    @DisplayName("UI ClickHouse . Cбросить пароль локальной УЗ")
    void resetPasswordLA() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.resetPasswordLA(nameLocalAD));
    }

    @Test
    @Order(10)
    @TmsLink("1419324")
    @DisplayName("UI ClickHouse . Добавление ТУЗ AD")
    void addAccountAD() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.addAccountAD(nameAD));
    }

    @Test
    @Order(11)
    @TmsLink("1419320")
    @DisplayName("UI ClickHouse . Удаление локальной УЗ")
    void deleteLocalAccount() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.deleteLocalAccount(nameLocalAD));
    }

    @Test
    @Order(12)
    @TmsLink("1419319")
    @DisplayName("UI ClickHouse . Удаление TУЗ АD")
    void deleteAccountAD() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.deleteAccountAD(nameAD));
    }

    @Test
    @Order(13)
    @TmsLinks({@TmsLink("1419326"), @TmsLink("1419322")})
    @DisplayName("UI ClickHouse . Добавить/удалиь пользовательскую группу")
    void addGroupAD() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        final String group = product.additionalAccessGroup();
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.addGroupAD(group));
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.deleteGroupAD(group));
    }

    @Test
    @Order(14)
    @TmsLinks({@TmsLink("1419323"), @TmsLink("1419325")})
    @DisplayName("UI ClickHouse . Добавить/удалить группу администраторов")
    void addGroupAdmin() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        final String group = product.additionalAccessGroup();
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.addGroupAdmin(group));
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.deleteGroupAdmin(group));
    }

    @Test
    @Order(15)
    @TmsLinks({@TmsLink("330325"), @TmsLink("330330")})
    @Disabled
    @DisplayName("UI ClickHouse. Выключить принудительно / Включить")
    void stopHard() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.LESS, clickHousePage::stopHard);
        clickHousePage.runActionWithCheckCost(CompareType.MORE, clickHousePage::start);
    }

    @Test
    @Order(16)
    @TmsLink("330324")
    @Disabled
    @DisplayName("UI ClickHouse. Выключить")
    void stopSoft() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.LESS, clickHousePage::stopSoft);
    }

    @Test
    @Order(17)
    @TmsLink("1536880")
    @DisplayName("UI ClickHouse. Мониторинг ОС")
    void monitoringOs() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        clickHousePage.checkClusterMonitoringOs();
    }

    @Test
    @Order(18)
    @TmsLink("")
    @DisplayName("UI ClickHouse. Обновить информацию о сертификатах Clickhouse")
    void updateInformationCert() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::updateInformationCert);
    }

    @Test
    @Order(19)
    @TmsLink("")
    @DisplayName("UI ClickHouse. Обновить сертификаты Clickhouse")
    void updateCertificate() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::updateCertificate);
    }

    @Test
    @Order(20)
    @TmsLink("")
    @DisplayName("UI ClickHouse Cluster. Проверка доступа к Web интерфейсу управления через AD")
    void openAdminConsole() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::openPointConnect);
    }

    @Test
    @Order(100)
    @TmsLink("330328")
    @DisplayName("UI ClickHouse. Удаление продукта")
    void delete() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.delete();
    }
}
