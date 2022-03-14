package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.Selenide.$;

public class MainPage implements Loadable {

    private final SelenideElement cloudDirectorBtn = $(By.xpath("//a[@href='/vcloud/orgs']"));
    private final SelenideElement orgStructureBtn = $(By.xpath("//*[text()='Орг. структура']"));

    public MainPage() {
        checkPage();
    }

    public void goToListOfOrganizations(){
        cloudDirectorBtn.shouldBe(Condition.enabled).click();
    }

    @Override
    public void checkPage() {
        orgStructureBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled);
    }
}
