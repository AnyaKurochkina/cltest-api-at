package ui.t1.tests;

import core.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.LoginPage;
import ui.t1.steps.*;
import ui.uiExtesions.ConfigExtension;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@ExtendWith(ConfigExtension.class)
public class DataCenterTests {

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
//        new LoginPage(project.getId())
//                .signIn(Role.DAY2_SERVICE_MANAGER);
    }

    @DisplayName("Создание и удаление Дата центра")
    @Test
    public void createAndDeleteDataCenter() {
        //Открываем сайт
        open("/");
//        AuthSteps authSteps = new AuthSteps();
//        authSteps.signIn();
        //Выбираем организацию
        MainSteps mainSteps = new MainSteps();
        mainSteps.goToOrgStructure();
        OrgStructureSteps orgStructureSteps = new OrgStructureSteps();
        orgStructureSteps
                .chooseGlobalOrganization()
                .chooseProject();
        //Создаем VmWare организацию
        OrganizationSteps organizationSteps = new OrganizationSteps();
        String orgName = organizationSteps.createOrganization();
        DataCenterSteps dataCenterSteps = new DataCenterSteps();
        //Создаем дата центр
        String nameOfDataCentre = dataCenterSteps
                .createDataCenter(orgName, randomAlphabetic(5).toLowerCase(Locale.ROOT));
        dataCenterSteps.stepInDataCenter(nameOfDataCentre);
        //Удаляем дата центр
        dataCenterSteps.deleteDataCenter(nameOfDataCentre);
        //Удаляем VmWare организацию
        organizationSteps.deleteOrganizationFromOrganizationPage(orgName);
    }

    @DisplayName("Зарезервировать внешние IP адреса")
    @Test
    public void reserveIpAddress() {
        //Открываем сайт
        open("/");
//        AuthSteps authSteps = new AuthSteps();
//        authSteps.signIn();
        //Выбираем организацию
        MainSteps mainSteps = new MainSteps();
        mainSteps.goToOrgStructure();
        OrgStructureSteps orgStructureSteps = new OrgStructureSteps();
        orgStructureSteps
                .chooseGlobalOrganization()
                .chooseProject();
        //Создаем VmWare организацию
        OrganizationSteps organizationSteps = new OrganizationSteps();
        String orgName = organizationSteps.createOrganization();
        DataCenterSteps dataCenterSteps = new DataCenterSteps();
        //Создаем дата центр
        String nameOfDataCentre = dataCenterSteps
                .createDataCenter(orgName, randomAlphabetic(5).toLowerCase(Locale.ROOT));
        dataCenterSteps.stepInDataCenter(nameOfDataCentre);

    }
}
