package ui.cloud.tests.orders.clickHouse;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.ClickHouse;
import models.cloud.portalBack.AccessGroup;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;

import static ui.elements.TypifiedElement.scrollCenter;

@Epic("UI Продукты")
@Feature("ClickHouse")
@Tags({@Tag("ui"), @Tag("ui_clickHouse")})
public class UiClickHouseTest extends UiProductTest {

    ClickHouse product;
    //= ClickHouse.builder().build().buildFromLink("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/db/orders/3a0db258-a6fb-4cb5-8d4b-055857be1265/main?context=proj-pkvckn08w9&type=project&org=vtb");

    String nameAD = "at_ad_user";
    String nameFull = "qa_order_service_admin";
    String nameLocalAD = "at_local_user";

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("330312")
    @Order(1)
    @DisplayName("UI ClickHouse. Заказ")
    void orderClickHouse() {
        double preBillingProductPrice;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            ClickHouseOrderPage orderPage = new ClickHouseOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getNameUser().setValue("at_user");
            orderPage.getGeneratePassButton1().shouldBe(Condition.enabled).click();
            orderPage.getGeneratePassButton2().shouldBe(Condition.enabled).click();
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMaxFlavor()));
            orderPage.getGroup().set(accessGroup);
            orderPage.getGroup2().set(accessGroup);
            orderPage.getGroup3().set(accessGroup);
            orderPage.getGroup4().set(accessGroup);
            preBillingProductPrice = EntitiesUtils.getCostValue(orderPage.getPrebillingCostElement());
            EntitiesUtils.clickOrder();
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
        Assertions.assertEquals(preBillingProductPrice, clickHousePage.getOrderCost(), 0.01);
    }

    @Test
    @TmsLink("1236735")
    @Order(2)
    @DisplayName("UI ClickHouse. Проверка полей заказа")
    void
    checkHeaderHistoryTable() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.getBtnGeneralInfo().click();
        clickHousePage.checkHeadersHistory();
        clickHousePage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(3)
    @TmsLink("330327")
    @Disabled
    @DisplayName("UI ClickHouse. Перезагрузить по питанию")
    void restart() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::restart);
    }


    @Test
    @Order(4)
    @TmsLink("330329")
    @DisplayName("UI ClickHouse. Расширить диск")
    void expandDisk() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.MORE, () -> clickHousePage.enlargeDisk("/app/clickhouse", "20", new Table("Тип").getRow(0).get()));
    }

    @Test
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
    @Disabled
    @DisplayName("UI ClickHouse. Сбросить пароль владельца БД")
    void resetPasswordDb() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::resetPasswordDb);
    }

    @Test
    @Order(7)
    @TmsLink("358259")
    @DisplayName("UI ClickHouse. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, clickHousePage::resetPasswordUserDb);
    }

    @Test
    @Order(8)
    @TmsLink("1422751")
    @DisplayName("UI ClickHouse . Cбросить пароль ТУЗ с полными правами")
    void resetPasswordFullRights() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.resetPasswordFullRights(nameFull));
    }

    @Test
    @Order(9)
    @TmsLink("1419321")
    @DisplayName("UI ClickHouse. Cоздание локальной УЗ")
    void createLocalAccount() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.createLocalAccount(nameLocalAD));
    }

    @Test
    @Order(10)
    @TmsLink("1422714")
    @DisplayName("UI ClickHouse . Cбросить пароль локальной УЗ")
    void resetPasswordLA() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.resetPasswordLA(nameLocalAD));
    }

    @Test
    @Order(11)
    @TmsLink("1419324")
    @DisplayName("UI ClickHouse . Добавление УЗ AD")
    void addAccountAD() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.addAccountAD(nameAD));
    }

    @Test
    @Order(12)
    @TmsLink("1419320")
    @DisplayName("UI ClickHouse . Удаление локальной УЗ")
    void deleteLocalAccount() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.deleteLocalAccount(nameLocalAD));
    }

    @Test
    @Order(13)
    @TmsLink("1419319")
    @DisplayName("UI ClickHouse . Удаление УЗ АD")
    void deleteAccountAD() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.deleteAccountAD(nameAD));
    }

    @Test
    @Order(14)
    @TmsLink("1419322")
    @DisplayName("UI ClickHouse . Удалить  пользовательскую группу")
    void deleteGroupAD() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.deleteGroupAD());
    }

    @Test
    @Order(15)
    @TmsLink("1419325")
    @DisplayName("UI ClickHouse . Удалить группу администраторов")
    void deleteGroupAdmin() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.deleteGroupAdmin());
    }

    @Test
    @Order(16)
    @TmsLink("1419326")
    @DisplayName("UI ClickHouse . Добавить пользовательскую группу")
    void addGroupAD() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.addGroupAD(accessGroup.getPrefixName()));
    }

    @Test
    @Order(17)
    @TmsLink("1419323")
    @DisplayName("UI ClickHouse . Добавить группу администраторов")
    void addGroupAdmin() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        clickHousePage.runActionWithCheckCost(CompareType.EQUALS, () -> clickHousePage.addGroupAdmin(accessGroup.getPrefixName()));
    }


    @Test
    @Order(18)
    @TmsLinks({@TmsLink("330325"), @TmsLink("330330")})
    @Disabled
    @DisplayName("UI ClickHouse. Выключить принудительно / Включить")
    void stopHard() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.LESS, clickHousePage::stopHard);
        clickHousePage.runActionWithCheckCost(CompareType.MORE, clickHousePage::start);
    }

    @Test
    @Order(19)
    @TmsLink("330324")
    @Disabled
    @DisplayName("UI ClickHouse. Выключить")
    void stopSoft() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        clickHousePage.runActionWithCheckCost(CompareType.LESS, clickHousePage::stopSoft);
    }

    @Test
    @Order(20)
    @TmsLink("1536880")
    @EnabledIfEnv("prod")
    @DisplayName("UI ClickHouse. Мониторинг ОС")
    void monitoringOs() {
        ClickHousePage clickHousePage = new ClickHousePage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        clickHousePage.checkClusterMonitoringOs();
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
