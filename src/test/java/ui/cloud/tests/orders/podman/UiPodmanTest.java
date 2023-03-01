package ui.cloud.tests.orders.podman;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Podman;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

@Epic("UI Продукты")
@Feature("Podman")
@Tags({@Tag("ui"), @Tag("ui_podman")})
public class UiPodmanTest extends UiProductTest {

    Podman product;
    //= Podman.builder().build().buildFromLink("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/containers/orders/103e20a2-2c9a-4a9c-96cd-c8d6b1307cae/main?context=proj-pkvckn08w9&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("851389")
    @Order(1)
    @DisplayName("UI Podman. Заказ")
    void orderScyllaDB() {
        double preBillingProductPrice;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            PodmanOrderPage orderPage = new PodmanOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroup().select(accessGroup);
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = EntitiesUtils.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
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
        Assertions.assertEquals(preBillingProductPrice, podmanPage.getCostOrder(), 0.01);
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
        new Graph().checkGraph();
    }

    @Test
    @Order(5)
    @TmsLink("851388")
    @DisplayName("UI Podman. Расширить точку монтирования")
    void expandDisk() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.MORE, () -> podmanPage
                .enlargeDisk("/app", "20", new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Order(6)
    @TmsLink("1295870")
    @DisplayName("UI Podman. Проверить конфигурацию")
    void vmActCheckConfig() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage
                .checkConfiguration(new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Order(8)
    @TmsLinks({@TmsLink("1091838"), @TmsLink("1091841")})
    @DisplayName("UI Podman. Удалить и добавить группу доступа")
    void addGroup() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage
                .deleteGroup("podman_admin", new Table("Роли узла").getRow(0).get()));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage
                .addGroup("podman_admin", Collections.singletonList(accessGroup.getPrefixName()),
                        new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Order(9)
    @TmsLink("1091844")
    @DisplayName("UI Podman. Изменить состав группы")
    void changeGroup() {
        PodmanPage podmanPage = new PodmanPage(product);
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random())
                .projectName(product.getProjectId()).build().createObject();
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage.updateGroup("podman_admin",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName()),
                new Table("Роли узла").getRow(0).get()));
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
