package ui.cloud.tests.orders.rabbitMqClusterAstra;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.RabbitMQClusterAstra;
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

import static ui.cloud.pages.orders.OrderUtils.checkOrderCost;

@Epic("UI Продукты")
@Feature("RabbitMQClusterAstra")
@Tags({@Tag("ui"), @Tag("ui_rabbit_mq_cluster_astra")})
public class UiRabbitMqClusterAstraTest extends UiProductTest {
    private RabbitMQClusterAstra product;// = RabbitMQClusterAstra.builder().platform("OpenStack").segment("dev-srv-app").build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/application_integration/orders/1cd0d80b-ed4d-4967-8ff3-6e29ef894941/main?context=proj-ln4zg69jek&type=project&org=vtb");
    private final String nameUser = "atUser";
    private final String nameHost = "atHostName";

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
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
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            RabbitMqClusterAstraOrderPage orderPage = new RabbitMqClusterAstraOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroupManagerSelect().set(accessGroup);
            if (product.isDev())
                orderPage.getGroupAdministratorSelect().set(accessGroup);
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
    @DisplayName("UI RabbitMQClusterAstra. Проверка развертывания в истории действий")
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
    @Disabled("Проверяется у Astra Linux")
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
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.addUser(nameUser, "14888", "104878"));
    }

    @Test
    @Order(6)
    @TmsLink("1060319")
    @DisplayName("UI RabbitMqClusterAstraPage. Проверка уникальности имени пользователя Rabbit MQ")
    void checkUniquenessAddUser() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.checkUniquenessAddUser(nameUser, "14888", "104878"));
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
        rabbitMqClusterAstraPage.checkHeadersHistory();
        rabbitMqClusterAstraPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(9)
    @TmsLink("1060335")
    @DisplayName("UI RabbitMqClusterAstra. Редактировать права на виртуальные хосты RabbitMQ")
    void editPermissions() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.editPermissions(nameUser, nameHost));
    }

    @Test
    @Order(10)
    @TmsLink("1060337")
    @DisplayName("UI RabbitMqClusterAstra. Удалить права на виртуальный хост RabbitMQ")
    void deletePermissions() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.deletePermissions(nameUser));
    }

    @Test
    @Order(11)
    @TmsLink("1060321")
    @DisplayName("UI RabbitMqClusterAstraPage. Удалить виртуальные хосты RabbitMQ")
    void deleteVirtualHosts() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.deleteVirtualHosts(nameHost));
    }

    @Test
    @Order(12)
    @TmsLink("1060320")
    @DisplayName("UI RabbitMqClusterAstraPage. Удалить пользователя RabbitMQ")
    void deleteUser() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.deleteUser());
    }


    @Test
    @Disabled("Проверяется у Astra Linux")
    @Order(13)
    @TmsLink("1060327")
    @DisplayName("UI RabbitMqClusterAstraPage. Проверить конфигурацию")
    void checkConfiguration() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::checkConfiguration);

    }

    @Test
    @Order(14)
    @TmsLink("1060333")
    @DisplayName("UI RabbitMqClusterAstraPage. Проверка доступа к Web интерфейсу управления через AD")
    void openAdminConsole() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::openPointConnect);
    }

    @Test
    @Order(15)
    @TmsLink("")
    @DisplayName("UI RabbitMqClusterAstra. Балансировка очередей")
    void reBalanceQueue() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::reBalanceQueue);
    }

    @Test
    @Order(16)
    @TmsLink("")
    @DisplayName("UI RabbitMqClusterAstra. Синхронизировать данные кластера RabbitMQ")
    void synchronizeData() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::synchronizeData);
    }

    @Test
    @Order(17)
    @TmsLink("")
    @DisplayName("UI RabbitMqClusterAstra. Изменить группу доступа на WEB интерфейс")
    void changeGroup() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.changeGroupWeb("Administrator", product.additionalAccessGroup()));
    }

    @Test
    @Order(18)
    @TmsLink("")
    @Disabled
    @DisplayName("UI RabbitMqClusterAstra. Удалить группу доступа на WEB интерфейс")
    void deleteGroup() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, () -> rabbitMqClusterAstraPage.deleteGroupWeb("Manager"));
    }

    @Test
    @Order(19)
    @TmsLink("")
    @DisplayName("UI RabbitMqClusterAstra. Вертикальное масштабирование кластера")
    void verticalScaling() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::verticalScaling);
    }

    @Test
    @Order(20)
    @TmsLink("")
    @DisplayName("UI RabbitMqClusterAstra. Обновить операционную систему")
    void updateOs() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::updateOs);
    }

    @Test
    @Order(21)
    @TmsLink("")
    @DisplayName("UI RabbitMqClusterAstra. Перенос кворумной ноды в OpenStack")
    void transferNode() {
        RabbitMqClusterAstraPage rabbitMqClusterAstraPage = new RabbitMqClusterAstraPage(product);
        rabbitMqClusterAstraPage.runActionWithCheckCost(CompareType.EQUALS, rabbitMqClusterAstraPage::transferNode);
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
