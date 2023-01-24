package ui.t1.tests.cloudDirector;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.t1.portalBack.VmWareOrganization;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.LoginPage;
import ui.extesions.ConfigExtension;

import java.util.UUID;

import static steps.portalBack.VdcOrganizationSteps.createVMwareOrganization;

@Log4j2
@ExtendWith(ConfigExtension.class)
@Epic("Cloud Director")
@Tags({@Tag("ui_cloud_director")})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractCloudDirectorTest extends Tests {
    Project project;
    VmWareOrganization vmWareOrganization;
    String dataCentreName;

    public AbstractCloudDirectorTest() {
        project = Project.builder().isForOrders(true).build().createObject();
        String name = UUID.randomUUID().toString().substring(25);
        vmWareOrganization = createVMwareOrganization(name, project.getId());
        dataCentreName = RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }
}
