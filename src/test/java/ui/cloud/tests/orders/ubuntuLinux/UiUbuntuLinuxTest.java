package ui.cloud.tests.orders.ubuntuLinux;

import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.Ubuntu;
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
@Feature("Ubuntu Linux")
@Tags({@Tag("ui"), @Tag("ui_ubuntu_linux")})
public class UiUbuntuLinuxTest extends UiProductTest {

    Ubuntu product;
    //= Ubuntu.builder().build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/compute/orders/9d680891-9998-491b-a5db-a6cf2d2fa29b/main?context=proj-1oob0zjo5h&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("378015")
    @Order(1)
    @DisplayName("UI UbuntuLinux. Заказ")
    void orderScyllaDB() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            UbuntuLinuxOrderPage orderPage = new UbuntuLinuxOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            orderPage.getConfigure().set(Product.getFlavor(product.getMinFlavor()));
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
            UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
            ubuntuLinuxPage.waitChangeStatus(Duration.ofMinutes(25));
            ubuntuLinuxPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        Assertions.assertEquals(preBillingProductPrice, ubuntuLinuxPage.getCostOrder(), 0.01);
    }


    @Test
    @TmsLink("1342206")
    @Order(2)
    @DisplayName("UI UbuntuLinux. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.getBtnGeneralInfo().click();
        ubuntuLinuxPage.checkHeadersHistory();
        ubuntuLinuxPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(3)
    @TmsLink("378042")
    @DisplayName("UI UbuntuLinux. Перезагрузить по питанию")
    void restart() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, ubuntuLinuxPage::restart);
    }


    @Test
    @Order(5)
    @TmsLink("378050")
    @DisplayName("UI UbuntuLinux. Расширить диск")
    void expandDisk() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.MORE, () ->ubuntuLinuxPage.enlargeDisk("/app", "20", new Table("Размер, ГБ").getRowByIndex(0)));
    }

    @Test
    @Order(6)
    @TmsLink("1295829")
    @DisplayName("UI UbuntuLinux. Проверить конфигурацию")
    void vmActCheckConfig() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, ubuntuLinuxPage::checkConfiguration);
    }

    @Test
    @Order(8)
    @TmsLinks({@TmsLink("1090952"), @TmsLink("1090957")})
    @DisplayName("UI UbuntuLinux. Добавить группу доступа")
    void addGroup() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> ubuntuLinuxPage.deleteGroup("user"));
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> ubuntuLinuxPage.addGroup("user", Collections.singletonList(accessGroup.getPrefixName())));

    }

    @Test
    @Order(9)
    @TmsLink("1090987")
    @DisplayName("UI UbuntuLinux. Изменить состав группы")
    void changeGroup() {
        UbuntuLinuxPage ubuntuLinuxPage = new UbuntuLinuxPage(product);
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        ubuntuLinuxPage.runActionWithCheckCost(CompareType.EQUALS, () -> ubuntuLinuxPage.updateGroup("user",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }


    @Test
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
