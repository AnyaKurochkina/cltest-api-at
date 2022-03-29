package ui.uiTests;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import ui.pages.*;
import ui.uiExtesions.ConfigExtension;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;
import static core.utils.Waiting.sleep;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@ExtendWith(ConfigExtension.class)
public class Example {

    @DisplayName("Создание VmWare организации")
    @Test
    public void createOrg() {
        open("/");
        LoginPage loginPage = new LoginPage();
        loginPage.singIn();
        MainPage mainPage = new MainPage();
        mainPage.goToListOfOrganizations();
        ListOfOrganizationsPage listOfOrganizationsPage = new ListOfOrganizationsPage();
        String orgName = randomAlphabetic(5);
        String fullOrgName = (listOfOrganizationsPage.getCurrentOrgName() + "-" + orgName).toLowerCase(Locale.ROOT);
        listOfOrganizationsPage.createOrganization(orgName);
        listOfOrganizationsPage.checkPage();
        listOfOrganizationsPage.stepInOrganization(fullOrgName);
        OrganizationPage organizationPage = new OrganizationPage();
        organizationPage.checkPage(fullOrgName);
        organizationPage.createVirtualDataCentre();
        VmWareDataCentreCreationPage vmWareDataCentreCreationPage = new VmWareDataCentreCreationPage();
        String nameOfDataCentre = randomAlphabetic(5).toLowerCase(Locale.ROOT);
        vmWareDataCentreCreationPage
                .setNameDataCentre(nameOfDataCentre)
                .setIpV4Value(10)
                .setCpuValue(14)
                .setWidthValue(15)
                .setRamValue(35)
                .confirmOrder();
        organizationPage.checkPage(fullOrgName);
        organizationPage.checkStatusOfDataCentre(nameOfDataCentre);


        organizationPage.stepInDataCentre(nameOfDataCentre);

        System.out.println();



        listOfOrganizationsPage.deleteOrganization(fullOrgName);
    }

    @DisplayName("Удаление VmWare организации")
    @Test
    public void deleteOrg() {
        open("/");
        LoginPage loginPage = new LoginPage();
        loginPage.singIn();
        MainPage mainPage = new MainPage();
        mainPage.goToListOfOrganizations();
        ListOfOrganizationsPage listOfOrganizationsPage = new ListOfOrganizationsPage();
        String orgName = randomAlphabetic(5);
        String fullOrgName = (listOfOrganizationsPage.getCurrentOrgName() + "-" + orgName).toLowerCase(Locale.ROOT);
        listOfOrganizationsPage.createOrganization(orgName);
        listOfOrganizationsPage.checkPage();
        listOfOrganizationsPage.stepInOrganization(fullOrgName);
        OrganizationPage organizationPage = new OrganizationPage();
        organizationPage.checkPage(fullOrgName);
        organizationPage.createVirtualDataCentre();
        mainPage.getNotificationBar().shouldNotHave(Condition.text("Request failed with status code 502"));
        listOfOrganizationsPage.deleteOrganization(fullOrgName);
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
