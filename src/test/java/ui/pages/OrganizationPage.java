package ui.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import core.enums.DataCentreStatus;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.fail;

public class OrganizationPage implements Loadable {

    private final SelenideElement titleOfOneOrganization = $x("//div[@type='large']");
    private final SelenideElement createVirtualDataCentreBtn = $x("//*[text()='Создать Виртуальный дата-центр']");
    private final SelenideElement circleProgressLoader = $x("//circle[@class='MuiCircularProgress-circle MuiCircularProgress-circleIndeterminate']");
    private final SelenideElement deleteOrgBtn = $x("//div[text()='Удалить']");
    private final SelenideElement idForDeleteInput = $x("//input[@name='id']");
    private final SelenideElement idForDelete = $(By.cssSelector(".MuiTypography-colorTextSecondary>b"));
    private final SelenideElement confirmDeleteBtn = $x("//span[text()='Удалить']");

    //Поля Таблицы allDataCentersTable, первого попавшегося дата центра
    SelenideElement firstNameOfDataCentreColumn = $x("//tr[@class='MuiTableRow-root MuiTableRow-hover']//td[1]/div/span");
    SelenideElement firstDateOfCreationColumn = $x("//tr[@class='MuiTableRow-root MuiTableRow-hover']//td[2]/div/div");
    SelenideElement firstModelAllocationResourcesColumn = $x("//tr[@class='MuiTableRow-root MuiTableRow-hover']//td[3]");
    SelenideElement firstStatusColumn = $x("//tr[@class='MuiTableRow-root MuiTableRow-hover']//td[4]/div");

    private final ElementsCollection allDataCentersTable = $$x("//tr[@class='MuiTableRow-root MuiTableRow-hover']");
    //Составные локаторы, которые являются частью таблицы allDataCenters
    By nameOfDataCentreColumn = By.xpath(".//td[1]/div/span");
    By dateOfCreationColumn = By.xpath(".//td[2]/div/div");
    By modelAllocationResourcesColumn = By.xpath(".//td[3]");
    By statusColumn = By.xpath(".//td[4]/div");

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

    public void stepInDataCenter(String nameOfDataCentre) {
        allDataCentersTable.forEach(e -> e.$(nameOfDataCentreColumn).shouldBe(Condition.visible));
        for (SelenideElement element : allDataCentersTable) {
            if (element.$(nameOfDataCentreColumn).shouldHave(Condition.text(nameOfDataCentre)).exists()) {
                element.$(nameOfDataCentreColumn).shouldBe(Condition.enabled).click();
                break;
            }
        }
    }

    public void checkStatusTransitionOfDataCenter(String nameOfDataCentre, DataCentreStatus startStatus, DataCentreStatus endStatus) {
        allDataCentersTable.shouldHave(CollectionCondition.sizeGreaterThan(0));
        for (SelenideElement e : allDataCentersTable) {
            if (e.$(nameOfDataCentreColumn).shouldBe(Condition.visible)
                    .shouldHave(Condition.text(nameOfDataCentre)).exists()) {
                e.$(statusColumn)
                        .shouldHave(Condition.attribute("title", startStatus.getStatus()))
                        .shouldNotHave(Condition.attribute("title", startStatus.getStatus()),
                                Duration.ofMillis(480000));
                e.$(statusColumn).shouldHave(Condition.attribute("title", endStatus.getStatus()));
            }
        }
    }
}
