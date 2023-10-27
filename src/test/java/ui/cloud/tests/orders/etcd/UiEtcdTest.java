package ui.cloud.tests.orders.etcd;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Etcd;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.EtcdOrderPage;
import ui.cloud.pages.orders.EtcdPage;
import ui.cloud.pages.orders.OrderUtils;
import ui.cloud.pages.orders.OrdersPage;
import ui.elements.Alert;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("Etcd")
@Tags({@Tag("ui"), @Tag("ui_Etcd")})
public class UiEtcdTest extends UiProductTest {

    Etcd product;// = Etcd.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/all/orders/48c224c9-38cf-4dc1-b78c-bb14e47985cf/main?context=proj-iv550odo9a&type=project&org=vtb");
    String nameUser = "at_user";



    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("")
    @Order(1)
    @DisplayName("UI Etcd. Заказ")
    void orderEtcd() {
        double prebillingCost;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            EtcdOrderPage orderPage = new EtcdOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getNameCluster().setValue("cluster");
            orderPage.getNameUser().setValue(nameUser);
            orderPage.getGeneratePassButton().shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
            orderPage.getNumberNodes().set("3");
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getGroupSelect().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            EtcdPage etcdPage = new EtcdPage(product);
            etcdPage.waitChangeStatus(Duration.ofMinutes(25));
            etcdPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        EtcdPage etcdPage = new EtcdPage(product);
        checkOrderCost(prebillingCost, etcdPage);
    }


    @Test
    @TmsLink("")
    @Order(2)
    @DisplayName("UI Etcd. Проверка полей заказа")
    void
    checkHeaderHistoryTable() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.getBtnGeneralInfo().click();
        etcdPage.checkHeadersHistory();
        etcdPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(3)
    @TmsLink("")
    @DisplayName("UI Etcd. Расширить точку монтирования")
    void expandDisk() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.runActionWithCheckCost(CompareType.MORE, () -> etcdPage.enlargeDisk("/app/etcd/data", "20", new Table("Роли узла").getRowByIndex(0)));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(4)
    @TmsLink("")
    @DisplayName("UI Etcd. Проверить конфигурацию")
    void vmActCheckConfig() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.runActionWithCheckCost(CompareType.EQUALS, etcdPage::checkConfiguration);
    }

    @Test
    @Order(5)
    @TmsLink("")
    @DisplayName("UI Etcd. Пользователь. Сброс пароля")
    void createLocalAccount() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.runActionWithCheckCost(CompareType.EQUALS, () -> etcdPage.resetPassword(nameUser));
    }

    @Test
    @Order(6)
    @TmsLink("")
    @DisplayName("UI Etcd. Создать сертификаты для пользователя etcd")
    void createCertificate() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.runActionWithCheckCost(CompareType.EQUALS, () -> etcdPage.createCertificate(nameUser));
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(7)
    @TmsLink("")
    @DisplayName("UI Etcd. Изменить конфигурацию")
    void changeConfiguration() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.runActionWithCheckCost(CompareType.EQUALS, etcdPage::changeConfiguration);
    }

    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(8)
    @TmsLinks({@TmsLink(""), @TmsLink(""), @TmsLink("")})
    @DisplayName("UI Etcd. Удалить/добавить/изменить группу доступа")
    void addGroup() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.runActionWithCheckCost(CompareType.EQUALS, () -> etcdPage.deleteGroupInNode("superuser", product.accessGroup()));
        etcdPage.runActionWithCheckCost(CompareType.EQUALS, () -> etcdPage.addGroupInNode("superuser", Collections.singletonList(product.accessGroup())));
        etcdPage.runActionWithCheckCost(CompareType.EQUALS, () -> etcdPage.updateGroupInNode("superuser", Arrays.asList(product.accessGroup(), product.additionalAccessGroup())));
    }

    @Test
    @Order(9)
    @EnabledIfEnv("prod")
    @TmsLink("")
    @DisplayName("UI Etcd. Мониторинг ОС")
    void monitoringOs() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("")
    @DisplayName("UI Etcd. Удаление продукта")
    void delete() {
        EtcdPage etcdPage = new EtcdPage(product);
        etcdPage.delete();
    }
}
