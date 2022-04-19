package ui.uiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.uiExtesions.ConfigExtension;
import ui.uiSteps.AuthSteps;
import ui.uiSteps.MainSteps;
import ui.uiSteps.OrgStructureSteps;
import ui.uiSteps.OrganizationSteps;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith(ConfigExtension.class)
public class VmWareOrganizationsTests {

    @DisplayName("Создание и удаление VmWare организации")
    @Test
    public void createAndDeleteOrg() {
        //Открываем сайт
        open("/");
        //Логинимся
        AuthSteps authSteps = new AuthSteps();
        authSteps.signIn();
        //Выбираем организацию
        MainSteps mainSteps = new MainSteps();
        mainSteps.goToOrgStructure();
        OrgStructureSteps orgStructureSteps = new OrgStructureSteps();
        orgStructureSteps
                .chooseGlobalOrganization()
                .chooseProject();
        //Создаем организацию
        OrganizationSteps organizationSteps = new OrganizationSteps();
        String orgName = organizationSteps.createOrganization();
        //Удаялем организацию
        organizationSteps.deleteOrganizationFromOrganizationPage(orgName);
    }
}
