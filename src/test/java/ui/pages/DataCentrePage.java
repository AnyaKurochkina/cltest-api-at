package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class DataCentrePage implements Loadable {

    private final SelenideElement titleOfDataCentrePage = $x("//div[@type='large'][@class='sc-heudyb hALtSu']");
    private final SelenideElement actionsBtn = $x("//button[@id='actions-menu-button']//div[text()='Действия']");
    private final SelenideElement deleteDataCentreActionBtn = $x("//*[text()='Удалить']");
    private final SelenideElement idForDelete = $(By.cssSelector(".MuiTypography-colorTextSecondary>b"));
    private final SelenideElement idForDeleteInput = $x("//input[@name='id']");
    private final SelenideElement confirmDeleteBtn = $x("//span[text()='Удалить']");
    private final SelenideElement deletionProtect = $x("//li[text()='Защита от удаления']");
    private final SelenideElement backToVmWareOrganization = $x("//a[text()='VMware организация']");
    private final SelenideElement checkBoxDeletionProtect = $x("//*[text()='Включить защиту от удаления']");
    private final SelenideElement acceptCheckBoxDeletionProtect = $x("//*[text()='Подтвердить']");
    private final SelenideElement actionMenuBtn = $x("//button[@id='actions-menu-button']");
    private final SelenideElement dataCentreInfoTable = $x("//tr[@class='MuiTableRow-root MuiTableRow-hover']");
    private final String dataCentreStatus = "./td[4]/div";
    private final String deletionProtectStatus = "./td[3]";

    public DataCentrePage(String dcName) {
        checkPage(dcName);
    }

    @Override
    public void checkPage() {
        titleOfDataCentrePage.shouldBe(Condition.visible);
    }

    public void checkPage(String dcName) {
        titleOfDataCentrePage.shouldBe(Condition.visible).shouldHave(Condition.text(dcName));
    }

    public void removeDeletionProtection() {
        actionMenuBtn.shouldBe(Condition.enabled).click();
        deletionProtect.shouldBe(Condition.enabled).click();
        checkBoxDeletionProtect.shouldBe(Condition.enabled).click();
        acceptCheckBoxDeletionProtect.shouldBe(Condition.enabled).click();
        }


    public void deleteDataCentre(){
        actionsBtn.shouldBe(Condition.enabled).click();
        deleteDataCentreActionBtn.shouldBe(Condition.enabled).click();
        idForDeleteInput.shouldBe(Condition.visible).val(idForDelete.getText());
        confirmDeleteBtn.shouldBe(Condition.enabled).click();
    }

    public void backToVmWareOrganization(){
        backToVmWareOrganization.shouldBe(Condition.enabled).click();
    }
}
