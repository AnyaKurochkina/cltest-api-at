package ui.t1.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.uiExtesions.ConfigExtension;
import ui.t1.steps.AuthSteps;
import ui.t1.steps.MainSteps;
import ui.t1.steps.OrgStructureSteps;
import ui.t1.steps.OrganizationSteps;

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
        //Создаем VmWare организацию
        OrganizationSteps organizationSteps = new OrganizationSteps();
        String orgName = organizationSteps.createOrganization();
        //Удаялем VmWare организацию
        organizationSteps.deleteOrganizationFromOrganizationPage(orgName);
    }
}
