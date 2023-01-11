package ui.t1.tests.cloudDirector;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.t1.portalBack.VmWareOrganization;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.authorizer.AuthorizerSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.LoginPage;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudDirector.DataCentrePage;

import java.util.UUID;

import static steps.portalBack.VdcOrganizationSteps.createVMwareOrganization;
import static steps.portalBack.VdcOrganizationSteps.deleteVMwareOrganization;

@ExtendWith(ConfigExtension.class)
@Epic("Cloud Director")
@Feature("VMWare организация. Виртуальны дата-центр.")
@Tags({@Tag("ui_cloud_director")})
@Log4j2
public class DataCentreTest extends Tests {
    Project project;

    public DataCentreTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String parentFolder = AuthorizerSteps.getParentProject(project.getId());
        this.project = Project.builder()
                .projectName("Проект для теста Виртуального дата центра")
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
    @TmsLinks({@TmsLink("147532"), @TmsLink("158903")})
    @DisplayName("VMware. Создание/Удаление VDC. Allocation Pool")
    public void createDataCentre() {
        String name = UUID.randomUUID().toString().substring(25);
        VmWareOrganization vmWareOrganization = createVMwareOrganization(name, project.getId());
        String dataCentreName = RandomStringUtils.randomAlphabetic(10).toLowerCase();
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentre(dataCentreName)
                .waitChangeStatus()
                .selectDataCentre(dataCentreName)
                .checkCreate();
        dataCentrePage.runActionWithCheckCost(CompareType.ZERO, dataCentrePage::delete);
        deleteVMwareOrganization(project.getId(), vmWareOrganization.getName());
    }
}
