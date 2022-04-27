package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.Selenide.*;

public class DataCenterPage implements Loadable {

    private final SelenideElement titleOfDataCenterPage = $x("//div[@type='large']");
    private final SelenideElement actionsBtn = $x("//button[@id='actions-menu-button']//div[text()='Действия']");
    private final SelenideElement deleteDataCenterActionBtn = $x("//*[text()='Удалить']");
    private final SelenideElement idForDelete = $(By.cssSelector(".MuiTypography-colorTextSecondary>b"));
    private final SelenideElement idForDeleteInput = $x("//input[@name='id']");
    private final SelenideElement confirmDeleteBtn = $x("//span[text()='Удалить']");
    private final SelenideElement deletionProtect = $x("//li[text()='Защита от удаления']");
    private final SelenideElement backToVmWareOrganization = $x("//a[text()='VMware организация']");
    private final SelenideElement checkBoxDeletionProtect = $x("//*[text()='Включить защиту от удаления']");
    private final SelenideElement acceptCheckBoxDeletionProtect = $x("//*[text()='Подтвердить']");
    private final SelenideElement actionMenuBtn = $x("//button[@id='actions-menu-button']");
    private final SelenideElement publicIpAddress = $x("//*[text()='Публичные IP-адреса']");
    private final SelenideElement dataCenterInfoTable = $x("//tr[@class='MuiTableRow-root MuiTableRow-hover']");
    private final String dataCenterStatus = "./td[4]/div";
    private final String deletionProtectStatus = "./td[3]";

    public DataCenterPage(String nameOfDataCentre) {
        checkPage(nameOfDataCentre);
    }

    @Override
    public void checkPage() {
        titleOfDataCenterPage.shouldBe(Condition.visible);
    }

    public void checkPage(String dcName) {
        titleOfDataCenterPage.shouldBe(Condition.visible).shouldHave(Condition.text(dcName));
    }

    public void removeDeletionProtection() {
        actionMenuBtn.shouldBe(Condition.enabled).click();
        deletionProtect.shouldBe(Condition.enabled).click();
        checkBoxDeletionProtect.shouldBe(Condition.enabled).click();
        acceptCheckBoxDeletionProtect.shouldBe(Condition.enabled).click();
        }


    public void deleteDataCenter(){
        actionsBtn.shouldBe(Condition.enabled).click();
        deleteDataCenterActionBtn.shouldBe(Condition.enabled).click();
        idForDeleteInput.shouldBe(Condition.visible).val(idForDelete.getText());
        confirmDeleteBtn.shouldBe(Condition.enabled).click();
    }

    public void reserveIpAddress(){
        publicIpAddress.scrollIntoView(true).shouldBe(Condition.visible);

    }

    public void backToVmWareOrganization(){
        backToVmWareOrganization.shouldBe(Condition.enabled).click();
    }
}
