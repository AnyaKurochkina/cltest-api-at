package ui.t1.tests.IAM.users.orgStructure;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.ControlPanelLoginPage;
import ui.extesions.ConfigExtension;
import ui.models.Organization;
import ui.t1.pages.ControlPanelIndexPage;
import ui.t1.pages.T1LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("IAM и Управление")
@Feature("Действия с организацией")
@Tags({@Tag("ui_cloud_org_actions")})
@Log4j2
@ExtendWith(ConfigExtension.class)
public class OrganizationActionsTest extends Tests {
    Project project;

    public OrganizationActionsTest() {
        project = Project.builder().isForOrders(true).build().createObject();
    }

    @Test
    @DisabledIfEnv("t1prod")
    @DisplayName("Создание организации")
    public void createOrganizationTest() {
        new ControlPanelLoginPage().signIn(Role.SUPERADMIN);
        Organization organization = new Organization("org_for_ui_test", "airat.muzafarov@gmail.com", "1650000000");
        assertTrue(new ControlPanelIndexPage()
                .goToOrganizationPage()
                .createOrganization(organization)
                .isOrgExist(organization.getName()));
    }

    @Test
    @DisplayName("Переключение между организациями")
    public void switchBetweenOrganizationTest() {
        String orgName = "QA IFT T1";
        assertTrue(new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN)
                .changeContext()
                .selectOrganization(orgName)
                .isContextNameDisplayed(orgName));
    }
}
