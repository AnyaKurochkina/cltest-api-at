package ui.uiSteps;

import io.qameta.allure.Step;
import ui.pages.ListOfOrganizationsPage;
import ui.pages.MainPage;
import ui.pages.OrganizationPage;

import java.util.Locale;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class MainSteps {

    @Step("Создание VmWare организации")
    public void createOrganization(){
        //Переходим к списку VmWare организаций
        MainPage mainPage = new MainPage();
        mainPage.goToListOfOrganizations();
        ListOfOrganizationsPage listOfOrganizationsPage = new ListOfOrganizationsPage();
    }

    @Step("Переход к оргструктуре")
    public void goToOrgStructure(){
        //Переходим к списку VmWare организаций
        MainPage mainPage = new MainPage();
        mainPage.goToOrgStructure();
    }
}
