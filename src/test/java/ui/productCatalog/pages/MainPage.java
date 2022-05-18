package ui.productCatalog.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {

    private final SelenideElement graphs = $x("//*[@href='/meccano/graphs']");
    private final SelenideElement directions = $x("//*[@href='/meccano/org_directions']");

    public GraphsListPage goToGraphsPage() {
        graphs.click();
        return new GraphsListPage();
    }

    public OrgDirectionsListPage goToOrgDirectionsPage() {
        directions.click();
        return new OrgDirectionsListPage();
    }
}
