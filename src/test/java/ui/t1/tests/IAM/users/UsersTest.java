package ui.t1.tests.IAM.users;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import ui.t1.pages.IAM.users.UsersPage;
import ui.t1.pages.IndexPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ui.t1.pages.IAM.users.UsersPage.isUserAdded;

@Epic("IAM и Управление")
@Feature("Пользователи")
@Tags({@Tag("ui_cloud_users")})
@Log4j2
public class UsersTest extends AbstractIAMTest {

    @Test
    @TmsLink("134902")
    @DisplayName("Пользователи. Добавление в проект.")
    void addUserToProjectTest() {
        new IndexPage()
                .goToUsers();
        assertFalse(isUserAdded(user));
        new UsersPage().addUser(user);
        assertTrue(isUserAdded(user));
    }

    @Test
    @TmsLink("134901")
    @DisplayName("Пользователи. Добавление в Папку.")
    void addUserToFolderTest() {
        new IndexPage()
                .changeContext("folder", folderId)
                .goToUsers();
        assertFalse(isUserAdded(user));
        new UsersPage().addUser(user);
        assertTrue(isUserAdded(user));
    }
}
