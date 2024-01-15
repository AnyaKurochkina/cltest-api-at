package ui.t1.tests.cloudDirector;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.authorizer.AuthorizerSteps;
import ui.extesions.ConfigExtension;
import ui.models.cloudDirector.StorageProfile;
import ui.models.cloudDirector.Vdc;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;
import ui.t1.pages.cloudDirector.CloudDirectorPage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.portalBack.VdcOrganizationSteps.deleteVMwareOrganization;

@ExtendWith(ConfigExtension.class)
@Epic("Cloud Director")
@Feature("VMWare организация")
@Tags({@Tag("ui_cloud_director")})
@Log4j2
public class VmWareOrganizationTest extends AbstractCloudDirectorTest {

    @Test
    @TmsLink("820925")
    @DisplayName("VMware. Проверка уникальности имени организации")
    void createVMwareOrganizationWithExistNameTest() {
        new IndexPage()
                .goToCloudDirector()
                .createWithExistName(name);
    }

    @Test
    @TmsLink("147520")
    @DisplayName("VMware. Создание организации.")
    void createVMwareOrganizationTest() {
        String orgName = new IndexPage()
                .goToCloudDirector()
                .create(UUID.randomUUID().toString().substring(25) + "-at-ui");
        deleteVMwareOrganization(project.getId(), orgName);
    }

    @Test
    @TmsLink("1620819")
    @DisplayName("VMware. Освобождение имени организации при удалении.")
    void createVMwareOrganizationWithSameNameAfterDeleteTest() {
        String name = UUID.randomUUID().toString().substring(25) + "-at-ui";
        String orgName = new IndexPage()
                .goToCloudDirector()
                .create(name);
        deleteVMwareOrganization(project.getId(), orgName);
        CloudDirectorPage cloudDirectorPage = new CloudDirectorPage();
        cloudDirectorPage.create(name);
        assertTrue(cloudDirectorPage.isOrganizationExist(orgName));
        deleteVMwareOrganization(project.getId(), orgName);
    }

    @Test
    @TmsLink("147521")
    @DisplayName("VMware. Удаление организации.")
    void deleteVMwareOrganizationTest() {
        String orgName = new IndexPage()
                .goToCloudDirector()
                .create(UUID.randomUUID().toString().substring(25) + "-at-ui");
        try {
            new CloudDirectorPage().delete(orgName);
        } catch (Exception e) {
            deleteVMwareOrganization(project.getId(), orgName);
        }
    }

    @Test
    @TmsLink("202041")
    @DisplayName("VMware. Удаление организации с Виртуальным дата-центром")
    void deleteVMwareOrganizationWithVDCTest() {
        String dataCentreName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "-at-ui";
        Vdc vdc = new Vdc(dataCentreName, "2", "4", new StorageProfile("High", "12"), "500");
        String orgName = new IndexPage()
                .goToCloudDirector()
                .create(UUID.randomUUID().toString().substring(25) + "-at-ui");
        assertTrue(new CloudDirectorPage()
                .goToOrganization(orgName)
                .addDataCentre(vdc)
                .waitChangeStatus()
                .goToCloudDirectorPage()
                .deleteWithOrders(orgName)
                .isOrganizationExist(orgName), "Организации не существует");
        new CloudDirectorPage().goToOrganization(orgName)
                .selectDataCentre(dataCentreName)
                .delete();
        deleteVMwareOrganization(project.getId(), orgName);
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
        new T1LoginPage(testProject.getId());
    }
}
