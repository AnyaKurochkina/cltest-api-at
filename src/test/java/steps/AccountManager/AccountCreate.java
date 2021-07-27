package steps.AccountManager;

import core.helper.Configurier;
import core.helper.Http;
import core.helper.HttpOld;
import core.helper.ShareData;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.AccountManager.Account;
import models.Authorizer.Folder;
import org.junit.Assert;
import steps.Steps;


@Log4j2
public class AccountCreate extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");


    @Step("Создание счета для папки {folderName}")
    public void createAccount(String folderName) {
        log.info("Изменение базового шаблона запроса при создании счета папки с типом: " + folderName);
        Folder folder = cacheService.entity(Folder.class)
                .setField("name", folderName)
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

    @Step("Перевод со счета {sourceContext} на счет папки {targetContext} проекта {amount}")
    public void transferMoneyFromAccountToFolder(String sourceContext, String targetContext, String amount) {
        Folder folder = cacheService.entity(Folder.class)
                .setField("name", targetContext)
                .getEntity();
        String sourceAccountId = getAccountIdByContext(sourceContext);
        String targetAccountId = getAccountIdByContext(folder.id);
        System.out.println("SourceAccountId = " + sourceAccountId);
        System.out.println("targetAccountId = " + targetAccountId);
        log.info("Отправка запроса на перевод денег со счета папки " + sourceContext + " на счет папки " + targetContext);

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
}
