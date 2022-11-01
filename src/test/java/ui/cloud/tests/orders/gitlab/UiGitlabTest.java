package ui.cloud.tests.orders.gitlab;

import com.mifmif.common.regex.Generex;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.GitLab;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.*;
import ui.uiExtesions.ConfigExtension;
import ui.uiExtesions.InterceptTestExtension;

import java.time.Duration;

@ExtendWith(InterceptTestExtension.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("ui_gitlab")})
@Log4j2
public class UiGitlabTest extends Tests {
    GitLab product;

    //TODO: пока так :)
    public UiGitlabTest() {
        if (Configure.ENV.equals("prod"))
            product = GitLab.builder().env("DEV").platform("OpenStack").build();
            //product = GitLab.builder().env("DEV").platform("OpenStack").link("https://prod-portal-front.cloud.vtb.ru/devops_tools/orders/7f67547f-6405-462a-a645-f21fb1c05187/main?context=proj-ln4zg69jek&type=project&org=vtb").build();
        else
            product = GitLab.builder().env("DEV").platform("vSphere").build();
        product.init();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        //Configuration.browserSize = "1366x768";
        new LoginPage(product.getProjectId())
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
