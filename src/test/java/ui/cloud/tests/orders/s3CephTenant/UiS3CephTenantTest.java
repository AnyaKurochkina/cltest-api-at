package ui.cloud.tests.orders.s3CephTenant;


import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.S3Ceph;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.OrderUtils;
import ui.cloud.pages.orders.OrdersPage;
import ui.cloud.pages.orders.S3CephTenantOrderPage;
import ui.cloud.pages.orders.S3CephTenantPage;
import ui.elements.Graph;
import ui.extesions.UiProductTest;

import java.time.Duration;

@Epic("UI Продукты")
@Feature("S3CephTenant")
@Tags({@Tag("ui"), @Tag("ui_s3_ceph_tenant")})
public class UiS3CephTenantTest extends UiProductTest {

    S3Ceph product;// = S3Ceph.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/object_storage/orders/0c8f067e-8b4e-4ab3-b469-69694702c34c/main?context=proj-iv550odo9a&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("891754")
    @Order(1)
    @DisplayName("UI S3CephTenant. Заказ")
    void orderS3Ceph() {
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            S3CephTenantOrderPage orderPage = new S3CephTenantOrderPage();
            orderPage.getSegmentSelect().set(product.getSegment());
            OrderUtils.clickOrder();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
            s3CepthPages.waitChangeStatus(Duration.ofMinutes(25));
            s3CepthPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
    }

    @Test
    @TmsLink("1458370")
    @Order(2)
    @DisplayName("UI S3CephTenant. Проверка развертывания заказа в истории действий")
    void checkHeaderHistoryTable() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.getBtnGeneralInfo().click();
        s3CepthPages.checkHeadersHistory();
        s3CepthPages.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().notContainsStatus(Graph.ERROR);
    }

    @Test
    @Order(3)
    @TmsLink("891767")
    @DisplayName("UI S3CephTenant. Добавить бакет")
    void addBucket() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.MORE, () -> s3CepthPages.addBucket("bucket", "12"));
    }

    @Test
    @Order(4)
    @TmsLink("891769")
    @DisplayName("UI S3CephTenant. Изменить настройки бакета")
    void changeSettingsBucket() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.MORE, () -> s3CepthPages.changeSettingsBucket("14"));
    }

    @Test
    @Order(5)
    @TmsLink("1240592")
    @DisplayName("UI S3CephTenant. Добавить правило жизненного цикла")
    void addRuLifeCycle() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.addRuLifeCycle("name", "11"));
    }

    @Test
    @Order(6)
    @TmsLink("1240621")
    @DisplayName("UI S3CephTenant. Изменить правило жизненного цикла")
    void changeRuLifeCycle() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.changeRuLifeCycle("ift", "15"));
    }

    @Test
    @Order(7)
    @TmsLink("1619410")
    @DisplayName("UI S3CephTenant. Добавить правило CORS")
    void addRuleCorse() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.addCorsRule("ruleCorse", "15 sec", "15"));
    }

    @Test
    @Order(8)
    @TmsLink("1619407")
    @DisplayName("UI S3CephTenant. Изменить правило CORS")
    void changeRuleCorse() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.changeCorsRule("10 sec", "10"));
    }

    @Test
    @Order(9)
    @TmsLink("1619425")
    @DisplayName("UI S3CephTenant. Удалить правило CORS")
    void deleteRuleCorse() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, s3CepthPages::deleteCorsRule);
    }

    @Test
    @Order(10)
    @TmsLink("1240649")
    @DisplayName("UI S3CephTenant. Удалить правило жизненного цикла")
    void deleteRuLifeCycle() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.deleteRuLifeCycle());
    }

    @Test
    @Order(11)
    @TmsLink("891772")
    @DisplayName("UI S3CephTenant. Добавить пользователя")
    void addUser() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.addUser("user"));
    }


    @Test
    @Order(12)
    @TmsLink("891775")
    @DisplayName("UI S3CephTenant. Добавить политику")
    void addAccessPolicy() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.addAccessPolicy("user"));
    }

    @Test
    @Order(13)
    @TmsLink("891777")
    @DisplayName("UI S3CephTenant. Изменить политику")
    void changeAccessPolicy() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.changeAccessPolicy());
    }

    @Test
    @Order(14)
    @TmsLink("891770")
    @DisplayName("UI S3CephTenant. Удалить бакет")
    void deleteBucket() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.LESS, () -> s3CepthPages.deleteBucket());
    }

    @Test
    @Order(15)
    @TmsLink("891774")
    @DisplayName("UI S3CephTenant. Удалить пользователя")
    void deleteUser() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage(product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.deleteUser());
    }

    @Test
    @Order(100)
    @TmsLink("891766")
    @DisplayName("UI S3CephTenant. Удалить тенант")
    void deleteTenant() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.LESS, () -> s3CepthPages.deleteTenant());
    }
}
