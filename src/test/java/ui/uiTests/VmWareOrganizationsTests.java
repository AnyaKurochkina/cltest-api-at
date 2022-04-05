package ui.uiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.*;
import ui.uiExtesions.ConfigExtension;
import ui.uiSteps.AuthSteps;
import ui.uiSteps.DataCenterSteps;
import ui.uiSteps.OrganizationSteps;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;
import static core.enums.DataCentreStatus.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

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
        //Создаем организацию
        OrganizationSteps organizationSteps = new OrganizationSteps();
        String orgName = organizationSteps.createOrganization();
        //Удаялем организацию
        organizationSteps.deleteOrganizationFromOrganizationPage(orgName);
    }
}
