package ui.cloud.tests.orders.scillaDB;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.ScyllaDb;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import api.Tests;
import ui.cloud.pages.*;
import ui.elements.Graph;
import ui.elements.Table;
import ui.extesions.ConfigExtension;
import ui.extesions.InterceptTestExtension;

import java.time.Duration;

@ExtendWith(ConfigExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_scylla_db")})
@Log4j2
public class UiScyllaDbTest extends Tests {

    ScyllaDb product;
    String nameDb = "at_db";
    String shortNameUserDB = "at_user";

    public UiScyllaDbTest() {
        if (Configure.ENV.equals("prod") || Configure.ENV.equals("blue"))
            product = ScyllaDb.builder().env("DEV").productName("ScyllaDB").platform("OpenStack").segment("dev-srv-app").build();
        //product = ScyllaDb.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/01828278-e513-4685-8cdd-f0c87840ff62/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = ScyllaDb.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();

    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("464084")
    @Order(1)
    @DisplayName("UI ScyllaDB. Заказ")
    void orderScyllaDB() {
        double preBillingProductPrice;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            ScyllaDbOrderPage orderPage = new ScyllaDbOrderPage();
            orderPage.getOsVersion().select(product.getOsVersion());
            orderPage.getSegment().selectByValue(product.getSegment());
            orderPage.getPlatform().selectByValue(product.getPlatform());
            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
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
            ScyllaPage scyllaPages = new ScyllaPage(product);
            scyllaPages.waitChangeStatus(Duration.ofMinutes(25));
            scyllaPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        ScyllaPage scyllaPage = new ScyllaPage(product);
        Assertions.assertEquals(preBillingProductPrice, scyllaPage.getCostOrder(), 0.01);
    }


    @Test
    @TmsLink("1236730")
    @Order(2)
    @DisplayName("UI ScyllaDB. Проверка полей заказа")
    void checkHeaderHistoryTable() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click();
        scyllaPage.checkHeadersHistory();
        scyllaPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().checkGraph();
    }

    @Test
    @Order(5)
    @TmsLink("464235")
    @DisplayName("UI ScyllaDB. Перезагрузить по питанию")
    void restart() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, scyllaPage::restart);
    }

    @Test
    @Order(9)
    @TmsLink("464279")
    @DisplayName("UI ScyllaDB. Расширить диск")
    void expandDisk() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.MORE, () -> scyllaPage.enlargeDisk("/app/scylla/data", "20", new Table("Размер, Гб").getRowByIndex(0)));
    }

    @Test
    @Order(11)
    @TmsLink("1190981")
    @DisplayName("UI ScyllaDB. Проверить конфигурацию")
    void vmActCheckConfig() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, scyllaPage::checkConfiguration);
    }

    @Test
    @Order(12)
    @TmsLink("464341")
    @DisplayName("UI ScyllaDB. Создание БД")
    void createDb() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
    }

    @Test
    @Order(16)
    @TmsLink("629611")
    @DisplayName("UI ScyllaDB. Добавить пользователя")
    void addUserDb() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
    }

    @Test
    @Order(17)
    @TmsLink("629634")
    @DisplayName("UI ScyllaDB. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.resetPasswordUserDb(shortNameUserDB));
    }

    @Test
    @Order(18)
    @TmsLink("629635")
    @DisplayName("UI ScyllaDB. Удалить пользователя БД")
    void
    deleteUserDb() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.addUserDb(shortNameUserDB));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.deleteUserDb(shortNameUserDB));
    }

    @Test
    @Order(19)
    @TmsLink("629649")
    @DisplayName("UI ScyllaDB. Удаление БД")
    void removeDb() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.createDb(nameDb));
        scyllaPage.runActionWithCheckCost(CompareType.EQUALS, () -> scyllaPage.removeDb(nameDb));
    }

    @Test
    @Order(20)
    @TmsLinks({@TmsLink("464218"), @TmsLink("464284")})
    @Disabled
    @DisplayName("UI ScyllaDB. Выключить принудительно / Включить")
    void stopHard() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.LESS, scyllaPage::stopHard);
        scyllaPage.runActionWithCheckCost(CompareType.MORE, scyllaPage::start);
    }

    @Test
    @Order(21)
    @TmsLink("464202")
    @Disabled
    @DisplayName("UI ScyllaDB. Выключить")
    void stopSoft() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.runActionWithCheckCost(CompareType.LESS, scyllaPage::stopSoft);
    }

    @Test
    @Order(100)
    @TmsLink("464240")
    @DisplayName("UI ScyllaDB. Удаление продукта")
    void delete() {
        ScyllaPage scyllaPage = new ScyllaPage(product);
        scyllaPage.delete();
    }

}
