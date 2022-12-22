package ui.cloud.tests.orders.podman;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.Podman;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
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
    //= Podman.builder().build().buildFromLink("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/containers/orders/af70df85-c2f2-4141-9676-f321e18aa54d/main?context=proj-pkvckn08w9&type=project&org=vtb");

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
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            PodmanOrderPage orderPage = new PodmanOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            orderPage.getGroup().select(accessGroup.getPrefixName());
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
    @DisplayName("UI Podman. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        podmanPage.checkHeadersHistory();
        podmanPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(5)
    @TmsLink("851388")
    @DisplayName("UI Podman. Расширить диск")
    void expandDisk() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.MORE, () -> podmanPage.enlargeDisk("/app", "20", new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Order(6)
    @TmsLink("1295870")
    @DisplayName("UI Podman. Проверить конфигурацию")
    void vmActCheckConfig() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage.checkConfiguration(new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Order(8)
    @TmsLinks({@TmsLink("1091838"), @TmsLink("1091841")})
    @DisplayName("UI Podman. Добавить группу доступа")
    void addGroup() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage.deleteGroup("podman_admin",new Table("Роли узла").getRowByIndex(0)));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage.addGroup("podman_admin", Collections.singletonList(accessGroup.getPrefixName()),new Table("Роли узла").getRowByIndex(0)));

    }

    @Test
    @Order(9)
    @TmsLink("1091844")
    @DisplayName("UI Podman. Изменить состав группы")
    void changeGroup() {
        PodmanPage podmanPage = new PodmanPage(product);
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        podmanPage.runActionWithCheckCost(CompareType.EQUALS, () -> podmanPage.updateGroup("podman_admin",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName()),new Table("Роли узла").getRowByIndex(0)));
    }


    @Test
    @Order(100)
    @TmsLink("851392")
    @DisplayName("UI Podman. Удаление продукта")
    void delete() {
        PodmanPage podmanPage = new PodmanPage(product);
        podmanPage.runActionWithCheckCost(CompareType.LESS, podmanPage::delete);
    }

}
