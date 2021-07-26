package tests.structure;

import io.qameta.allure.Description;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import steps.AccountManager;
import steps.AuthorizerSteps;
import steps.Hooks;
import steps.PortalBackSteps;

import java.io.IOException;

@Order(1)
@DisplayName("Создание орг структуры")
public class CreateOrgStructureTest extends Hooks {
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();
    AccountManager accountManager = new AccountManager();
    PortalBackSteps portalBackSteps = new PortalBackSteps();

    @DisplayName("Создание орг структуры")
    @Description("Получение организации " +
            ">Поиск информационной системы " +
            ">Создание бизнес-блока " +
            "> Создание департамента в бизнес-блоке " +
            "> Создание папки в департаменте " +
            "> Создание счета для папки аккаунта " +
            "> Связка счета с папкой " +
            "> Перевод со счета аккаунта на счет папки проекта " +
            ">Получение идентификатора projectEnv " +
            "> Создание проекта")
//    @Tag("production")
    @Test
    public void createOrgStructure() throws IOException, ParseException {
        authorizerSteps.getOrgName("ВТБ");
        portalBackSteps.getInfoSys("crux");
        authorizerSteps.createFolder("business_block", "vtb", "TEST_AT_API3", "folder_id_Business");
        authorizerSteps.createFolder("department", "folder_id_Business", "TEST_DEP_FOLD3", "folder_id_department");
        authorizerSteps.createFolder("default", "folder_id_department", "TEST_DEFAULT_FOLD3", "folder_id_default");
        accountManager.createAccount("folder_id_default", "vtb");
        accountManager.linkAccountWithFolder("folder_id_default");
        accountManager.transferMoneyFromAccountToFolder("vtb", "folder_id_default", "15000.00");
        portalBackSteps.getProjectEnv("Серверы разработки ПО");
        authorizerSteps.createProject("test_api_project3", "folder_id_default");
    }
}
