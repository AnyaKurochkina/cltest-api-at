package ui.uiTests;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import ui.uiExtesions.ConfigExtension;
import ui.pages.LoginPage;
import ui.pages.MainPage;
import ui.pages.OrganizationsPage;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
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
        String nameOfOrg = (organizationsPage.getCurrentOrgName() + "-" + name).toLowerCase(Locale.ROOT);
        organizationsPage.createOrganization(name);
        organizationsPage.checkPage();
        organizationsPage.deleteOrganization(nameOfOrg);
    }

    @Test
    public void testingOpen() {
        open("/");
        sleep(5000);
    }

    @Test
    public void failTest() {
        open("/");
        sleep(5000);
        $(By.xpath("//input[@id='sdfsdfsdf']")).shouldBe(Condition.visible);
    }
}
