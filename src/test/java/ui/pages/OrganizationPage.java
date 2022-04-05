package ui.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.fail;

public class OrganizationPage implements Loadable {

    private final SelenideElement titleOfOneOrganization = $x("//div[@type='large']");
    private final SelenideElement createVirtualDataCentreBtn = $x("//*[text()='Создать Виртуальный дата-центр']");
    private final SelenideElement circleProgressLoader = $x("//circle[@class='MuiCircularProgress-circle MuiCircularProgress-circleIndeterminate']");
    private ElementsCollection allDataCentres = $$x("//tr[@class='MuiTableRow-root MuiTableRow-hover']");
    private final SelenideElement deleteOrgBtn = $x("//div[text()='Удалить']");
    private final SelenideElement idForDeleteInput = $x("//input[@name='id']");
    private final SelenideElement idForDelete = $(By.cssSelector(".MuiTypography-colorTextSecondary>b"));
    private final SelenideElement confirmDeleteBtn = $x("//span[text()='Удалить']");
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
        titleOfOneOrganization.shouldBe(Condition.visible).shouldHave(Condition.text(name));
    }

    public void deleteOrganization() {
        deleteOrgBtn.shouldBe(Condition.enabled).click();
        idForDeleteInput.shouldBe(Condition.visible).val(idForDelete.getText());
        confirmDeleteBtn.shouldBe(Condition.enabled).click();
    }

    public void checkLoader() {
        circleProgressLoader.shouldNot(Condition.visible);
        circleProgressLoader.shouldNot(Condition.visible);
    }

    public void stepInDataCentre(String nameOfDataCentre) {
        allDataCentres.forEach(e -> e.$x(nameOfDataCentreColumn).shouldBe(Condition.visible));
        for (SelenideElement element : allDataCentres) {
            if (element.$x(nameOfDataCentreColumn).shouldHave(Condition.text(nameOfDataCentre)).exists()) {
                element.$x(nameOfDataCentreColumn).shouldBe(Condition.enabled).click();
                break;
            }
        }
    }

    public void checkStatusOfDataCentre(String nameOfDataCentre) {
        allDataCentres.shouldHave(CollectionCondition.sizeGreaterThan(0));
        for (SelenideElement e : allDataCentres) {
            if (e.$x(nameOfDataCentreColumn).shouldBe(Condition.visible)
                    .shouldHave(Condition.text(nameOfDataCentre)).exists()) {
                e.$x(statusColumn).shouldBe(Condition.visible)
                        .shouldNotHave(Condition.attribute("title", "Разворачивается"),
                                Duration.ofMillis(480000))
                        .shouldHave(Condition.attribute("title", "В порядке"));
            }
        }
    }

    public void checkDeleteStatusOfDataCentre(String nameOfDataCentre) {
        allDataCentres.shouldHave(CollectionCondition.sizeGreaterThan(0));
        for (SelenideElement e : allDataCentres) {
            if (e.$x(nameOfDataCentreColumn).shouldBe(Condition.visible)
                    .shouldHave(Condition.text(nameOfDataCentre)).exists()) {
                e.$x(statusColumn)
                        .shouldHave(Condition.attribute("title", "Удаляется"))
                        .shouldNotHave(Condition.attribute("title", "Удаляется"), Duration.ofMillis(50000));
                e.$x(statusColumn).shouldHave(Condition.attribute("title", "Удалено"));
            }
        }
    }
}
