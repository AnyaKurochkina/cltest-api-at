package ui.testsUi;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import ui.UiExtesions.ConfigExtension;
import ui.pages.LoginPage;
import ui.pages.MainPage;
import ui.pages.OrganizationsPage;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$$;
import static core.utils.Waiting.sleep;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@ExtendWith(ConfigExtension.class)
public class Example {

    @Test
    public void createOrg() {
        open("/");
        LoginPage loginPage = new LoginPage();
        loginPage.singIn();
        MainPage mainPage = new MainPage();
        mainPage.goToListOfOrganizations();
        OrganizationsPage organizationsPage = new OrganizationsPage();
        String name = randomAlphabetic(5);
        organizationsPage.checkPage();
        String nameOfOrg = (organizationsPage.getCurrentOrgName() + "-" + name).toLowerCase(Locale.ROOT);
        organizationsPage.createOrganization(name);
        organizationsPage.checkPage();
        ElementsCollection collection = $$(By.xpath("//*[@class='MuiTableRow-root MuiTableRow-hover']"));
        for (SelenideElement element: collection){
            if (element.$(By.cssSelector(".MuiTableCell-alignLeft"))
                    .getText().equals(nameOfOrg)){
                element.$(By.cssSelector("div[title='Удалить']")).shouldBe(Condition.enabled).click();
                break;
            }
        }
        String idForDelete = $(By.cssSelector(".MuiTypography-colorTextSecondary>b")).shouldBe(Condition.visible).getText();
        $(By.xpath("//input[@name='id']")).shouldBe(Condition.visible).val(idForDelete);
        $(By.xpath("//*[text()='Удалить']")).shouldBe(Condition.enabled).click();
        $(By.xpath("//div[@id='notistack-snackbar']")).shouldBe(Condition.visible).shouldHave(Condition.text("VMware организация "+ nameOfOrg +" удалена успешно"));
        $(By.xpath("//div[@id='notistack-snackbar']")).shouldBe(Condition.disappear, Duration.ofMillis(15000));
        System.out.println();
    }

    @Test
    public void testingOpen() {
        open("/");
        sleep(5000);
    }
}
