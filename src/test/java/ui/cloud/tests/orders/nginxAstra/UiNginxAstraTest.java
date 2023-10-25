package ui.cloud.tests.orders.nginxAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Nginx;
import org.junit.EnabledIfEnv;
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
@Feature("NginxAstra")
@Tags({@Tag("ui"), @Tag("ui_nginx_astra")})
public class UiNginxAstraTest extends UiProductTest {

    Nginx product;// = Nginx.builder().build().buildFromLink("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/web/orders/8606c9be-6bdd-4c30-9627-effe1f33ccea/main?context=proj-pkvckn08w9&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("847274")
    @Order(1)
    @DisplayName("UI NginxAstra. Заказ")
    void orderNginxAstra() {
        double prebillingCost;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            NginxAstraOrderPage orderPage = new NginxAstraOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getDataCentreSelect().setByDataValue(product.getDataCentre());
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
            NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
            nginxAstraPage.waitChangeStatus(Duration.ofMinutes(25));
            nginxAstraPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        checkOrderCost(prebillingCost, nginxAstraPage);
    }

    @Test
    @TmsLink("1644514")
    @Order(2)
    @DisplayName("UI NginxAstra. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        nginxAstraPage.getGeneralInfoTab().switchTo();
        nginxAstraPage.checkHeadersHistory();
        nginxAstraPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[2]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(3)
    @TmsLink("1107519")
    @DisplayName("UI NginxAstra. Обновить сертификат NginxAstra")
    void updateCertificate() {
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        nginxAstraPage.runActionWithCheckCost(CompareType.EQUALS, nginxAstraPage::updateCertificate);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(4)
    @TmsLink("847275")
    @DisplayName("UI NginxAstra. Расширить точку монтирования")
    void expandDisk() {
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        nginxAstraPage.runActionWithCheckCost(CompareType.MORE, () -> nginxAstraPage.enlargeDisk("/app", "20", new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(5)
    @TmsLinks({@TmsLink("1644511"), @TmsLink("1644512"), @TmsLink("1644510")})
    @DisplayName("UI NginxAstra. Удаление/Добавление/Изменение группы")
    void addGroup() {
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        nginxAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> nginxAstraPage.addGroupInNode("user", Collections.singletonList(product.accessGroup())));
        nginxAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> nginxAstraPage.updateGroupInNode("user", Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
        nginxAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> nginxAstraPage.deleteGroupInNode("nginx_admin", product.accessGroup()));
    }


    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(6)
    @TmsLink("1644508")
    @DisplayName("UI NginxAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        nginxAstraPage.runActionWithCheckCost(CompareType.EQUALS, nginxAstraPage::checkConfiguration);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(7)
    @TmsLink("1644505")
    @DisplayName("UI NginxAstra. Изменить конфигурацию")
    void changeConfiguration() {
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        nginxAstraPage.runActionWithCheckCost(CompareType.MORE, nginxAstraPage::changeConfiguration);
    }

    @Test
    @Order(8)
    @TmsLink("1644504")
    @DisplayName("UI NginxAstra. Мониторинг ОС")
    void monitoringOs() {
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        nginxAstraPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("847280")
    @DisplayName("UI NginxAstra. Удаление продукта")
    void delete() {
        NginxAstraPage nginxAstraPage = new NginxAstraPage(product);
        nginxAstraPage.runActionWithCheckCost(CompareType.LESS, nginxAstraPage::delete);
    }
}
