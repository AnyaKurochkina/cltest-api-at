package tests.structure;

import io.qameta.allure.Description;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import steps.AccountManager;
import steps.AuthorizerSteps;
import steps.Hooks;

import java.io.IOException;

@Order(1)
@DisplayName("Создание орг структуры")
public class CreateOrgStructureTest extends Hooks {

    @DisplayName("Создание орг структуры")
    @Description("Создание бизнес-блока " +
            "> Создание департамента в бизнес-блоке " +
            "> Создание папки в департаменте " +
            "> Создание счета для папки аккаунта " +
            "> Связка счета с папкой " +
            "> Перевод со счета аккаунта на счет папки проекта " +
            "> Создание проекта")
//    @Tag("production")
    @Test
    public void createOrgStructure() throws IOException, ParseException {
        AuthorizerSteps authorizerSteps = new AuthorizerSteps();
        AccountManager accountManager = new AccountManager();
        authorizerSteps.createFolder("business_block", "vtb", "TEST_AT_API", "folder_id_Business");
        authorizerSteps.createFolder("department", "folder_id_Business", "TEST_DEP_FOLD", "folder_id_department");
        authorizerSteps.createFolder("default", "folder_id_department", "TEST_DEFAULT_FOLD", "folder_id_default");
        accountManager.createAccount("folder_id_default", "vtb");
        accountManager.linkAccountWithFolder("folder_id_default");
        accountManager.transferMoneyFromAccountToFolder("vtb", "folder_id_default", "15000.00");
        authorizerSteps.createProject("test_api_project", "folder_id_default");
    }
}
