package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.Selenide.$;

public class OrganizationPage implements Loadable {

    private final SelenideElement titleOfOneOrganization = $(By.xpath("//div[@type='large']"));
    private final SelenideElement createVirtualDataCentreBtn = $(By.xpath("//*[text()='Создать Виртуальный дата-центр']"));

    public OrganizationPage() {
        checkPage();
    }

    public void createVirtualDataCentre(){
        createVirtualDataCentreBtn.shouldBe(Condition.enabled).click();
    }

    @Override
    public void checkPage() {
        titleOfOneOrganization.shouldBe(Condition.visible);
    }

    public void checkPage(String name) {
        titleOfOneOrganization.shouldHave(Condition.text(name));
    }


}
