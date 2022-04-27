package ui.t1.steps;

import io.qameta.allure.Step;
import ui.t1.pages.ListOfOrganizationsPage;
import ui.t1.pages.MainPage;
import ui.t1.pages.OrganizationPage;

import java.util.Locale;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class OrganizationSteps {

    @Step("Создание VmWare организации")
    public String createOrganization(){
        //Переходим к списку VmWare организаций
        MainPage mainPage = new MainPage();
        mainPage.goToListOfOrganizations();
        ListOfOrganizationsPage listOfOrganizationsPage = new ListOfOrganizationsPage();
        //Генерируем имя для организации
        String vmWareOrgName = randomAlphabetic(5);
        String fullOrgName = (listOfOrganizationsPage.getCurrentGlobalOrgName() + "-" + vmWareOrgName).toLowerCase(Locale.ROOT);
        //создаем организацию и заходим в неё
        listOfOrganizationsPage.createOrganization(vmWareOrgName);
        listOfOrganizationsPage.checkPage();
        listOfOrganizationsPage.stepInOrganization(fullOrgName);
        //Проверяем создание организации
        OrganizationPage organizationPage = new OrganizationPage();
        organizationPage.checkPage(fullOrgName);
        return fullOrgName;
    }

    @Step("Удаление VmWare организации из списка организаций")
    public void deleteOrganizationFromListOfOrganizations(String orgName){
        //Удаялем организацию
        OrganizationPage organizationPage = new OrganizationPage();
        organizationPage.checkPage(orgName);
        ListOfOrganizationsPage listOfOrganizationsPage = new ListOfOrganizationsPage();
        organizationPage.deleteOrganization();
        listOfOrganizationsPage.checkPage();
    }

    @Step("Удаление VmWare организации из страницы организации")
    public void deleteOrganizationFromOrganizationPage(String orgName){
        //Удаялем организацию
        OrganizationPage organizationPage = new OrganizationPage();
        organizationPage.checkPage(orgName);
        organizationPage.deleteOrganization();
    }
}
