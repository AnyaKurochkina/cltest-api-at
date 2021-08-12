package steps.accountManager;

import core.helper.Configurier;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.accountManager.Account;
import models.authorizer.Folder;
import models.authorizer.Organization;
import steps.Steps;


@Log4j2
public class AccountSteps extends Steps {
    private static final String URL = Configurier.getAppProp("host_kong");


    @Step("Создание счета для папки {folderName}")
    public void createAccount(String folderName) {
        log.info("Изменение базового шаблона запроса при создании счета папки с типом: " + folderName);
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", folderName)
                .getEntity();

        String accountId = jsonHelper.getJsonTemplate("/accountmanager/accountTemplate.json")
                .set("$.parent_id", getAccountIdByContext(folder.parentId))
                .set("$.name", String.format("%s (%s)", folderName, folder.id))
                .send(URL)
                .post("accountmanager/api/v1/organizations/vtb/accounts")
                .assertStatus(200)
                .jsonPath()
                .get("account.account_id");

        new Http(URL)
                .post("accountmanager/api/v1/folders/" + folder.id + "/accounts/" + accountId)
                .assertStatus(200);

        Account account = Account.builder()
                .accountId(accountId)
                .folderId(folder.id)
                .build();

        cacheService.saveEntity(account);

    }

    @Step("Перевод со счета организации {sourceContext} на счет папки {targetContext} суммы {amount}")
    public void transferMoneyFromOrganizationToFolder(String org, String targetContext, String amount) {
        Organization organization = cacheService.entity(Organization.class)
                .withField("title", org)
                .getEntity();
        transferMoneyFromAccountToFolder(organization.name, targetContext, amount);
    }

    @Step("Перевод со счета {sourceContext} на счет папки {targetContext} проекта {amount}")
    public void transferMoneyFromFolderToFolder(String sourceContext, String targetContext, String amount) {
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", sourceContext)
                .getEntity();
        transferMoneyFromAccountToFolder(folder.id, targetContext, amount);
    }

    private void transferMoneyFromAccountToFolder(String sourceContext, String targetContext, String amount) {
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", targetContext)
                .getEntity();
        String sourceAccountId = getAccountIdByContext(sourceContext);
        String targetAccountId = getAccountIdByContext(folder.id);
        log.info(String.format("Отправка запроса на перевод денег со счета %s папки %s на счет %s папки %s", sourceAccountId, sourceContext, targetAccountId, targetContext));

        jsonHelper.getJsonTemplate("/accountManager/transaction.json")
                .set("$.from_account_id", sourceAccountId)
                .set("$.to_account_id", targetAccountId)
                .set("$.amount", amount)
                .set("$.reason", "Перевод в рамках тестирования на сумму " + amount)
                .send(URL)
                .post("accountmanager/api/v1/organizations/vtb/accounts/transfers")
                .assertStatus(200);
    }


    public String getAccountIdByContext(String context) {
        log.info("Получение account_id для контекста - " + context);
        String account_id = null;
        int total_count = new Http(URL)
                .get("accountmanager/api/v1/organizations/vtb/accounts")
                .assertStatus(200)
                .jsonPath()
                .get("meta.total_count");
        int countOfIteration = total_count/ 100 + 1;
        for (int i = 1; i<=countOfIteration; i++) {
            account_id = new Http(URL)
                    .get("accountmanager/api/v1/organizations/vtb/accounts?page="+i+"&per_page=100")
                    .assertStatus(200)
                    .jsonPath()
                    .get(String.format("list.find{it.name.contains('%s') || it.name.contains('%s')}.account_id", context.toUpperCase(), context.toLowerCase()));
            if(account_id != null)
                break;
        }
        return account_id;
    }

    @Step("Удаление счета для папки {folderName}")
    public void deleteAccount(String folderName) {
        Organization organization = cacheService.entity(Organization.class).getEntity();
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", folderName)
                .getEntity();
        Account account = cacheService.entity(Account.class)
                .withField("folderId", folder.id)
                .getEntity();
        log.info(String.format("Удаление счета %s для папки %s", account.accountId, folder.id));
        JsonPath jsonPath = new Http(URL)
                .delete(String.format("accountmanager/api/v1/organizations/%s/accounts/%s?force_unlink=1", organization.name, account.accountId))
                .assertStatus(200)
                .jsonPath();

        account.isDeleted = true;
        cacheService.saveEntity(account);

    }
}
