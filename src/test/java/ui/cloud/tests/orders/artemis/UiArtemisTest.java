package ui.cloud.tests.orders.artemis;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.Artemis;
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
import static ui.elements.TypifiedElement.scrollCenter;

@Epic("UI Продукты")
@Feature("Artemis")
@Tags({@Tag("ui"), @Tag("ui_artemis")})
public class UiArtemisTest extends UiProductTest {
    private Artemis product;// = Artemis.builder().platform("OpenStack").segment("dev-srv-app").build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/263c42d5-42f3-4d89-8d2a-2f00b480b6c1/main?context=proj-ahjjqmlgnm&type=project&org=vtb");
    private final String nameUser = "atUser";
    private final String nameHost = "atHostName";

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("")
    @Order(1)
    @DisplayName("UI Artemis. Заказ")
    void orderArtemis() {
        double prebillingCost;
        try {
            String accessGroup = product.accessGroup();
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            ArtemisOrderPage orderPage = new ArtemisOrderPage();
            orderPage.getOsVersionSelect().set(product.getOsVersion());
            orderPage.getSegmentSelect().set(product.getSegment());
            orderPage.getPlatformSelect().set(product.getPlatform());
            orderPage.getFlavorSelect().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            prebillingCost = OrderUtils.getCostValue(orderPage.getPrebillingCostElement());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            ArtemisPage artemisPage = new ArtemisPage(product);
            artemisPage.waitChangeStatus(Duration.ofMinutes(25));
            artemisPage.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        ArtemisPage artemisPage = new ArtemisPage(product);
        checkOrderCost(prebillingCost, artemisPage);
    }

    @Test
    @TmsLink("")
    @Order(2)
    @DisplayName("UI Artemis. Проверка графа в истории действий")
    void
    checkHeaderHistoryTable() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.getBtnGeneralInfo().click();
        artemisPage.checkHeadersHistory();
        artemisPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(3)
    @TmsLink("")
    @DisplayName("UI Artemis. Аварийное обновление сертификатов Artemis")
    void synchronizeData() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::emergencyUpdateCertificate);
    }

    @Test
    @Order(4)
    @TmsLink("")
    @DisplayName("UI Artemis. Обновление информации о кластере")
    void updateInfCluster() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::updateInfCluster);
    }


    @Test
    @Order(5)
    @TmsLink("")
    @DisplayName("UI Artemis. Отправить конфигурацию кластера на email")
    void sendConfiguration() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::sendConfiguration);
    }

    @Test
    @Order(6)
    @TmsLink("")
    @DisplayName("UI Artemis. Перезапуск кластера")
    void resetCluster() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::resetCluster);
    }

    @Test
    @Order(7)
    @TmsLink("")
    @DisplayName("UI Artemis. Создание сервиса")
    void createService() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::createService);
    }

    @Test
    @Order(8)
    @TmsLink("")
    @DisplayName("UI Artemis. Создание клиента (own) без сервиса")
    void createClientWithOutService() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::createClientWithOutService);
    }

    @Test
    @Order(9)
    @TmsLink("")
    @DisplayName("UI Artemis. Создание клиента (own) с сервисом")
    void createClientWithService() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::createClientWithService);
    }

    @Test
    @Order(10)
    @TmsLink("")
    @DisplayName("UI Artemis. Создание клиента (temporary)")
    void createClientTemporary() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::createClientTemporary);
    }

    @Test
    @Order(11)
    @TmsLink("")
    @DisplayName("UI Artemis. Создание прав доступа клиента")
    void createRightClient() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::createRightClient);
    }

    @Test
    @Order(12)
    @TmsLink("")
    @DisplayName("UI Artemis. Удаление прав клиента")
    void deleteRightClient() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::deleteRightClient);
    }

    @Test
    @Order(13)
    @TmsLink("")
    @DisplayName("UI Artemis. Удаление клиента")
    void deleteClient() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::deleteClient);
    }


    @Test
    @Order(14)
    @TmsLink("")
    @DisplayName("UI Artemis. Удаление сервиса")
    void deleteService() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::deleteService);
    }

    @Test
    @Order(15)
    @TmsLink("")
    @DisplayName("UI Artemis. Мониторинг ОС")
    void monitoringOs() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        artemisPage.checkClusterMonitoringOs();
    }

    @Test
    @Order(16)
    @TmsLink("")
    @DisplayName("UI Artemis. Обновление операционной системы на ВМ кластера")
    void updateOsVm() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::updateOsVm);
    }


    @Test
    @Order(17)
    @TmsLink("")
    @DisplayName("UI Artemis. Обновление сертификатов")
    void updateCertificate() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::updateCertificate);
    }

    @Test
    @Order(18)
    @TmsLink("")
    @Disabled
    @DisplayName("UI Artemis. Обновление версии инсталляции")
    void updateInstallVersion() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.EQUALS, artemisPage::updateInstallVersion);
    }

    @Test
    @Order(18)
    @TmsLink("")
    @DisplayName("UI Artemis. Вертикальное масштабирование")
    void verticalScaling() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.MORE, artemisPage::verticalScaling);
    }

    @Test
    @Order(19)
    @TmsLink("")
    @DisplayName("UI Artemis. Горизонтальное масштабирование")
    void horizontalScaling() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.MORE, artemisPage::horizontalScaling);
    }

    @Test
    @Order(20)
    @TmsLink("")
    @DisplayName("UI Artemis. Расширить точку монтирования ")
    void expandDisk() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.MORE, artemisPage::enlargeDisk);
    }

    @Test
    @Order(21)
    @TmsLink("")
    @DisplayName("UI Artemis. Проверить конфигурацию")
    void checkConfiguration() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.MORE, artemisPage::checkConfiguration);
    }

    @Test
    @Order(22)
    @TmsLink("")
    @DisplayName("UI Artemis. Включение\\отключение протоколов")
    void onOffProtokol() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.runActionWithCheckCost(CompareType.MORE, artemisPage::onOffProtokol);
    }


    @Test
    @Order(100)
    @TmsLink("1060329")
    @DisplayName("UI Artemis. Удаление продукта")
    void delete() {
        ArtemisPage artemisPage = new ArtemisPage(product);
        artemisPage.delete();
    }
}
