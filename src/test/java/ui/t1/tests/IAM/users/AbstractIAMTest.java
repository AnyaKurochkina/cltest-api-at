package ui.t1.tests.IAM.users;

import api.Tests;
import core.enums.Role;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Folder;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.authorizer.AuthorizerSteps;
import ui.extesions.ConfigExtension;
import ui.models.IamUser;
import ui.t1.pages.T1LoginPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static steps.resourceManager.ResourceManagerSteps.getFolderById;

@Log4j2
@ExtendWith(ConfigExtension.class)
public abstract class AbstractIAMTest extends Tests {
    Project project;
    Folder folder;
    Organization organization;
    IamUser user;
    IamUser user2;
    IamUser user3;

    public AbstractIAMTest() {
        organization = Organization.builder().type("default").build().createObject();
        project = Project.builder().isForOrders(true).build().createObject();
        folder = getFolderById(AuthorizerSteps.getParentProject(project.getId()));
        user = new IamUser("airat.muzafarov@gmail.com", new ArrayList<>(Collections.singletonList("Администратор")));
        user2 = new IamUser("x64-bit@ya.ru", new ArrayList<>(Arrays.asList("Администратор")));
        user3 = new IamUser("amuzafarov@t1.ru", new ArrayList<>(Collections.singletonList("Администратор")));
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }
}
