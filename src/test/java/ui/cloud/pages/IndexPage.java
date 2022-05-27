package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ui.cloud.pages.productCatalog.GraphsListPage;
import ui.cloud.pages.productCatalog.orgDirectionsPages.OrgDirectionsListPage;

import static com.codeborne.selenide.Selenide.$x;

public class IndexPage {
    SelenideElement orderMoreBtn = $x("//button[contains(., 'Заказать еще')]");

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

    public OrgDirectionsListPage goToOrgDirectionsPage() {
        directions.scrollTo();
        directions.click();
        return new OrgDirectionsListPage();
    }

}