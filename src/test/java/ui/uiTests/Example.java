package ui.uiTests;

import com.codeborne.selenide.Condition;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import ui.pages.*;
import ui.uiExtesions.ConfigExtension;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.*;
import static core.utils.Waiting.sleep;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.http.HttpStatus.SC_OK;

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
        String fullOrgName = "vtb-test-afljz";
//        String fullOrgName = (listOfOrganizationsPage.getCurrentOrgName() + "-" + orgName).toLowerCase(Locale.ROOT);
//        listOfOrganizationsPage.createOrganization(orgName);
        listOfOrganizationsPage.checkPage();
        listOfOrganizationsPage.stepInOrganization(fullOrgName);
        OrganizationPage organizationPage = new OrganizationPage();
        organizationPage.checkPage(fullOrgName);
//        organizationPage.createVirtualDataCentre();
//        VmWareDataCentreCreationPage vmWareDataCentreCreationPage = new VmWareDataCentreCreationPage();
        String nameOfDataCentre = "sdfsdf";
//        String nameOfDataCentre = randomAlphabetic(5).toLowerCase(Locale.ROOT);
//        vmWareDataCentreCreationPage
//                .setNameDataCentre(nameOfDataCentre)
//                .setIpV4Value(10)
//                .setCpuValue(14)
//                .setWidthValue(15)
//                .setRamValue(35)
//                .confirmOrder();

        organizationPage.checkPage(fullOrgName);
        organizationPage.checkStatusOfDataCentre(nameOfDataCentre);

        organizationPage.stepInDataCentre(nameOfDataCentre);

        DataCentrePage dataCentrePage = new DataCentrePage(nameOfDataCentre);
        dataCentrePage.deleteDataCentre();
        System.out.println();


        listOfOrganizationsPage.deleteOrganization(fullOrgName);
    }

    @DisplayName("Удаление Дата центра")
    @Test
    public void deleteDataCentre() {
        //Открываем сайт
        open("/");
        //Логинимся
        LoginPage loginPage = new LoginPage();
        loginPage.singIn();
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
        //Создаем виртуальный дата центр внутри нашей организации
        OrganizationPage organizationPage = new OrganizationPage();
        organizationPage.checkPage(fullOrgName);
        organizationPage.createVirtualDataCentre();
        //Заполняем поля при создании дата центра
        VmWareDataCentreCreationPage vmWareDataCentreCreationPage = new VmWareDataCentreCreationPage();
        String nameOfDataCentre = randomAlphabetic(5).toLowerCase(Locale.ROOT);
        vmWareDataCentreCreationPage
                .setNameDataCentre(nameOfDataCentre)
                .setIpV4Value(10)
                .setCpuValue(14)
                .setWidthValue(15)
                .setRamValue(35)
                .confirmOrder();
        organizationPage.checkLoader();
        organizationPage.checkPage(fullOrgName);
        //Проверяем статус созданного дата центра и заходим в него
        organizationPage.checkStatusOfDataCentre(nameOfDataCentre);
        organizationPage.stepInDataCentre(nameOfDataCentre);
        DataCentrePage dataCentrePage = new DataCentrePage(nameOfDataCentre);
        //Убираем защиту от удаления и удаляем дата центр
        dataCentrePage.removeDeletionProtection();
        dataCentrePage.deleteDataCentre();
        //Возвращаемся к организации
        dataCentrePage.backToVmWareOrganization();
        organizationPage.checkPage();
        //Проверяем статус дата центра
        organizationPage.checkDeleteStatusOfDataCentre(nameOfDataCentre);
        //Удаляем организацию
        organizationPage.deleteOrganization();
        listOfOrganizationsPage.checkPage();
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
        String fullOrgName = (listOfOrganizationsPage.getCurrentGlobalOrgName() + "-" + orgName).toLowerCase(Locale.ROOT);
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
