package ui.t1.tests.cloudDirector;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;

@ExtendWith(ConfigExtension.class)
@Epic("Cloud Director")
@Feature("VMWare организация. Пользователи.")
@Tags({@Tag("ui_cloud_director")})
@Log4j2
public class UsersVmWareOrganizationTest extends AbstractCloudDirectorTest {
    //todo вынести проверку на существования пользователя

    @Test
    @TmsLink("147523")
    @DisplayName("VMware. Добавление пользователя")
    void addUserTest() {
            new IndexPage()
                    .goToCloudDirector()
                    .goToOrganization(vmWareOrganization.getName())
                    .goToUsers()
                    .addUser("test", "vApp User", "12345678");
    }

    @Test
    @TmsLink("558975")
    @DisplayName("VMware. Изменить пароль.")
    void changeUserPasswordTest() {
        String userName = "change_password_test";
            new IndexPage()
                    .goToCloudDirector()
                    .goToOrganization(vmWareOrganization.getName())
                    .goToUsers()
                    .addUser(userName, "vApp User", "12345678")
                    .changeUserPassword(userName, "87654321");
    }

    @Test
    @TmsLink("147526")
    @DisplayName("VMware. Редактирование пользователя.")
    void editUserTest() {
        String userName = "edit_user_test";
        String fio = "Ivan Ivanov";
        String email = "test@test.ru";
        String role = "vApp Author";
            new IndexPage()
                    .goToCloudDirector()
                    .goToOrganization(vmWareOrganization.getName())
                    .goToUsers()
                    .addUser(userName, "vApp User", "12345678")
                    .editUser(userName, fio, email, role)
                    .compareUserFields(userName, fio, email, role);
    }

    @Test
    @TmsLink("147528")
    @DisplayName("VMware. Удаление пользователя")
    void deleteUserTest() {
        String userName = "test_user";
            new IndexPage()
                    .goToCloudDirector()
                    .goToOrganization(vmWareOrganization.getName())
                    .goToUsers()
                    .addUser(userName, "vApp User", "12345678")
                    .deleteUser(userName);
    }
}
