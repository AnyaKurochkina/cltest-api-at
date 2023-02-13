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

//    @Test
//    @TmsLink("")
//    @Order(1)
//    @DisplayName("UI S3CephTenant. Заказ")
//    void orderS3Ceph() {
//        try {
//            new IndexPage()
//                    .clickOrderMore()
//                    .selectProduct(product.getProductName());
//              S3CephTenantOrderPage orderPage = new S3CephTenantOrderPage();
//              orderPage.getSegment().selectByValue(product.getSegment());
//            orderPage.orderClick();
//            new OrdersPage()
//                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
//                    .getElementByColumn("Продукт")
//                    .hover()
//                    .click();
//            S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
//            s3CepthPages.waitChangeStatus(Duration.ofMinutes(25));
//            s3CepthPages.checkLastAction("Развертывание");
//        } catch (Throwable e) {
//            product.setError(e.toString());
//            throw e;
//        }
//    }


//    @Test
//    @TmsLink("")
//    @Order(2)
//    @DisplayName("UI S3CephTenant. Проверка полей заказа")
//    void checkHeaderHistoryTable() {
//        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
//        s3CepthPages.getBtnGeneralInfo().click();
//        s3CepthPages.checkHeadersHistory();
//        s3CepthPages.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
//        new Graph().checkGraph();
//    }

    @Test
    @Order(3)
    @TmsLink("")
    @DisplayName("UI S3CephTenant. Добавить бакет")
    void addBucket() {
        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
        s3CepthPages.runActionWithCheckCost(CompareType.MORE, () -> s3CepthPages.addBucket("bucket","12"));
    }

//    @Test
//    @Order(29)
//    @TmsLink("")
//    @DisplayName("UI S3CephTenant. Удалить бакет")
//    void deleteBucket() {
//        S3CephTenantPage s3CepthPages = new S3CephTenantPage (product);
//        s3CepthPages.runActionWithCheckCost(CompareType.LESS, () -> s3CepthPages.deleteBucket());
//    }

//
//    @Test
//    @Order(10)
//    @TmsLink("")
//    @DisplayName("UI RedisAstra. Изменить конфигурацию")
//    void changeConfiguration() {
//        RedisAstraPage redisPage = new RedisAstraPage(product);
//        redisPage.runActionWithCheckCost(CompareType.MORE, redisPage::changeConfiguration);
//    }
//
//    @Test
//    @Order(11)
//    @TmsLink("")
//    @DisplayName("UI RedisAstra. Проверить конфигурацию")
//    void vmActCheckConfig() {
//        RedisAstraPage redisPage = new RedisAstraPage(product);
//        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::checkConfiguration);
//    }
//
//    @Test
//    @Order(19)
//    @TmsLink("")
//    @DisplayName("UI RedisAstra. Сбросить пароль")
//    void resetPassword () {
//        RedisAstraPage redisPage = new RedisAstraPage(product);
//        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::resetPassword);
//    }
//
//    @Test
//    @TmsLinks({@TmsLink(""), @TmsLink("1454015")})
//    @Order(25)
//    @DisplayName("UI RedisAstra. Добавление/удаление группы доступа")
//    void deleteGroup() {
//        RedisAstraPage redisPage = new RedisAstraPage(product);
//        //redisPage.deleteGroup("superuser");
//        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
//        redisPage.addGroup("superuser", Collections.singletonList(accessGroup.getPrefixName()));
//    }
//
//    @Test
//    @TmsLink("")
//    @Order(26)
//    @DisplayName("UI RedisAstra. Изменение группы доступа")
//    void updateGroup() {
//        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
//        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
//        RedisAstraPage redisPage = new RedisAstraPage(product);
//        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.updateGroup("superuser",
//                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
//    }
//
//    @Test
//    @Order(27)
//    @TmsLink("")
//    @DisplayName("UI Windows. Мониторинг ОС")
//    void monitoringOs() {
//        RedisAstraPage redisPage = new RedisAstraPage(product);
//        redisPage.checkMonitoringOs();
//    }
//
//    @Test
//    @Order(100)
//    @TmsLink("")
//    @DisplayName("UI RedisAstra. Удаление продукта")
//    void delete() {
//        RedisAstraPage redisPage = new RedisAstraPage(product);
//        redisPage.delete();
//    }

 }
