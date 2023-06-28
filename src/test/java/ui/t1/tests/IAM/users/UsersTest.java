package ui.t1.tests.IAM.users;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import ui.t1.pages.IAM.users.UsersPage;
import ui.t1.pages.IndexPage;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ui.t1.pages.IAM.users.UsersPage.isUserAdded;

@Epic("IAM и Управление")
@Feature("Пользователи")
@Tags({@Tag("ui_cloud_users")})
@Log4j2
public class UsersTest extends AbstractIAMTest {

    @Test
    @TmsLinks({@TmsLink("134902"), @TmsLink("134904")})
    @DisplayName("Пользователи. Добавление/Отзыв прав в проект.")
    void addUserToProjectTest() {
        new IndexPage()
                .goToUsers()
                .addUser(user)
                .removeUser(user);
    }

    @Test
    @TmsLinks({@TmsLink("538813"), @TmsLink("536159")})
    @DisplayName("Пользователи. Добавление/Отзыв прав в организацию.")
    void addUserToOrganizationTest() {
        Organization organization = Organization.builder().build().createObject();
        new IndexPage()
                .goToUsers()
                .changeContext("organization", organization.getName(), organization.getTitle())
                .addUser(user3)
                .removeUser(user3);
    }

    @Test
    @TmsLink("134901")
    @DisplayName("Пользователи. Добавление в Папку.")
    void addUserToFolderTest() {
        new IndexPage()
                .goToUsers()
                .changeContext("folder", folderId)
                .addUser(user)
                .removeUser(user);
    }

    @Test
    @TmsLink("134903")
    @DisplayName("Пользователи. Редактирование ролей пользователя.")
    void editUserTest() {
        new IndexPage()
                .goToUsers()
                .addUser(user2)
                .addRole(user2, "Редактор");
        user2.addRole("Редактор");
        assertTrue(isUserAdded(user2));
        new UsersPage()
                .removeRoles(user2, user2.getRole())
                .removeUser(user2);
    }

    @Test
    @TmsLink("134905")
    @DisplayName("Пользователи. Переключение контекста.")
    void changeContextTest() {
        Project project = Project.builder()
                .projectName("test_api")
                .folderName(folderId)
                .build()
                .createObject();
        new IndexPage()
                .goToUsers()
                .changeContext("project", project.getId(), project.getProjectName());
    }

    @Test
    @TmsLink("511113")
    @DisplayName("Пользователи. Просмотр списка пользователей в контексте организации.")
    void userListByOrganizationContextTest() {
        Organization organization = Organization.builder().build().createObject();
        new IndexPage()
                .goToUsers()
                .changeContext("organization", organization.getName(), organization.getTitle())
                .showUsers("Текущего уровня и ниже")
                .checkTableHeaders(Arrays.asList("Пользователь", "Роли", "Роли уровней ниже", "Статус"));
    }

    @Test
    @TmsLink("511136")
    @DisplayName("Пользователи. Просмотр списка пользователей из папки.")
    void userListByFolderContextTest() {
        new IndexPage()
                .goToUsers()
                .changeContext("folder", folderId)
                .showUsers("Текущего уровня и ниже")
                .checkTableHeaders(Arrays.asList("Пользователь", "Роли", "Роли уровней ниже", "Статус"))
                .showUsers("Текущего уровня и выше")
                .checkTableHeaders(Arrays.asList("Пользователь", "Роли", "Роли уровней выше", "Статус"));
    }
}
