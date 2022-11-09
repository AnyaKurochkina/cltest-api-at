package ui.t1.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {

    private final SelenideElement cloudDirectorBtn = $x("//a[@href='/vcloud/orgs']");
    private final SelenideElement orgStructureBtn = $x("//*[text()='Орг. структура']");
    @Getter
    private final SelenideElement notificationBar = $(By.xpath("//div[@id='notistack-snackbar']"));

    public MainPage() {
        checkPage();
    }

    public void goToListOfOrganizations(){
        cloudDirectorBtn.shouldBe(Condition.enabled).click();
    }

    public void checkPage() {
        orgStructureBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled);
    }

    public void goToOrgStructure(){
        orgStructureBtn.shouldBe(Condition.enabled).click();
    }
}
