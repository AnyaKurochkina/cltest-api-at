package ui.t1.tests.IAM.serviceAccounts;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ServiceAccount;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("IAM и Управление")
@Feature("Сервисные аккаунты")
@Tags({@Tag("serviceAccounts_ui_t1")})
@Log4j2
@ExtendWith(ConfigExtension.class)
public class ServiceAccountsTests extends Tests {

    Project project = Project.builder().isForOrders(true).build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @DisplayName("Создание сервисного аккаунта")
    public void createServiceAccount() {
        ServiceAccount account = ServiceAccount.builder()
                .title("create_service_account_ui_test")
                .roles(Arrays.asList("Администратор", "Наблюдатель", "Редактор"))
                .build();
        new IndexPage()
                .goToServiceAccounts()
                .createServiceAccount(account)
                .checkHeadersAndTabs()
                .checkAccountDataInServiceAccountPage(account)
                .goToServiceAccountList()
                .isServiceAccountExist(account.getTitle());
    }

    @Test
    @DisplayName("Редактирование сервисного аккаунта")
    public void editServiceAccount() {
        ServiceAccount account = ServiceAccount.builder()
                .title("edit_service_account_ui_test")
                .roles(Collections.singletonList("Администратор"))
                .build()
                .createObject();
        ServiceAccount updatedAccount = ServiceAccount.builder()
                .title("updated_edit_service_account_ui_test")
                .roles(Arrays.asList("Администратор", "Наблюдатель", "Редактор"))
                .build();
        assertTrue(new IndexPage()
                .goToServiceAccounts()
                .editServiceAccount(account.getTitle(), updatedAccount)
                .checkAccountData(updatedAccount)
                .isServiceAccountExist(updatedAccount.getTitle()));
    }
}
