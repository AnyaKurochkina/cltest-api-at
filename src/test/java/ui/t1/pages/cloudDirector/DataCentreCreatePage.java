package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.elements.Input;

import static com.codeborne.selenide.Selenide.$;
import static ui.cloud.pages.EntitiesUtils.clickOrder;

public class DataCentreCreatePage {
    SelenideElement pageTitle = $(By.xpath("//*[text()='Создание виртуального дата-центра VMware']"));

    public DataCentreCreatePage() {
        pageTitle.shouldBe(Condition.visible);
    }

    public DataCentreCreatePage setDataCentreName(String name) {
        Input.byLabel("Имя виртуального дата-центра").setValue(name);
        return this;
    }

    public VMwareOrganizationPage orderDataCentre() {
        clickOrder();
        return new VMwareOrganizationPage();
    }

    public void orderDataCentreWithSameName() {
        clickOrder("Имя виртуального дата-центра не уникально в организации");
    }
}
