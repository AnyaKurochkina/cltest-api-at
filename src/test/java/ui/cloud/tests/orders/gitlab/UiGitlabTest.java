package ui.cloud.tests.orders.gitlab;

import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.GitLab;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import ui.cloud.pages.*;
import ui.cloud.pages.orders.GitlabOrderPage;
import ui.cloud.pages.orders.GitlabPage;
import ui.cloud.pages.orders.OrdersPage;
import ui.extesions.UiProductTest;

import java.time.Duration;

@Tags({@Tag("ui_gitlab")})
@Log4j2
public class UiGitlabTest extends UiProductTest {
    GitLab product;

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginCloudPage(product.getProjectId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLink("874526")
    @Order(1)
    @DisplayName("UI Gitlab. Заказ")
    void orderGitLab() {
        double preBillingProductPrice;
        String randomName;
        try {
            new IndexPage()
                    .clickOrderMore()
                    .selectProduct(product.getProductName());
            GitlabOrderPage orderPage = new GitlabOrderPage();
            randomName = new Generex("vtb-gitlab-[a-z]{5,15}").random();
            orderPage.getProjectName().setValue(randomName);
            orderPage.getParticipant().setValue("vtb4050213");
            orderPage.getParticipant2().selectById("vtb4050213-vshipunov@vtb.ru");
            //orderPage.getLoadOrderPricePerDay().shouldBe(Condition.visible);
            //preBillingProductPrice = IProductPage.getPreBillingCostAction(orderPage.getLoadOrderPricePerDay());
            orderPage.orderClick();
            new OrdersPage()
                    .getRowElementByColumnValue("Продукт",
                            randomName)
                    .hover()
                    .click();
            GitlabPage gitlabPages = new GitlabPage(product);
            gitlabPages.waitChangeStatus(Duration.ofMinutes(25));
            gitlabPages.checkLastAction("Развертывание");
        } catch (Throwable e) {
            product.setError(e.toString());
            throw e;
        }
        GitlabPage gitlabPage = new GitlabPage(product);
        //Assertions.assertEquals(preBillingProductPrice, gitlabPage.getCostOrder(), 0.01);
    }

    @Test
    @Order(100)
    @TmsLink("874669")
    @DisplayName("UI Gitlab. Удалить")
    void deleteGitlab() {
        GitlabPage gitlabPage = new GitlabPage(product);
        gitlabPage.delete();
    }

}
