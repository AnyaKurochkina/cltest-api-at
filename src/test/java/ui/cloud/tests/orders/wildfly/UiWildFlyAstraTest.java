package ui.cloud.tests.orders.wildfly;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.WildFly;
import models.cloud.portalBack.AccessGroup;
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
@Feature("WildFlyAstra")
@Tags({@Tag("ui"), @Tag("ui_wildfly_astra")})
public class UiWildFlyAstraTest extends UiProductTest {
    String versionWildFly = "28.0.1.Final";
    String versionJava = "17.0.6";
    WildFly product;// = WildFly.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/all/orders/ce9e3c27-ca51-491d-a355-5e4eb8870a51/main?context=proj-2xdbtyzqs3&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("908276")
    @Order(1)
    @DisplayName("UI WildFlyAstra. Заказ")
    void orderWildFlyAstra() {
        double prebillingCost;
        try {
            String accessGroup = product.getAccessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            WildFlyAstraOrderPage orderPage = new WildFlyAstraOrderPage();
            orderPage.getVersionWildfly().set(product.getWildFlyVersion());
            orderPage.getVersionJava().set(product.getVersionJava());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getVersionJava().set(product.getVersionJava());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getRoleSelect().set("user");
            orderPage.getGroupSelect().set(accessGroup);
            orderPage.getGroupWildFly().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
            wildFlyPage.waitChangeStatus(Duration.ofMinutes(25));
            wildFlyPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        checkOrderCost(prebillingCost, wildFlyPage);
    }

    @Test
    @TmsLink("1644557")
    @Order(2)
    @DisplayName("UI WildFlyAstra. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.getGeneralInfoTab().switchTo();
        wildFlyPage.checkHeadersHistory();
        wildFlyPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(3)
    @TmsLink("1353800")
    @DisplayName("UI WildFlyAstra. Обновить ОС сервера WildFly")
    void updateServerOs() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::updateServerOs);
    }

    @Test
    @Order(4)
    @TmsLink("908271")
    @DisplayName("UI WildFlyAstra. Обновить сертификат WildFly")
    void updateCertificate() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::updateCertificate);
    }

    @Test
    @Order(5)
    @TmsLink("1353241")
    @DisplayName("UI WildFlyAstra. Остановить сервис Wildfly")
    void stopService() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::stopService);
    }

    @Test
    @Order(6)
    @TmsLink("1353338")
    @DisplayName("UI WildFlyAstra. Запустить сервис Wildfly")
    void startService() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::startService);
    }

    @Test
    @Order(7)
    @TmsLink("1353235")
    @DisplayName("UI WildFlyAstra. Перезапустить сервис Wildfly")
    void resetService() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::resetService);
    }

    @Test
    @Order(8)
    @TmsLink("1089998")
    @DisplayName("UI WildFlyAstra. Синхронизировать конфигурацию сервера WildFly")
    void synchronizeService() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::synchronizeService);
    }

    @Test
    @Order(9)
    @TmsLinks({@TmsLink("910818"), @TmsLink("910821")})
    @DisplayName("UI WildFlyAstra. Добавить/удалить группу WildFly")
    void addGroupWildFlyAstra() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        AccessGroup accessGroup = AccessGroup.builder().name(new Generex("vtb-[a-z]{5,15}").random()).projectName(product.getProjectId()).build().createObject();
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.addGroupWildFlyAstra("Monitor", accessGroup.getPrefixName()));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.deleteGroupWildFlyAstra("Monitor", accessGroup.getPrefixName()));
    }

    @Test
    @Order(10)
    @TmsLinks({@TmsLink("1644573"), @TmsLink("1644572"), @TmsLink("1644574")})
    @DisplayName("UI WildFlyAstra. Удалить/добавить/изменить группу доступа")
    void addGroup() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.deleteGroupInNode("user", accessGroupOne.getPrefixName()));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.addGroupInNode("superuser", Collections.singletonList(accessGroupOne.getPrefixName())));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.updateGroupInNode("superuser", Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @Order(11)
    @TmsLink("908277")
    @DisplayName("UI WildFlyAstraLinux. Расширить точку монтирования")
    void expandDisk() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.MORE, () -> wildFlyPage
                .enlargeDisk("/app/app", "20", new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Order(12)
    @TmsLink("")
    @DisplayName("UI WildFlyAstra. Изменить конфигурацию")
    void vmActCheckConfig() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::changeConfiguration);
    }


    @Test
    @Order(13)
    @EnabledIfEnv("prod")
    @TmsLink("1171954")
    @DisplayName("UI WildFlyAstra. Мониторинг ОС")
    void monitoringOs() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(15)
    @TmsLink("908267")
    @DisplayName("UI WildFlyAstra. Консоль администратора")
    void openAdminConsole() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::openAdminConsole);
    }

    @Test
    @Order(16)
    @TmsLink("")
    @DisplayName("UI WildFlyAstra. Заменить Java Wildfly")
    void changeJavaWildFly() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.changeJavaWildFly(versionWildFly,versionJava));
    }

    @Test
    @Order(100)
    @TmsLink("908275")
    @DisplayName("UI WildFlyAstra. Удаление продукта")
    void delete() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.LESS, wildFlyPage::delete);
    }
}
