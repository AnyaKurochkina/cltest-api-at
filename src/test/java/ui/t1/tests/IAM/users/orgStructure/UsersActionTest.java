package ui.t1.tests.IAM.users.orgStructure;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import kotlin.collections.ArrayDeque;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.models.IamUser;
import ui.t1.pages.IAM.ModalWindow;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.authorizer.AuthorizerSteps.createFolder;
import static steps.authorizer.AuthorizerSteps.deleteFolderByNameT1;

@Epic("IAM и Управление")
@Feature("Действия с пользователями")
@Tags({@Tag("ui_cloud_users_actions")})
@Log4j2
@ExtendWith(ConfigExtension.class)
public class UsersActionTest extends Tests {

    Project project = Project.builder().isForOrders(true).build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @DisplayName("Добавление/удаление пользователя")
    public void addUserTest() {
        IamUser user = new IamUser("airat.muzafarov@gmail.com", new ArrayDeque<>(Arrays.asList("Администратор")));
        String folderTitle = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        String folderName = createFolder(folderTitle);
        try {
            assertTrue(new IndexPage().goToOrgStructure()
                    .openModalWindow(folderTitle)
                    .setRole(user)
                    .isUserAdded(user), String.format("User %s not found", user.getEmail()));
            new IndexPage().goToOrgStructure()
                    .openModalWindow(folderTitle)
                    .deleteUser(user);
            assertTrue(new ModalWindow().isUserTableEmpty());
        } finally {
            deleteFolderByNameT1(folderName);
        }
    }
}
