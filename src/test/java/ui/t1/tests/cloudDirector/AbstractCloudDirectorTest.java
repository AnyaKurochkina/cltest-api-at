package ui.t1.tests.cloudDirector;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.t1.portalBack.VmWareOrganization;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.models.cloudDirector.StorageProfile;
import ui.models.cloudDirector.Vdc;
import ui.t1.pages.T1LoginPage;

import java.util.UUID;

import static steps.portalBack.VdcOrganizationSteps.createVMwareOrganization;
import static steps.portalBack.VdcOrganizationSteps.deleteVMwareOrganization;

@Log4j2
@ExtendWith(ConfigExtension.class)
@Epic("Cloud Director")
@Tags({@Tag("ui_cloud_director")})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractCloudDirectorTest extends Tests {
    Project project;
    VmWareOrganization vmWareOrganization;
    String name;
    String dataCentreName;
    Vdc testVdc;

    public AbstractCloudDirectorTest() {
        project = Project.builder().isForOrders(true).build().createObject();
        name = UUID.randomUUID().toString().substring(25) + "-at-ui";
        vmWareOrganization = createVMwareOrganization(name, project.getId());
        dataCentreName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "-at-ui";
        testVdc = new Vdc(dataCentreName, "2", "4", new StorageProfile("High", "20"));
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage("proj-vrr9y4bhlg")
                .signIn(Role.CLOUD_ADMIN);
    }

    @AfterAll
    @Title("Удаление организации")
    public void deleteOrg() {
        deleteVMwareOrganization(project.getId(), vmWareOrganization.getName());
    }
}
