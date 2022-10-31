package ui.cloud.tests.orders.clickHouseCluster;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.ClickHouseCluster;
import models.orderService.products.PostgreSQL;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.elements.Alert;
import ui.elements.Graph;
import ui.elements.Table;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.time.Duration;

import static core.helper.StringUtils.$x;

@ExtendWith(ConfigExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_clickhouse_cluster")})
@Log4j2
public class UiClickHouseClusterTest extends Tests {

    ClickHouseCluster product;
    String nameAD= "at_ad_user";
    String nameLocalAD= "at_local_user";
    String nameGroup ="cloud-zorg-winxtkhxxdw";
    SelenideElement node = $x("(//td[.='clickhouse'])[1]");
    public UiClickHouseClusterTest() {
        if (Configure.ENV.equals("prod"))
           product = ClickHouseCluster.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
          //  product = ClickHouseCluster.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/db/orders/cffa192c-aab5-4826-9c11-a8a87b3d6684/user?context=proj-pkvckn08w9&type=project&org=vtb").build();
        else
            product = ClickHouseCluster.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1138094")
    @Order(1)
    @DisplayName("UI ClickHouse Cluster. Заказ")
    void orderClickHouseCluster() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            ClickHouseClusterOrderPage orderPage = new ClickHouseClusterOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getNameCluster().setValue("cluster");
            orderPage.getNameUser().setValue("at_user");
            orderPage.getGeneratePassButton1().shouldBe(Condition.enabled).click();
            orderPage.getGeneratePassButton2().shouldBe(Condition.enabled).click();
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            orderPage.getGroup().select(accessGroup.getPrefixName());
            orderPage.getGroup2().select(accessGroup.getPrefixName());
            orderPage.getGroup3().select(accessGroup.getPrefixName());
            orderPage.getGroup4().select(accessGroup.getPrefixName());
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = IProductPage.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new Alert().checkColor(Alert.Color.GREEN).checkText("Заказ успешно создан");
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
        Assertions.assertEquals(preBillingProductPrice, clickHouseClusterPage.getCostOrder(), 0.01);
    }


    @Test
    @TmsLink("1236734")
    @Order(2)
    @DisplayName("UI ClickHouse Cluster. Проверка полей заказа")
    void
    checkHeaderHistoryTable() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
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
    @DisplayName("UI ClickHouse Cluster. Расширить диск")
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
    @DisplayName("UI ClickHouse Cluster. Cоздание локальной УЗ")
    void createLocalAccount() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.createLocalAccount(nameLocalAD));
    }

    @Test
    @Order(7)
    @TmsLink("1149195")
    @DisplayName("UI ClickHouse Cluster. Cбросить пароль локальной УЗ")
    void resetPasswordLA() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.resetPasswordLA(nameLocalAD));
    }

    @Test
    @Order(8)
    @TmsLink("1152774")
    @DisplayName("UI ClickHouse Cluster. Добавление УЗ AD")
    void addAccountAD() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.addAccountAD(nameAD));
    }

    @Test
    @Order(10)
    @TmsLink("1152773")
    @DisplayName("UI ClickHouse Cluster. Удаление локальной УЗ")
    void deleteLocalAccount() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.deleteLocalAccount(nameLocalAD));
    }
    @Test
    @Order(11)
    @TmsLink("1152780")
    @DisplayName("UI ClickHouse Cluster. Удаление УЗ АD")
    void deleteAccountAD() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.deleteAccountAD(nameAD));
    }

    @Test
    @Order(12)
    @TmsLink("1152788")
    @DisplayName("UI ClickHouse Cluster. Добавить группу пользователей AD")
    void addGroupAD() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS,() -> clickHouseClusterPage.addGroupAD(nameGroup));
    }

    @Test
    @Order(13)
    @TmsLink("1152789")
    @DisplayName("UI ClickHouse Cluster. Удалить группу пользователей AD")
    void deleteGroupAD() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS,() -> clickHouseClusterPage.deleteGroupAD(nameGroup));
    }


        @Test
    @Order(14)
    @TmsLink("1152793")
    @DisplayName("UI ClickHouse Cluster. Добавить группу администраторов")
    void addGroupAdmin() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
            clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS,() -> clickHouseClusterPage.addGroupAdmin(nameGroup));
    }
    @Test
    @Order(15)
    @TmsLink("1152794")
    @DisplayName("UI ClickHouse Cluster. Удалить группу администраторов")
    void deleteGroupAdmin() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHouseClusterPage.deleteGroupAdmin(nameGroup));
    }


    @Test
    @Order(16)
    @TmsLinks({@TmsLink("1138086"), @TmsLink("1138091")})
    @Disabled
    @DisplayName("UI ClickHouse Cluster. Выключить принудительно / Включить")
    void stopHard() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.LESS, clickHouseClusterPage::stopHard);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.MORE, clickHouseClusterPage::start);
    }

    @Test
    @Order(17)
    @TmsLink("1138092")
    @Disabled
    @DisplayName("UI ClickHouse Cluster. Выключить")
    void stopSoft() {
        ClickHouseClusterPage clickHouseClusterPage = new ClickHouseClusterPage(product);
        clickHouseClusterPage.runActionWithCheckCost(CompareType.LESS, clickHouseClusterPage::stopSoft);
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
