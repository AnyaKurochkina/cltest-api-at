package ui.cloud.tests.orders.redisAstra;


import com.codeborne.selenide.Condition;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Redis;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import steps.portalBack.PortalBackSteps;
import ui.cloud.pages.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.UiProductTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

@Epic("UI Продукты")
@Feature("Redis (Astra)")
@Tags({@Tag("ui"), @Tag("ui_redis_astra")})
public class UiRedisAstraTest extends UiProductTest {

    Redis product;
    // = Redis.builder().build().buildFromLink("https://console.blue.cloud.vtb.ru/db/orders/365e253d-bd94-4462-a282-5b3d44f1c9c6/main?context=proj-iv550odo9a&type=project&org=vtb");

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("796925")
    @Order(1)
    @DisplayName("UI RedisAstra. Заказ")
    void orderRedisAstra() {
        double preBillingProductPrice;
        try {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(product.getProjectId(), "", "compute");
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            RedisAstraOrderPage orderPage = new RedisAstraOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getGeneratePassButton().shouldBe(Condition.enabled).click();
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            orderPage.getConfigure().set(NewOrderPage.getFlavor(product.getMinFlavor()));
            orderPage.getGroup().select(accessGroup);
            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            preBillingProductPrice = EntitiesUtils.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowByColumnValue("Продукт", orderPage.getLabelValue())
                    .getElementByColumn("Продукт")
                    .hover()
                    .click();
            RedisAstraPage redisPages = new RedisAstraPage (product);
            redisPages.waitChangeStatus(Duration.ofMinutes(25));
            redisPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        RedisAstraPage redisPage = new RedisAstraPage(product);
        Assertions.assertEquals(preBillingProductPrice, redisPage.getCostOrder(), 0.01);
    }


    @Test
    @TmsLink("1236732")
    @Order(2)
    @DisplayName("UI RedisAstra. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.getBtnGeneralInfo().click();
        redisPage.checkHeadersHistory();
        redisPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(9)
    @TmsLink("796991")
    @DisplayName("UI RedisAstra. Расширить диск")
    void expandDisk() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, () -> redisPage.enlargeDisk("/app/redis/data", "20", new Table("Роли узла").getRowByIndex(0)));
    }


    @Test
    @Order(10)
    @TmsLink("797003")
    @DisplayName("UI RedisAstra. Изменить конфигурацию")
    void changeConfiguration() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.MORE, redisPage::changeConfiguration);
    }

    @Test
    @Order(11)
    @TmsLink("797006")
    @DisplayName("UI RedisAstra. Проверить конфигурацию")
    void vmActCheckConfig() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::checkConfiguration);
    }

    @Test
    @Order(19)
    @TmsLink("796997")
    @DisplayName("UI RedisAstra. Сбросить пароль")
    void resetPassword () {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, redisPage::resetPassword);
    }

    @Test
    @TmsLinks({@TmsLink("1454017"), @TmsLink("1454015")})
    @Order(25)
    @DisplayName("UI RedisAstra. Добавление/удаление группы доступа")
    void deleteGroup() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        //redisPage.deleteGroup("superuser");
        AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        redisPage.addGroup("superuser", Collections.singletonList(accessGroup.getPrefixName()));
    }

    @Test
    @TmsLink("1454016")
    @Order(26)
    @DisplayName("UI RedisAstra. Изменение группы доступа")
    void updateGroup() {
        AccessGroup accessGroupOne = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
        AccessGroup accessGroupTwo = AccessGroup.builder().name(new Generex("win[a-z]{5,10}").random()).projectName(product.getProjectId()).build().createObject();
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.runActionWithCheckCost(CompareType.EQUALS, () -> redisPage.updateGroup("superuser",
                Arrays.asList(accessGroupOne.getPrefixName(), accessGroupTwo.getPrefixName())));
    }

    @Test
    @Order(27)
    @TmsLink("1296747")
    @DisplayName("UI Windows. Мониторинг ОС")
    void monitoringOs() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.checkMonitoringOs();
    }

    @Test
    @Order(100)
    @TmsLink("796989")
    @DisplayName("UI RedisAstra. Удаление продукта")
    void delete() {
        RedisAstraPage redisPage = new RedisAstraPage(product);
        redisPage.delete();
    }

 }
