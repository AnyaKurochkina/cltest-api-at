package ui.cloud.tests.orders.clickHouseCluster;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.ClickHouseCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.orders.ClickHouseClusterOrderPage;
import ui.cloud.pages.orders.ClickHouseClusterPage;
import ui.cloud.pages.orders.OrderUtils;
import ui.cloud.pages.orders.OrdersPage;
import ui.elements.Graph;
import ui.extesions.UiProductTest;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("ClickHouse Cluster")
@Tags({@Tag("ui"), @Tag("ui_clickhouse_cluster")})
public class UiClickHouseClusterTest extends UiProductTest {
    ClickHouseCluster product;
//    ClickHouseCluster product = ClickHouseCluster.builder().build().buildFromLink("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/db/orders/50b52931-3f9f-4bd4-9868-373baa186cdf/main?context=proj-pkvckn08w9&type=project&org=vtb");

    String nameAD = "at_ad_user";
    String nameLocalAD = "at_local_user";
    String nameGroup = "cloud-zorg-dev-group";
    SelenideElement node = $x("(//td[.='clickhouse'])[1]");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1138094")
    @Order(1)
    @DisplayName("UI ClickHouse Cluster. Заказ")
    void orderClickHouseCluster() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            ClickHouseClusterOrderPage orderPage = new ClickHouseClusterOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getNameCluster().setValue("cluster");
            orderPage.getNameUser().setValue("at_user");
            orderPage.getGeneratePassButton1().shouldBe(Condition.enabled).click();
            orderPage.getGeneratePassButton2().shouldBe(Condition.enabled).click();
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getGroupSelect().set(accessGroup);
            orderPage.getGroup2().set(accessGroup);
            orderPage.getGroup3().set(accessGroup);
            orderPage.getGroup4().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
            clickHouseClusterPage.waitChangeStatus(Duration.ofMinutes(25));
            clickHouseClusterPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        checkOrderCost(prebillingCost, clickHouseClusterPage);
    }


    @Test
    @TmsLink("1236734")
    @Order(2)
    @DisplayName("UI ClickHouse Cluster. Проверка полей заказа")
    void
    checkHeaderHistoryTable() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.getBtnGeneralInfo().click();
        clickHouseClusterPage.checkHeadersHistory();
        clickHouseClusterPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(3)
    @TmsLink("1138093")
    @DisplayName("UI ClickHouse Cluster. Перезагрузить по питанию")
    void restart() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, clickHouseClusterPage::restart);
    }


    @Test
    @Order(4)
    @TmsLink("1138087")
    @DisplayName("UI ClickHouse Cluster. Расширить точку монтирования")
    void expandDisk() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.MORE, () -> clickHouseClusterPage.enlargeDisk("/app/clickhouse", "20", node));
    }

    @Test
    @Order(5)
    @TmsLink("1162629")
    @DisplayName("UI ClickHouse Cluster. Проверить конфигурацию")
    void vmActCheckConfig() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, clickHouseClusterPage::checkConfiguration);
    }

    @Test
    @Order(6)
    @TmsLink("1152772")
    @DisplayName("UI ClickHouse Cluster. Пользователи. Создать локальную УЗ")
    void createLocalAccount() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.createLocalAccount(nameLocalAD));
    }

    @Test
    @Order(7)
    @TmsLink("1149195")
    @DisplayName("UI ClickHouse Cluster. Пользователи. Cбросить пароль")
    void resetPasswordLA() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.resetPasswordLA(nameLocalAD));
    }

    @Test
    @Order(8)
    @TmsLink("1152774")
    @DisplayName("UI ClickHouse Cluster. Пользователи. Добавить ТУЗ AD")
    void addAccountAD() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.addAccountAD(nameAD));
    }

    @Test
    @Order(9)
    @TmsLink("1152773")
    @DisplayName("UI ClickHouse Cluster. Пользователи. Удалить локальную УЗ")
    void deleteLocalAccount() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.deleteLocalAccount(nameLocalAD));
    }

    @Test
    @Order(10)
    @TmsLink("1152780")
    @DisplayName("UI ClickHouse Cluster. Пользователи. Удалить ТУЗ AD")
    void deleteAccountAD() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.deleteAccountAD(nameAD));
    }

    @Test
    @Order(11)
    @TmsLinks({@TmsLink("1152788"), @TmsLink("1152789")})
    @DisplayName("UI ClickHouse Cluster. Группы. Добавить/удалить пользовательскую группу")
    void addGroupAD() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        AccessGroup accessGroup = AccessGroup.builder().name(new Generex("vtb-[a-z]{5,15}").random()).projectName(product.getProjectId()).build().createObject();
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.addGroupAD(accessGroup.getName()));
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.deleteGroupAD(accessGroup.getPrefixName()));
    }

    @Test
    @Order(12)
    @TmsLinks({@TmsLink("1152793"), @TmsLink("1152794")})
    @DisplayName("UI ClickHouse Cluster. Группы. Добавить группу администраторов")
    void addGroupAdmin() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        AccessGroup accessGroup = AccessGroup.builder().name(new Generex("vtb-[a-z]{5,15}").random()).projectName(product.getProjectId()).build().createObject();
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.addGroupAdmin(accessGroup.getName()));
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.deleteGroupAdmin(accessGroup.getPrefixName()));

    }

    @Test
    @Order(13)
    @TmsLinks({@TmsLink("1138086"), @TmsLink("1138091")})
    @Disabled
    @DisplayName("UI ClickHouse Cluster. Выключить принудительно / Включить")
    void stopHard() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.LESS, clickHouseClusterPage::stopHard);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.MORE, clickHouseClusterPage::start);
    }

    @Test
    @Order(14)
    @TmsLink("1138092")
    @Disabled
    @DisplayName("UI ClickHouse Cluster. Выключить")
    void stopSoft() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.LESS, clickHouseClusterPage::stopSoft);
    }

    @Test
    @Order(15)
    @EnabledIfEnv("prod")
    @TmsLink("1296753")
    @DisplayName("UI ClickHouse Cluster. Мониторинг ОС")
    void monitoringOs() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("1138090")
    @DisplayName("UI ClickHouse Cluster. Удаление продукта")
    void delete() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.delete();
    }
}
