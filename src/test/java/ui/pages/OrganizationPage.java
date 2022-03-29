package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.Selenide.*;

public class OrganizationPage implements Loadable {

    private final SelenideElement titleOfOneOrganization = $x("//div[@type='large']");
    private final SelenideElement createVirtualDataCentreBtn = $x("//*[text()='Создать Виртуальный дата-центр']");
    private ElementsCollection allDataCentres = $$x("//tr[@class='MuiTableRow-root MuiTableRow-hover']");
    String nameOfDataCentreColumn = ".//td[1]/div/span";
    String dateOfCreationColumn = ".//td[2]/div/div";
    String modelAllocationResourcesColumn = ".//td[3]";
    String statusColumn = ".//td[4]/div";

    public OrganizationPage() {
        checkPage();
    }

    public void createVirtualDataCentre() {
        createVirtualDataCentreBtn.shouldBe(Condition.enabled).click();
    }

    @Override
    public void checkPage() {
        titleOfOneOrganization.shouldBe(Condition.visible);
    }

    public void checkPage(String name) {
        titleOfOneOrganization.shouldHave(Condition.text(name));
    }

    public void stepInDataCentre(String nameOfDataCentre) {
        //asDynamicIterable()
        allDataCentres.stream().filter(element -> element.$x(nameOfDataCentreColumn).getText().equals("paasl"))
                .forEach(e -> e.$x(nameOfDataCentreColumn).click());
    }

    public void checkStatusOfDataCentre(String nameOfDataCentre) {
        allDataCentres.stream()
                .filter(element -> element.$x(nameOfDataCentreColumn)
                        .shouldBe(Condition.visible)
                        .getText().equals(nameOfDataCentre))
                .forEach(e -> e.$x(statusColumn)
                        .shouldHave(Condition.attribute("title", "В порядке")));
    }


}
