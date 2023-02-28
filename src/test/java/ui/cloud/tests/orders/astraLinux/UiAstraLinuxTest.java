package ui.cloud.tests.orders.astraLinux;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
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
@Feature("Astra Linux")
@Tags({@Tag("ui"), @Tag("ui_astra_linux")})
public class UiAstraLinuxTest extends UiProductTest {

    Astra product;
    //= Astra.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/compute/orders/ae50ab87-de69-4c30-bcdd-c339726b8d13/main?context=proj-iv550odo9a&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("378275")
    @Order(1)
    @DisplayName("UI Astra. Заказ")
    void orderScyllaDB() {
        double preBillingProductPrice;
        try {
            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            AstraLinuxOrderPage orderPage = new AstraLinuxOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(accessGroup.getPrefixName());
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = EntitiesUtils.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
            astraLinuxPage.waitChangeStatus(Duration.ofMinutes(25));
            astraLinuxPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        Assertions.assertEquals(preBillingProductPrice, astraLinuxPage.getCostOrder(), 0.01);
    }

    @Test
    @TmsLink("1236736")
    @Order(2)
    @DisplayName("UI AstraLinux. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.getGeneralInfoTab().switchTo();
        astraLinuxPage.checkHeadersHistory();
        astraLinuxPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(3)
    @TmsLink("382916")
    @DisplayName("UI AstraLinux. Перезагрузить по питанию")
    void restart() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.runActionWithCheckCost(CompareType.EQUALS, astraLinuxPage::restart);
    }

    @Test
    @Order(4)
    @TmsLink("1267198")
    @Disabled
    @DisplayName("UI AstraLinux. Выпустить клиентский сертификат")
    void issueClientCertificate() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> astraLinuxPage.issueClientCertificate("nameCertificate"));
    }

    @Test
    @Order(5)
    @TmsLink("382920")
    @DisplayName("UI AstraLinux. Расширить точку монтирования")
    void expandDisk() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.runActionWithCheckCost(CompareType.MORE, () -> astraLinuxPage
                .enlargeDisk("/app", "20", new Table("Размер, ГБ").getRowByIndex(0)));
    }

    @Test
    @Order(6)
    @TmsLink("1267201")
    @DisplayName("UI AstraLinux. Проверить конфигурацию")
    void vmActCheckConfig() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.runActionWithCheckCost(CompareType.EQUALS, astraLinuxPage::checkConfiguration);
    }

    @Test
    @Order(8)
    @TmsLinks({@TmsLink("1090926"), @TmsLink("1090863")})
    @DisplayName("UI AstraLinux. Удалить и добавить группу доступа")
    void addGroup() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> astraLinuxPage.deleteGroup("user"));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        astraLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> astraLinuxPage.addGroup("user", Collections.singletonList(accessGroup.getPrefixName())));

    }

    @Test
    @Order(9)
    @TmsLink("1090932")
    @DisplayName("UI AstraLinux. Изменить состав группы")
    void changeGroup() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        astraLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> astraLinuxPage.updateGroup("user",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @Order(11)
    @TmsLink("382915")
    @DisplayName("UI AstraLinux. Изменить конфигурацию")
    void changeConfiguration() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.runActionWithCheckCost(CompareType.MORE, astraLinuxPage::changeConfiguration);
    }
    @Test
    @Order(12)
    @TmsLink("1164676")
    @DisplayName("UI AstraLinux. Мониторинг ОС")
    void monitoringOs() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.checkMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("382918")
    @DisplayName("UI AstraLinux. Удаление продукта")
    void delete() {
        AstraLinuxPage astraLinuxPage = new AstraLinuxPage(product);
        astraLinuxPage.runActionWithCheckCost(CompareType.LESS, astraLinuxPage::delete);
    }
}
