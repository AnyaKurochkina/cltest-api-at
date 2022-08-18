package ui.cloud.tests.orders;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.PostgreSQL;
import models.orderService.products.PostgreSQLAstra;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Graph;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.time.Duration;

@ExtendWith(InterceptTestExtension.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_postgresql_astra")})
@Log4j2
public class UiPostgreSQLAstraLinuxTest extends Tests {

    PostgreSQL product;

    public UiPostgreSQLAstraLinuxTest() {
        if (Configure.ENV.equals("prod"))
            //product = PostgreSQL.builder().productName("PostgreSQL (Astra Linux)").env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            product = PostgreSQL.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/6c3749e1-605a-4724-8335-9b19069bd5a9/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = PostgreSQL.builder().env("DSO").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        //Configuration.browserSize = "1366x768";
        new LoginPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }
//
//    @Test
//    @TmsLink("16306")
//    @Order(1)
//    @DisplayName("UI PostgreSQLAstra. Заказ")
//    void orderPostgreSQL() {
//        double preBillingProductPrice;
//        try {
//            new IndexPage()
//                    .clickOrderMore()
//                    .selectProduct(product.getProductName());
//            PostgreSQLAstraOrderPage orderPage = new PostgreSQLAstraOrderPage();
//            orderPage.getOsVersion().select(product.getOsVersion());
//            orderPage.getSegment().selectByValue(product.getSegment());
//            orderPage.getPlatform().selectByValue(product.getPlatform());
//            orderPage.getConfigure().selectByValue(Product.getFlavor(product.getMinFlavor()));
//            AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
//            orderPage.getGroup().select(accessGroup.getPrefixName());
//            orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
//            preBillingProductPrice = IProductPage.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
//            orderPage.orderClick();
//            new OrdersPage()
//                    .getRowElementByColumnValue("Продукт",
//                            orderPage.getLabelValue())
//                    .hover()
//                    .click();
//            PostgreSqlAstraPage pSqlPages = new PostgreSqlAstraPage(product);
//            pSqlPages.waitChangeStatus(Duration.ofMinutes(25));
//            pSqlPages.checkLastAction("Развертывание");
//        } catch (Throwable e) {
//            product.setError(e.toString());
//            throw e;
//        }
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        Assertions.assertEquals(preBillingProductPrice, pSqlPage.getCostOrder(), 0.01);
//    }
//
//    @Test
//    @TmsLink("1076804")
//    @Order(2)
//    @DisplayName("UI PostgreSQLAstra. Проверка заголовка столбцов в Истории действий.")
//    void checkHeaderHistoryTable() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click(ClickOptions.usingJavaScript());
//        pSqlPage.getBtnHistory().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
//        pSqlPage.checkHeadersHistory();
//    }
//
//    @Test
//    @TmsLink("1076809")
//    @Order(3)
//    @DisplayName("UI PostgreSQLAstra. Проверка элемента 'Схема выполнения'")
//    void checkHistoryGraphScheme() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.getBtnHistory().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
//        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
//        new Graph().checkGraph();
//    }
//
//    @Test
//    @TmsLink("2023")
//    @Order(4)
//    @DisplayName("UI PostgreSQLAstra. Проверка 'Защита от удаления'")
//    void checkProtectOrder() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.switchProtectOrder("Защита от удаления включена");
//        pSqlPage.runActionWithParameters("Приложение", "Удалить рекурсивно", "Удалить", () ->
//        {
//            Dialog dlgActions = new Dialog("Удаление");
//            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
//        }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
//        new Alert().checkColor(Alert.Color.RED).checkText("Заказ защищен от удаления").close();
//        Selenide.refresh();
//        pSqlPage.switchProtectOrder("Защита от удаления выключена");
//    }
//
//    @Test
//    @Order(5)
//    @TmsLink("14506")
//    @DisplayName("UI PostgreSQLAstra. Перезагрузить по питанию")
//    void restart() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::restart);
//    }

//    @Test
//    @Order(6)
//    @TmsLink("109449")
//    @DisplayName("UI PostgreSQLAstra. Расширить диск")
//    void expandDisk() {
//      //  Assumptions.assumeFalse("OpenStack".equals(product.getPlatform()), "Тест отключен для платформы OpenStack");
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.expandDisk("Дополнительные точки монтирования", "20"));
//    }

//    @Test
//    @Order(7)
//    @TmsLink("647428")
//    @DisplayName("UI PostgreSQLAstra. Изменить конфигурацию")
//    void changeConfiguration() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::changeConfiguration);
//    }

//    @Test
//    @Order(8)
//    @TmsLink("")
//    @DisplayName("UI PostgreSQLAstra. Создание БД")
//    void createDb() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.createDb();
//    }
//
//    @Test
//    @Order(9)
//    @TmsLink("")
//    @DisplayName("UI PostgreSQLAstra. Назначить предел подключений")
//    void setLimitConnectDb() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.setLimitConnectDb();
//    }
//
//    @Test
//    @Order(10)
//    @TmsLink("")
//    @DisplayName("UI PostgreSQLAstra. Убрать предел подключений")
//    void removeLimitConnectDb() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.removeLimitConnectDb("ru_RU.UTF-8");
//    }

//    @Test
//    @Order(11)
//    @TmsLink("")
//    @DisplayName("UI PostgreSQLAstra. Сбросить пароль владельца БД")
//    void resetPasswordDb() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.resetPasswordDb();
//    }

//    @Test
//    @Order(12)
//    @TmsLink("")
//    @DisplayName("UI PostgreSQLAstra. Добавить пользователя")
//    void createUserDb() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.createUserDb();
//    }

    @Test
    @Order(13)
    @TmsLink("")
    @DisplayName("UI PostgreSQLAstra. Сбросить пароль пользователя БД")
    void resetPasswordUserDb() {
        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
        pSqlPage.resetPasswordUserDb();
    }
//
//    @Test
//    @Order(14)
//    @TmsLink("")
//    @DisplayName("UI PostgreSQLAstra. Удалить пользователя БД")
//    void deletePasswordUserDb() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.deletePasswordUserDb();
//    }






//
//    @Test
//    @Order(11)
//    @TmsLink("")
//    @DisplayName("UI PostgreSQLAstra. Удаление БД")
//    void removeDb() {
//        PostgreSqlAstraPage pSqlPage = new PostgreSqlAstraPage(product);
//        pSqlPage.removeDb("ru_RU.UTF-8");
//    }


}
