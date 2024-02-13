package ui.cloud.tests.orders.podmanAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.PodmanAstra;
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
@Feature("Podman (Astra)")
@Tags({@Tag("ui"), @Tag("ui_podman_astra")})
public class UiPodmanAstraTest extends UiProductTest {

    private PodmanAstra product;// = PodmanAstra.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/all/orders/92155aeb-773d-4a86-9c17-d9237d6e35b3/main?context=proj-iv550odo9a&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("851389")
    @Order(1)
    @DisplayName("UI PodmanAstra. Заказ")
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
            PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
            podmanAstraPage.waitChangeStatus(Duration.ofMinutes(25));
            podmanAstraPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        checkOrderCost(prebillingCost, podmanAstraPage);
    }

    @Test
    @TmsLink("1349840")
    @Order(2)
    @DisplayName("UI PodmanAstra. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.getGeneralInfoTab().switchTo();
        podmanAstraPage.checkHeadersHistory();
        podmanAstraPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(3)
    @TmsLink("851388")
    @DisplayName("UI PodmanAstra. Расширить точку монтирования")
    void expandDisk() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.runActionWithCheckCost(CompareType.MORE, () -> podmanAstraPage
                .enlargeDisk("/app", "20", new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(4)
    @TmsLink("1295870")
    @DisplayName("UI PodmanAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanAstraPage
                .checkConfiguration(new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(5)
    @TmsLinks({@TmsLink("1091838"), @TmsLink("1091841")})
    @DisplayName("UI PodmanAstra. Удалить и добавить группу доступа")
    void addGroup() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanAstraPage
                .deleteGroup("podman_admin", new Table("Роли узла").getRow(0).get()));
        podmanAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanAstraPage
                .addGroup("podman_admin", Collections.singletonList(product.accessGroup()),
                        new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(6)
    @TmsLink("1091844")
    @DisplayName("UI PodmanAstra. Изменить состав группы")
    void changeGroup() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanAstraPage.updateGroup("podman_admin",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup()),
                new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Order(7)
    @TmsLink("1296740")
    @DisplayName("UI PodmanAstra. Мониторинг ОС")
    void monitoringOs() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        podmanAstraPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(8)
    @TmsLink("")
    @DisplayName("UI Podman. Обновить ОС")
    void updateOs() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.runActionWithCheckCost(CompareType.EQUALS, podmanAstraPage::updateOs);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(9)
    @TmsLink("")
    @DisplayName("UI PodmanAstra. Установить Ключ-Астром")
    void addKeyAstrom() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.runActionWithCheckCost(CompareType.MORE, podmanAstraPage::addKeyAstrom);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(10)
    @TmsLink("")
    @DisplayName("UI PodmanAstra. Удалить Ключ-Астром")
    void delKeyAstrom() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.runActionWithCheckCost(CompareType.LESS, podmanAstraPage::delKeyAstrom);
    }

    @Test
    @Order(100)
    @TmsLink("851392")
    @DisplayName("UI Podman. Удалить рекурсивно")
    void delete() {
        PodmanAstraPage podmanAstraPage = new PodmanAstraPage(product);
        podmanAstraPage.runActionWithCheckCost(CompareType.LESS, podmanAstraPage::delete);
    }
}
