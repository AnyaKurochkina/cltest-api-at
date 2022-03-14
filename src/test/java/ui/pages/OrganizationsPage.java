package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import ui.Loadable;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.webdriver;

public class OrganizationsPage implements Loadable {

    private final SelenideElement plusBtn = $(By.xpath("//div[@data-testid='vdc-org-list-add-button']"));
    private final SelenideElement orgCreationFrame = $(By.xpath("//div[@role='dialog']"));
    private final SelenideElement orgNameInput = $(By.xpath("//input[@name='name']"));
    private final SelenideElement createOrgBtn = $(By.cssSelector(".MuiDialogActions-spacing > button:nth-child(2)"));
    private final SelenideElement oneOrganization = $(By.cssSelector(".MuiTableRow-hover")).shouldBe(Condition.visible);

    public String getCurrentOrgName() {
        return StringUtils.substringAfter(webdriver().driver().url(), "&org=");
    }

    public void createOrganization(String orgName) {
        plusBtn.shouldBe(Condition.enabled).click();
        orgCreationFrame.shouldBe(Condition.visible);
        orgNameInput.shouldBe(Condition.visible).val(orgName.toLowerCase());
        createOrgBtn.shouldBe(Condition.enabled).click();
        orgCreationFrame.shouldBe(Condition.disappear);
        $(By.cssSelector(".MuiCircularProgress-svg")).shouldBe(Condition.disappear);
    }

    @Override
    public boolean checkPage() {
        return oneOrganization.shouldBe(Condition.visible).isDisplayed();
    }
}
