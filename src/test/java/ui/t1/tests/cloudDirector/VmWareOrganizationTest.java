package ui.t1.tests.cloudDirector;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.authorizer.AuthorizerSteps;
import ui.cloud.pages.LoginPage;
import ui.extesions.ConfigExtension;
import ui.t1.pages.CloudDirectorPage;
import ui.t1.pages.IndexPage;

import java.util.UUID;

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
        this.project = Project.builder().projectName("Проект для теста VMWare организаций").folderName(parentFolder).build().createObjectPrivateAccess();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    void name() {
        String name = UUID.randomUUID().toString().substring(25);
        CloudDirectorPage cloudDirector = new IndexPage().goToCloudDirector();
        name = cloudDirector.create(name);
        cloudDirector.delete(name);
    }
}
