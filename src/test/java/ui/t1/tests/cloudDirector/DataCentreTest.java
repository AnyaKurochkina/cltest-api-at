package ui.t1.tests.cloudDirector;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
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
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudDirector.DataCentrePage;

import java.util.UUID;

import static steps.portalBack.VdcOrganizationSteps.createVMwareOrganization;
import static steps.portalBack.VdcOrganizationSteps.deleteVMwareOrganization;

@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ConfigExtension.class)
@Epic("Cloud Director")
@Feature("VMWare организация. Виртуальны дата-центр.")
@Tags({@Tag("ui_cloud_director")})
@Log4j2
public class DataCentreTest extends Tests {
    Project project;
    VmWareOrganization vmWareOrganization;
    String dataCentreName;

    public DataCentreTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String parentFolder = AuthorizerSteps.getParentProject(project.getId());
        this.project = Project.builder()
                .projectName("Проект для теста Виртуального дата центра")
                .folderName(parentFolder)
                .build()
                .createObjectPrivateAccess();
        String name = UUID.randomUUID().toString().substring(25);
        vmWareOrganization = createVMwareOrganization(name, this.project.getId());
        dataCentreName = RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @Order(1)
    @TmsLink("147532")
    @DisplayName("VMware. Создание VDC. Allocation Pool")
    public void createDataCentre() {
        new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentre(dataCentreName)
                .waitChangeStatus()
                .selectDataCentre(dataCentreName)
                .checkCreate();
    }

    @Test
    @Order(2)
    @TmsLink("570222")
    @DisplayName("VMware. Проверка уникальности имени VDC")
    public void createDataCentreWithSameNameTest() {
        new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentreWithExistName(dataCentreName);
    }

    @Test
    @Order(3)
    @TmsLink("158901")
    @DisplayName("VMware. Зарезервировать внешние IP адреса")
    public void reserveExternalIPAddressesTest() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.addIpAddresses(2));
    }

    @Test
    @Order(4)
    @TmsLink("559036")
    @DisplayName("VMware. Изменение конфигурации VDC Allocation Pool")
    public void changeConfigVDCAllocationPoolTest() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.changeConfig(4, 16));
    }

    @Test
    @Order(100)
    @TmsLink("158903")
    @DisplayName("VMware. Удаление VDC")
    public void deleteDataCentre() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.ZERO, dataCentrePage::delete);
        deleteVMwareOrganization(project.getId(), vmWareOrganization.getName());
    }
}
