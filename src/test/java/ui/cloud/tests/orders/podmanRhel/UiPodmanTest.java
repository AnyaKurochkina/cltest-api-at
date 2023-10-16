package ui.cloud.tests.orders.podmanRhel;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Podman;
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
import static ui.elements.TypifiedElement.scrollCenter;

@Epic("UI Продукты")
@Feature("Podman")
@Tags({@Tag("ui"), @Tag("ui_podman")})
public class UiPodmanTest extends UiProductTest {

    Podman product;// = Podman.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/containers/orders/9c0628a1-2553-4ac3-ab22-07a8a9c71457/main?context=proj-lww1vo6okh&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("851389")
    @Order(1)
    @DisplayName("UI Podman. Заказ")
    void orderPodman() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectCategory("Контейнеры")
                    .expandProductsList()
                    .selectProduct(product.getProductName());
            PodmanOrderPage orderPage = new PodmanOrderPage();
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getDataCentreSelect().set(product.getDataCentre());
            orderPage.getOsVersionSelect().set(product.getOsVersion());
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
            PodmanPage podmanPage = new PodmanPage(product);
            podmanPage.waitChangeStatus(Duration.ofMinutes(25));
            podmanPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        PodmanPage podmanPage = new PodmanPage(product);
        checkOrderCost(prebillingCost, podmanPage);
    }

    @Test
    @TmsLink("1349840")
    @Order(2)
    @DisplayName("UI Podman. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.getGeneralInfoTab().switchTo();
        podmanPage.checkHeadersHistory();
        podmanPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(5)
    @TmsLink("851388")
    @DisplayName("UI Podman. Расширить точку монтирования")
    void expandDisk() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.MORE, () -> podmanPage
                .enlargeDisk("/app", "20", new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(6)
    @TmsLink("1295870")
    @DisplayName("UI Podman. Проверить конфигурацию")
    void vmActCheckConfig() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage
                .checkConfiguration(new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(8)
    @TmsLinks({@TmsLink("1091838"), @TmsLink("1091841")})
    @DisplayName("UI Podman. Удалить и добавить группу доступа")
    void addGroup() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage
                .deleteGroup("podman_admin", new Table("Роли узла").getRow(0).get()));
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage
                .addGroup("podman_admin", Collections.singletonList(product.accessGroup()),
                        new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(9)
    @TmsLink("1091844")
    @DisplayName("UI Podman. Изменить состав группы")
    void changeGroup() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage.updateGroup("podman_admin",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup()),
                new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Order(10)
    @TmsLink("1296740")
    @DisplayName("UI Podman. Мониторинг ОС")
    void monitoringOs() {
        PodmanPage podmanPage = new PodmanPage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        podmanPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("851392")
    @DisplayName("UI Podman. Удалить рекурсивно")
    void delete() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.LESS, podmanPage::delete);
    }
}
