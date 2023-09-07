package ui.cloud.tests.orders.ubuntuLinux;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Ubuntu;
import org.junit.DisabledIfEnv;
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
@Feature("Ubuntu Linux")
@Tags({@Tag("ui"), @Tag("ui_ubuntu_linux")})
public class UiUbuntuLinuxTest extends UiProductTest {

    Ubuntu product; //= Ubuntu.builder().build().buildFromLink("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/compute/orders/bed207dc-ccbc-49fb-92fa-d889fc22e2c3/main?context=proj-pkvckn08w9&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("378015")
    @Order(1)
    @DisplayName("UI UbuntuLinux. Заказ")
    void orderScyllaDB() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .expandProductsList()
                    .selectProduct(product.getProductName());
            UbuntuLinuxOrderPage orderPage = new UbuntuLinuxOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getRoleSelect().set("user");
            orderPage.getGroupSelect().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
            ubuntuLinuxPage.waitChangeStatus(Duration.ofMinutes(25));
            ubuntuLinuxPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        checkOrderCost(prebillingCost, ubuntuLinuxPage);
    }

    @Test
    @TmsLink("1342206")
    @Order(2)
    @DisplayName("UI UbuntuLinux. Проверка развертывания в истории действий")
    void checkHeaderHistoryTable() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.getBtnGeneralInfo().click();
        ubuntuLinuxPage.checkHeadersHistory();
        ubuntuLinuxPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @DisabledIfEnv("prod")
    @Order(3)
    @TmsLink("378042")
    @DisplayName("UI UbuntuLinux. Перезагрузить по питанию")
    void restart() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, ubuntuLinuxPage::restart);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(5)
    @TmsLink("378050")
    @DisplayName("UI UbuntuLinux. Расширить точку монтирования")
    void expandDisk() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.MORE, () -> ubuntuLinuxPage
                .enlargeDisk("/app", "20", new Table("Размер, ГБ").getRowByIndex(0)));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(6)
    @TmsLink("1295829")
    @DisplayName("UI UbuntuLinux. Проверить конфигурацию")
    void vmActCheckConfig() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, ubuntuLinuxPage::checkConfiguration);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(8)
    @TmsLinks({@TmsLink("1090952"), @TmsLink("1090957")})
    @DisplayName("UI UbuntuLinux. Удалить и добавить группу доступа")
    void addGroup() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> ubuntuLinuxPage.deleteGroup("user"));
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> ubuntuLinuxPage.addGroup("user", Collections.singletonList(product.accessGroup())));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(9)
    @TmsLink("1090987")
    @DisplayName("UI UbuntuLinux. Изменить состав группы")
    void changeGroup() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> ubuntuLinuxPage.updateGroup("user",
                Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(11)
    @TmsLink("378040")
    @DisplayName("UI UbuntuLinux. Изменить конфигурацию")
    void changeConfiguration() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.MORE, ubuntuLinuxPage::changeConfiguration);
    }

    @Test
    @Order(12)
    @TmsLink("1296749")
    @DisplayName("UI UbuntuLinux. Мониторинг ОС")
    void checkMonitoringOs() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.checkMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("378043")
    @DisplayName("UI UbuntuLinux. Удаление продукта")
    void delete() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.LESS, ubuntuLinuxPage::delete);
    }
}
