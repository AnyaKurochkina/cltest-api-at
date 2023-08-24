package ui.t1.tests.IAM.users;

import api.Tests;
import core.enums.Role;
import kotlin.collections.ArrayDeque;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.authorizer.AuthorizerSteps;
import ui.extesions.ConfigExtension;
import ui.models.IamUser;
import ui.t1.pages.T1LoginPage;

import java.util.Arrays;

@Log4j2
@ExtendWith(ConfigExtension.class)
public abstract class AbstractIAMTest extends Tests {
    Project project;
    String folderId;
    public static IamUser user;
    IamUser user2;
    IamUser user3;

    public AbstractIAMTest() {
        project = Project.builder().isForOrders(true).build().createObject();
        folderId = AuthorizerSteps.getParentProject(project.getId());;
        user = new IamUser("airat.muzafarov@gmail.com", new ArrayDeque<>(Arrays.asList("Администратор")));
        user2 = new IamUser("x64-bit@ya.ru", new ArrayDeque<>(Arrays.asList("Администратор")));
        user3 = new IamUser("amuzafarov@t1.ru", new ArrayDeque<>(Arrays.asList("Администратор")));
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }
}
