package ui.cloud.tests.orders.rabbitMqClusterAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.RabbitMQClusterAstra;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.*;
import ui.cloud.tests.ActionParameters;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("RabbitMQClusterAstra")
@Tags({@Tag("ui"), @Tag("ui_rabbit_mq_cluster_astra")})
public class UiRabbitMqClusterAstraTest extends UiProductTest {
    RabbitMQClusterAstra product = RabbitMQClusterAstra.builder().build().buildFromLink("https://ift2-portal-front.oslb-dev01.corp.dev.vtb/all/orders/a0147196-b8bb-47db-85d9-13f5157e8ce6/main?context=proj-pkvckn08w9&type=project&org=vtb");

    String nameUser = "atUser";
    String nameHost = "atHostName";
    String nameGroup = "cloud-zorg-dev-group";

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("1060322")
    @Order(1)
    @DisplayName("UI RabbitMQClusterAstra. Заказ")
    void orderRabbitMQClusterAstra() {
        double prebillingCost;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            RabbitMqClusterAstraOrderPage orderPage = new RabbitMqClusterAstraOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupSelect().set(accessGroup);
            orderPage.getGroup2Select().set(accessGroup);
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
            rabbitMqClusterAstraPage.waitChangeStatus(Duration.ofMinutes(25));
            rabbitMqClusterAstraPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        checkOrderCost(prebillingCost, rabbitMqClusterAstraPage);
    }

    @Test
    @TmsLink("1727719")
    @Order(2)
    @DisplayName("UI RabbitMQClusterAstra. Проверка полей заказа")
    void
    checkHeaderHistoryTable() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.getBtnGeneralInfo().click();
        rabbitMqClusterAstraPage.checkHeadersHistory();
        rabbitMqClusterAstraPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(3)
    @TmsLink("1060318")
    @DisplayName("UI RabbitMqClusterAstra. Обновить сертификаты RabbitMQ")
    void updateCertificate() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::updateCertificate);
    }

    @Test
    @Order(4)
    @TmsLink("1060330")
    @DisplayName("UI RabbitMqClusterAstra. Расширить точку монтирования")
    void expandDisk() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.MORE, () -> rabbitMqClusterAstraPage.enlargeDisk("/app", "20", new Table("Роли узла").getRow(0).get()));
    }

    @Test
    @Order(5)
    @TmsLink("1060324")
    @DisplayName("UI RabbitMqClusterAstraPage. Создать пользователя RabbitMQ")
    void addUser() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.addUser(nameUser));
    }
    @Test
    @Order(6)
    @TmsLink("1060324")
    @DisplayName("UI RabbitMqClusterAstraPage. Проверка уникальности имени пользователя Rabbit MQ")
    void checkUniquenessAddUser() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.checkUniquenessAddUser(nameUser));

    }

    @Test
    @Order(7)
    @TmsLink("1060334")
    @DisplayName("UI RabbitMqClusterAstraPage. Создать виртуальные хосты RabbitMQ")
    void createVirtualHosts() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.createVirtualHosts(nameHost));
    }
    @Test
    @Order(8)
    @TmsLink("1060325")
    @DisplayName("UI RabbitMqClusterAstraPage. Проверка уникальности имени виртуального хоста RabbitMQ")
    void checkUniquenessСreateVirtualHosts() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.createVirtualHosts(nameHost));
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.checkUniquenessСreateVirtualHosts(nameHost));
        rabbitMqClusterAstraPage.getBtnGeneralInfo().click();
        rabbitMqClusterAstraPage.checkHeadersHistory();
        rabbitMqClusterAstraPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(9)
    @TmsLink("1060331")
    @DisplayName("UI RabbitMqClusterAstra. Добавить права на виртуальные хосты RabbitMQ")
    void addPermissions() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.addPermissions(nameUser,nameHost));
    }

    @Test
    @Order(10)
    @TmsLink("1060335")
    @DisplayName("UI RabbitMqClusterAstra. Редактировать права на виртуальные хосты RabbitMQ")
    void editPermissions() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.addPermissions(nameUser,nameHost));
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.editPermissions(nameUser,nameHost));
    }

    @Test
    @Order(11)
    @TmsLink("1060337")
    @DisplayName("UI RabbitMqClusterAstra. Удалить права на виртуальный хост RabbitMQ")
    void deletePermissions() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.addPermissions(nameUser,nameHost));
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.deletePermissions(nameUser));
    }

    @Test
    @Order(12)
    @TmsLink("1060321")
    @DisplayName("UI RabbitMqClusterAstraPage. Удалить виртуальные хосты RabbitMQ")
    void deleteVirtualHosts() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.deleteVirtualHosts(nameHost));
    }

    @Test
    @Order(13)
    @TmsLink("1060320")
    @DisplayName("UI RabbitMqClusterAstraPage. Удалить пользователя RabbitMQ")
    void deleteUser() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.deleteUser(nameUser));
    }

    @Test
    @Order(14)
    @TmsLink("1060327")
    @DisplayName("UI RabbitMqClusterAstraPage. Проверить конфигурацию")
    void checkConfiguration() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::checkConfiguration);

    }

    @Test
    @Order(15)
    @TmsLink("1060333")
    @DisplayName("UI RabbitMqClusterAstraPage. Проверка доступа к Web интерфейсу управления через AD")
    void openAdminConsole() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::openAdminConsole);

    }


    @Test
    @Order(100)
    @TmsLink("1060329")
    @DisplayName("UI RabbitMqClusterAstra. Удаление продукта")
    void delete() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.delete();
    }
}
