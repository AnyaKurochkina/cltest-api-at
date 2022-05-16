package ui.t1.steps;

import io.qameta.allure.Step;
import ui.t1.pages.ListOfOrganizationsPage;
import ui.t1.pages.MainPage;
import ui.t1.pages.OrganizationPage;

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

    @Step("Переход к списку организаций")
    public void goToListOfOrganization(){
        MainPage mainPage = new MainPage();
        mainPage.goToListOfOrganizations();
        OrganizationPage organizationPage = new OrganizationPage();
    }
}
