package ui.cloud.tests.orders.s3CephTenant;


import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Redis;
import models.cloud.orderService.products.S3Ceph;
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
@Feature("S3CephTenant")
@Tags({@Tag("ui"), @Tag("ui_s3_ceph_tenant")})
public class UiS3CephTenantTest extends UiProductTest {

    S3Ceph product = S3Ceph.builder().build().buildFromLink("https://ift2-portal-front.apps.sk5-soul01.corp.dev.vtb/object_storage/orders/0469c17f-1b54-42a4-adce-09b96ecc2f49/main?context=proj-pkvckn08w9&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
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
              orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
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
    @DisplayName("UI S3CephTenant. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.getBtnGeneralInfo().click();
        s3CepthPages.checkHeadersHistory();
        s3CepthPages.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(3)
    @TmsLink("891767")
    @DisplayName("UI S3CephTenant. Добавить бакет")
    void addBucket() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.MORE, () -> s3CepthPages.addBucket("bucket","12"));
    }

    @Test
    @Order(4)
    @TmsLink("891769")
    @DisplayName("UI S3CephTenant. Изменить настройки бакета")
    void changeSettingsBucket() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.MORE, () -> s3CepthPages.changeSettingsBucket("14"));
    }

    @Test
    @Order(5)
    @TmsLink("1240592")
    @DisplayName("UI S3CephTenant. Добавить правило жизненного цикла")
    void addRuLifeCycle() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.addRuLifeCycle("name","11"));
    }

    @Test
    @Order(6)
    @TmsLink("1240621")
    @DisplayName("UI S3CephTenant. Изменить правило жизненного цикла")
    void changeRuLifeCycle() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.changeRuLifeCycle("ift","15"));
    }

    @Test
    @Order(7)
    @TmsLink("1240649")
    @DisplayName("UI S3CephTenant. Удалить правило жизненного цикла")
    void deleteRuLifeCycle() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.deleteRuLifeCycle());
    }

    @Test
    @Order(8)
    @TmsLink("891772")
    @DisplayName("UI S3CephTenant. Добавить пользователя")
    void addUser() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.addUser("user"));
    }

    @Test
    @Order(9)
    @TmsLink("891774")
    @DisplayName("UI S3CephTenant. Удалить пользователя")
    void deleteUser() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.deleteUser());
    }

    @Test
    @Order(10)
    @TmsLink("891775")
    @DisplayName("UI S3CephTenant. Добавить политику")
    void addAccessPolicy() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.EQUALS, () -> s3CepthPages.addAccessPolicy("user"));
    }

    @Test
    @Order(11)
    @TmsLink("891770")
    @DisplayName("UI S3CephTenant. Удалить бакет")
    void deleteBucket() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.LESS, () -> s3CepthPages.deleteBucket());
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
