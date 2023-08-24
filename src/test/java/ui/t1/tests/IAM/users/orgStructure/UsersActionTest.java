package ui.t1.tests.IAM.users.orgStructure;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;

import static ui.t1.tests.IAM.users.AbstractIAMTest.user;

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
    @DisplayName("Добавление пользователя")
  //  @Disabled("не готов")
    public void addUserTest() {
        String folderName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        new IndexPage().goToOrgStructure()
                .createFolder(folderName)
                .openModalWindow(folderName)
                .setRole(user)
                .isUserAdded(user);
    }
}
