package ui.cloud.tests.orders.wildfly;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.WildFly;
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
import java.util.Collections;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("WildFlyAstra")
@Tags({@Tag("ui"), @Tag("ui_wildfly_astra")})
public class UiWildFlyAstraTest extends UiProductTest {
    private final String versionWildFly = "23.0.2.Final";
    private final String versionJava = "8";
    private final String newVersionJava = "11";
    private WildFly product;// = WildFly.builder().platform("OpenStack").segment("dev-srv-app").build().buildFromLink("https://console.blue.cloud.vtb.ru/all/orders/eec5608c-3e7e-4262-b6fe-8947cb2ffd4c/main?context=proj-iv550odo9a&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
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
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            WildFlyAstraOrderPage orderPage = new WildFlyAstraOrderPage();
            orderPage.getVersionWildfly().set(product.getWildFlyVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getVersionJava().set(versionJava);
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
        wildFlyPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[2]").shouldBe(Condition.enabled).click();
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
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::updateCertificateWithoutChange);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::updateCertificateGlobal);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::updateCertificateF5);
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
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.addGroupWildFlyAstra("Monitor", product.additionalAccessGroup()));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.deleteGroupWildFlyAstra("Monitor", product.additionalAccessGroup()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(10)
    @TmsLinks({@TmsLink("1644573"), @TmsLink("1644572"), @TmsLink("1644574")})
    @DisplayName("UI WildFlyAstra. Удалить/добавить/изменить группу доступа")
    void addGroup() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.deleteGroupInNode("superuser", product.accessGroup()));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.addGroupInNode("superuser", Collections.singletonList(product.accessGroup())));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.updateGroupInNode("superuser", Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(11)
    @TmsLink("908277")
    @DisplayName("UI WildFlyAstraLinux. Расширить точку монтирования")
    void expandDisk() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.MORE, () -> wildFlyPage
                .enlargeDisk("/app/app", "20", new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(12)
    @TmsLink("")
    @DisplayName("UI WildFlyAstra. Вертикальное масштабирование WildFly")
    void vmActCheckConfig() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::changeConfiguration);
    }

    @Test
    @Order(13)
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
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.changeJavaWildFly(versionWildFly, newVersionJava));
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
