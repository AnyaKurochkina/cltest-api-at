package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.elements.Button;
import ui.elements.Input;

import static com.codeborne.selenide.Selenide.$;

public class DataCentreCreatePage {
    SelenideElement pageTitle = $(By.xpath("//*[text()='Создание виртуального дата-центра VMware']"));
    Button order;

    public DataCentreCreatePage() {
        pageTitle.shouldBe(Condition.visible);
        order = Button.byText("Заказать");
    }

    public DataCentreCreatePage setDataCentreName(String name) {
        Input.byLabel("Имя виртуального дата-центра").setValue(name);
        return this;
    }

    public VMwareOrganizationPage orderDataCentre() {
        order.click();
        return new VMwareOrganizationPage();
    }
}
