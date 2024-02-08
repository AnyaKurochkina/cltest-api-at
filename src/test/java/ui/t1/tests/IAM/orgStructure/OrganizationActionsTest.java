package ui.t1.tests.IAM.orgStructure;

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

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    @DisplayName("Создание организации/Удаление организации")
    public void createOrganizationTest() {
        new ControlPanelLoginPage().signIn(Role.SUPERADMIN);
        Organization organization = new Organization("org_for_ui_test", "airat.muzafarov@gmail.com", "1650000000");
        assertTrue(new ControlPanelIndexPage()
                .goToOrganizationPage()
                .createOrganization(organization)
                .isOrgExist(organization.getName()));
        assertFalse(new ControlPanelIndexPage()
                .goToOrganizationPage()
                .deleteOrganization(organization)
                .isOrgExist(organization.getName()), format("Организация с именем {} найдена в таблице", organization.getName()));
    }

    @Test
    @DisplayName("Переключение между организациями")
    public void switchBetweenOrganizationTest() {
        models.cloud.authorizer.Organization org = models.cloud.authorizer.Organization
                .builder()
                .type("not_default")
                .build()
                .onlyGetObject();
        String orgName = org.getTitle();
        assertTrue(new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN)
                .goToContextDialog()
                .changeOrganization(orgName)
                .isContextNameDisplayed(orgName));
    }
}
