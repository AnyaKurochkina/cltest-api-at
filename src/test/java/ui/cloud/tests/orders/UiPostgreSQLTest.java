package ui.cloud.tests.orders;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import models.orderService.products.PostgreSQL;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.time.Duration;

@ExtendWith(InterceptTestExtension.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_postgresql")})
@Log4j2
public class UiPostgreSQLTest extends Tests {

    PostgreSQL product;

    public UiPostgreSQLTest() {
        if (Configure.ENV.equals("prod"))
            //  product = PostgreSQL.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();
            product = PostgreSQL.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").link("https://prod-portal-front.cloud.vtb.ru/db/orders/7b6a9bab-bdcc-4c50-8b5c-8cc9eb5de47f/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = PostgreSQL.builder().env("DSO").platform("vSphere").segment("dev-srv-app").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        //Configuration.browserSize = "1366x768";
        new LoginPage(product.getProjectId())
                .signIn();
    }

//    @Test
//    @TmsLink("16306")
//    @Order(1)
//    @DisplayName("UI PostgreSQL. Заказ")
//    void orderPostgreSQL() {
//        double preBillingProductPrice;
//        try {
//            new IndexPage()
//                    .clickOrderMore()
//                    .selectProduct(product.getProductName());
//            WindowsOrderPage orderPage = new WindowsOrderPage();
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
//            PostgreSqlPage pSqlPages = new PostgreSqlPage(product);
//            pSqlPages.waitChangeStatus(Duration.ofMinutes(25));
//            pSqlPages.checkLastAction("Развертывание");
//        } catch (Throwable e) {
//            product.setError(e.toString());
//            throw e;
//        }
//        PostgreSqlPage pSqlPage = new PostgreSqlPage(product);
//        Assertions.assertEquals(preBillingProductPrice, pSqlPage.getCostOrder(), 0.01);
//    }

//    @Test
//    @TmsLink("1076804")
//    @Order(2)
//    @DisplayName("UI PostgreSQL. Проверка заголовка столбцов в Истории действий.")
//    void checkHeaderHistoryTable() {
//        PostgreSqlPage pSqlPage = new PostgreSqlPage(product);
//        pSqlPage.getBtnGeneralInfo().shouldBe(Condition.enabled).click(ClickOptions.usingJavaScript());
//        pSqlPage.getBtnHistory().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
//        pSqlPage.checkHeadersHistory();
//    }

//    @Test
//    @TmsLink("1076809")
//    @Order(3)
//    @DisplayName("UI PostgreSQL. Проверка элемента 'Схема выполнения'")
//    void checkHistoryGraphScheme() {
//        PostgreSqlPage pSqlPage = new PostgreSqlPage(product);
//        pSqlPage.getBtnHistory().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
//        pSqlPage.getHistoryTable().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
//        new EntitiesUtils().checkGraphScheme(null);
//    }

//    @Test
//    @TmsLink("2023")
//    @Order(4)
//    @DisplayName("UI PostgreSQL. Проверка 'Защита от удаления'")
//    void checkProtectOrder() {
//        PostgreSqlPage pSqlPage = new PostgreSqlPage(product);
//        pSqlPage.switchProtectOrder("Защита от удаления включена");
//        pSqlPage.runActionWithParameters("Виртуальная машина", "Удалить", "Удалить", () ->
//        {
//            Dialog dlgActions = new Dialog("Удаление");
//            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
//        }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
//        new Alert().checkColor(Alert.Color.RED).checkText("Заказ защищен от удаления").close();
//        Selenide.refresh();
//        pSqlPage.switchProtectOrder("Защита от удаления выключена");
//    }

//    @Test
//    @Order(5)
//    @TmsLink("14506")
//    @DisplayName("UI PostgreSQL. Перезагрузить по питанию")
//    void restart() {
//        PostgreSqlPage pSqlPage = new PostgreSqlPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.EQUALS, pSqlPage::restart);
//    }

    @Test
    @Order(6)
    @TmsLink("109449")
    @DisplayName("UI PostgreSQL. Расширить диск")
    void expandDisk() {
      //  Assumptions.assumeFalse("OpenStack".equals(product.getPlatform()), "Тест отключен для платформы OpenStack");
        PostgreSqlPage pSqlPage = new PostgreSqlPage(product);
        pSqlPage.runActionWithCheckCost(CompareType.MORE, () -> pSqlPage.expandDisk("Дополнительные точки монтирования", "20"));
    }

//    @Test
//    @Order(7)
//    @TmsLink("647428")
//    @DisplayName("UI PostgreSQL. Изменить конфигурацию")
//    void changeConfiguration() {
//        PostgreSqlPage pSqlPage = new PostgreSqlPage(product);
//        pSqlPage.runActionWithCheckCost(CompareType.MORE, pSqlPage::changeConfiguration);
//
//    }


}
