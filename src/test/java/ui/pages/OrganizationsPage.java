package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class OrganizationsPage implements Loadable {

    private final SelenideElement plusBtn = $(By.xpath("//div[@data-testid='vdc-org-list-add-button']"));
    private final SelenideElement orgNameInput = $(By.xpath("//input[@name='name']"));
    private final SelenideElement createOrgBtn = $(By.cssSelector(".MuiDialogActions-spacing > button:nth-child(2)"));
    private final SelenideElement oneOrganization = $(By.cssSelector(".MuiTableRow-hover"));
    private ElementsCollection allOrganizations = $$(By.xpath("//*[@class='MuiTableRow-root MuiTableRow-hover']"));
    private final SelenideElement orgCreationFrame = $(By.xpath("//div[@role='dialog']"));
    private final SelenideElement idForDelete = $(By.cssSelector(".MuiTypography-colorTextSecondary>b"));
    private final SelenideElement idForDeleteInput = $(By.xpath("//input[@name='id']"));
    private final SelenideElement confirmDeleBtn = $(By.xpath("//*[text()='Удалить']"));
    private final SelenideElement notificationBar = $(By.xpath("//div[@id='notistack-snackbar']"));

    public OrganizationsPage() {
        checkPage();
    }

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

    public void deleteOrganization(String orgName){
        for (SelenideElement element: allOrganizations){
            if (element.$(By.cssSelector(".MuiTableCell-alignLeft"))
                    .getText().equals(orgName)){
                element.$(By.cssSelector("div[title='Удалить']")).shouldBe(Condition.enabled).click();
                break;
            }
        }
        idForDeleteInput.shouldBe(Condition.visible).val(idForDelete.getText());
        confirmDeleBtn.shouldBe(Condition.enabled).click();
        notificationBar.shouldBe(Condition.visible).shouldHave(Condition.text("VMware организация "+ orgName +" удалена успешно"));
        notificationBar.shouldBe(Condition.disappear, Duration.ofMillis(15000));
    }

    @Override
    public void checkPage() {
        oneOrganization.shouldBe(Condition.visible);
    }
}
