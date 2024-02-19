package ui.t1.tests.IAM.serviceAccounts;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ServiceAccount;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IAM.serviceAccounts.ServiceAccountPage;
import ui.t1.pages.IAM.serviceAccounts.ServiceAccountsListPage;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;

import java.util.Arrays;
import java.util.Collections;

import static core.helper.StringUtils.format;
import static core.helper.StringUtils.getClipBoardText;
import static core.utils.AssertUtils.assertEqualsJson;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("IAM и Управление")
@Feature("Сервисные аккаунты")
@Tags({@Tag("serviceAccounts_ui_t1")})
@Log4j2
@ExtendWith(ConfigExtension.class)
public class ServiceAccountsTests extends Tests {

    Project project = Project.builder().isForOrders(true).build().onlyGetObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @DisplayName("Создание/удаление сервисного аккаунта без API-ключей")
    public void createServiceAccount() {
        ServiceAccount account = ServiceAccount.builder()
                .title("create_delete_service_account_ui_test")
                .roles(Arrays.asList("Администратор", "Наблюдатель", "Редактор"))
                .build();
        assertTrue(new IndexPage()
                .goToServiceAccounts()
                .createServiceAccount(account)
                .checkHeadersAndTabs()
                .checkAccountDataInServiceAccountPage(account)
                .goToServiceAccountList()
                .isServiceAccountExist(account.getTitle()), format("Сервисный аккаунт {} не найден", account.getTitle()));
        assertFalse(new ServiceAccountsListPage()
                        .deleteServiceAccount(account)
                        .isServiceAccountExist(account.getTitle()),
                format("Сервисный аккаунт {} существует после ожидаемого удаления", account.getTitle()));
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
                .isServiceAccountExist(updatedAccount.getTitle()), format("Сервисный аккаунт {} не найден", account.getTitle()));
        account.deleteObject();
    }

    @Test
    @DisplayName("Создание Api ключа")
    public void createApiKey() {
        ServiceAccount account = ServiceAccount.builder()
                .title("create_api_key_ui_test")
                .withApiKey(false)
                .roles(Collections.singletonList("Администратор"))
                .build()
                .createObject();
        JSONObject jsonObject = new IndexPage()
                .goToServiceAccounts()
                .goToServiceAccountPage(account)
                .createApiKey(account.getTitle());
        String text = getClipBoardText();
        assertEqualsJson(jsonObject, new JSONObject(text));
        account.setWithApiKey(true);
        account.save();
    }

    @Test
    @DisplayName("Создание статического ключа")
    public void createStaticKey() {
        String description = "Статический ключ для тестов ui";
        ServiceAccount account = ServiceAccount.builder()
                .title("create_static_key_ui_test")
                .withApiKey(false)
                .roles(Collections.singletonList("Администратор хранилища"))
                .build()
                .createObject();
        JSONObject jsonObject = new IndexPage()
                .goToServiceAccounts()
                .goToServiceAccountPage(account)
                .createStaticKey(description);
        String text = getClipBoardText();
        assertEqualsJson(jsonObject, new JSONObject(text));
        String id = jsonObject.getString("access_id");
        assertTrue(new ServiceAccountPage(account.getTitle())
                .isStaticKeyExist(id));
        account.deleteStaticKeyNewStorage(id);
    }

    @Test
    @DisplayName("Удаление статического ключа")
    public void deleteStaticKey() {
        ServiceAccount account = ServiceAccount.builder()
                .title("create_static_key_ui_test")
                .withApiKey(false)
                .roles(Collections.singletonList("Администратор хранилища"))
                .build()
                .createObject();
        account.createStaticKeyNewStorage();
        assertTrue(new IndexPage()
                .goToServiceAccounts()
                .goToServiceAccountPage(account)
                .deleteStaticKey(account)
                .isTableEmpty());
    }

    @Test
    @DisplayName("Удаление сервисного аккаунта с API-ключом")
    public void deleteServiceAccountWithApiKey() {
        ServiceAccount account = ServiceAccount.builder()
                .title("delete_service_account_with_api_key_ui_test")
                .build()
                .createObject();
        assertTrue(new IndexPage()
                .goToServiceAccounts()
                .deleteServiceAccountWithApiToken(account)
                .isServiceAccountExist(account.getTitle()), format("Сервисный аккаунт {} не найден", account.getTitle()));
    }

    @Test
    @DisplayName("Удаление API-ключа")
    public void deleteApiKey() {
        ServiceAccount account = ServiceAccount.builder()
                .title("delete_api_key_ui_test")
                .build()
                .createObject();
        assertTrue(new IndexPage()
                .goToServiceAccounts()
                .goToServiceAccountPage(account)
                .deleteApiKey()
                .isTableEmpty());
        account.setWithApiKey(false);
        account.save();
    }
}
