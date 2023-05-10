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
import ui.cloud.pages.*;
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

    WildFly product;// = WildFly.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/web/orders/f50263d6-d6c9-4b29-b758-8689b3cb8bc2/main?context=proj-ln4zg69jek&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginCloudPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("908276")
    @Order(1)
    @DisplayName("UI WildFlyAstra. Заказ")
    void orderWildFlyAstra() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            WildFlyAstraOrderPage orderPage = new WildFlyAstraOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
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
        new Graph().checkGraph();
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
    @DisplayName("UI WildFlyAstra. Добавление/Удаление группы WildFly")
    void addGroupWildFlyAstra() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        AccessGroup accessGroup = AccessGroup.builder().name(new Generex("vtb-[a-z]{5,15}").random()).projectName(product.getProjectId()).build().createObject();
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.addGroupWildFlyAstra("Monitor",accessGroup.getPrefixName()));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.deleteGroupWildFlyAstra("Monitor",accessGroup.getPrefixName()));
    }

    @Test
    @Order(10)
    @TmsLinks({@TmsLink("1644573"), @TmsLink("1644572"), @TmsLink("1644574")})
    @DisplayName("UI WildFlyAstra. Удаление/Добавление/Изменение группы")
    void addGroup() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.deleteGroupInNode("superuser",accessGroupOne.getPrefixName()));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.addGroupInNode("superuser", Collections.singletonList(accessGroupOne.getPrefixName())));
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, () -> wildFlyPage.updateGroupInNode("superuser",Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
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
    @TmsLink("910157")
    @DisplayName("UI WildFlyAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.EQUALS, wildFlyPage::checkConfiguration);
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
    @Order(100)
    @TmsLink("908275")
    @DisplayName("UI WildFlyAstra. Удаление продукта")
    void delete() {
        WildFlyAstraPage wildFlyPage = new WildFlyAstraPage(product);
        wildFlyPage.runActionWithCheckCost(CompareType.LESS, wildFlyPage::delete);
    }
}
