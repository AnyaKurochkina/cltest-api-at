package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class MainPage {

    private final SelenideElement cloudDirectorBtn = $(By.xpath("//a[@href='/vcloud/orgs']"));

    public void goToListOfOrganizations(){
        cloudDirectorBtn.shouldBe(Condition.enabled).click();
    }
}
