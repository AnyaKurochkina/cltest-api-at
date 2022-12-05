package ui.t1.tests.cloudDirector;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.t1.portalBack.VmWareOrganization;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.authorizer.AuthorizerSteps;
import ui.cloud.pages.LoginPage;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;

import java.util.UUID;

import static steps.portalBack.VdcOrganizationSteps.createVMwareOrganization;
import static steps.portalBack.VdcOrganizationSteps.deleteVMwareOrganization;

@ExtendWith(ConfigExtension.class)
@Epic("Cloud Director")
@Feature("VMWare организация")
@Tags({@Tag("ui_cloud_director")})
@Log4j2
public class VmWareOrganizationTest extends Tests {
    Project project;

    public VmWareOrganizationTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String parentFolder = AuthorizerSteps.getParentProject(project.getId());
        this.project = Project.builder()
                .projectName("Проект для теста VMWare организаций")
                .folderName(parentFolder)
                .build()
                .createObjectPrivateAccess();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @TmsLink("820925")
    @DisplayName("VMware. Проверка уникальности имени организации")
    void createVMwareOrganizationWithExistNameTest() {
        String name = UUID.randomUUID().toString().substring(25);
        VmWareOrganization vmWareOrganization = createVMwareOrganization(name, project.getId());
        try {
            new IndexPage()
                    .goToCloudDirector()
                    .createWithExistName(name);
        } finally {
            deleteVMwareOrganization(project.getId(), vmWareOrganization.getName());
        }
    }

    @Test
    @TmsLink("147520")
    @DisplayName("VMware. Создание организации.")
    void createVMwareOrganizationTest() {
        String orgName = new IndexPage()
                .goToCloudDirector()
                .create(UUID.randomUUID().toString().substring(25));
        deleteVMwareOrganization(project.getId(), orgName);
    }

    @Test
    @TmsLink("147521")
    @DisplayName("VMware. Удаление организации.")
    void deleteVMwareOrganizationTest() {
        String name = UUID.randomUUID().toString().substring(25);
        VmWareOrganization vmWareOrganization = createVMwareOrganization(name, project.getId());
        try {
            new IndexPage()
                    .goToCloudDirector()
                    .delete(vmWareOrganization.getName());
        } finally {
            deleteVMwareOrganization(project.getId(), vmWareOrganization.getName());
        }
    }

    @Test
    @TmsLink("")
    @Disabled("Нужно доделать")
    @DisplayName("WMware. Управление. Тарифы услуг")
    void serviceTariffVMwareOrganizationTest() {
        Project testProject;
        Project project = Project.builder().isForOrders(true).build().createObject();
        String parentFolder = AuthorizerSteps.getParentProject(project.getId());
        testProject = Project.builder().projectName("Проект для теста VMWare тарифы услуг").folderName(parentFolder)
                .build()
                .createObjectPrivateAccess();
        new LoginPage(testProject.getId());
    }
}
