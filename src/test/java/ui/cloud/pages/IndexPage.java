package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.productCatalog.GraphsListPage;
import ui.cloud.pages.productCatalog.orgDirectionsPages.OrgDirectionsListPage;

import static com.codeborne.selenide.Selenide.$x;
@Getter
public class IndexPage {
    private final SelenideElement orderMoreBtn = $x("//button[contains(., 'Заказать еще')]");
    private final SelenideElement btnProducts = Selenide.$x("//div[not(@hidden)]/a[@href='/vm/orders' and text()='Продукты']");
    private final SelenideElement graphs = $x("//*[@href='/meccano/graphs']");
    private final SelenideElement directions = $x("//*[@href='/meccano/org_direction']");

    public NewOrderPage clickOrderMore(){
        orderMoreBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled);
        orderMoreBtn.hover().click();
        return new NewOrderPage();
    }

   public GraphsListPage goToGraphsPage() {
        graphs.click();
        return new GraphsListPage();
    }

    @Step("Переход на страницу Конструктор.Направления")
    public OrgDirectionsListPage goToOrgDirectionsPage() {
        directions.scrollTo();
        directions.click();
        return new OrgDirectionsListPage();
    }

}