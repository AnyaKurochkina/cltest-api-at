package steps.accountManager;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.accountManager.Account;
import models.authorizer.Folder;
import models.authorizer.Organization;
import steps.Steps;

import java.util.Objects;

@Log4j2
public class AccountSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");

    /**
     *
     * @param folderName имя папки
     */
    @Step("Создание счета для папки {folderName}")
    public void createAccount(String folderName) {
        log.info("Изменение базового шаблона запроса при создании счета папки с типом: " + folderName);
        //Плучение папки по её имени
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", folderName)
                .getEntity();
        //Запрос на создание счета для папки с получением account ID
        String accountId = jsonHelper.getJsonTemplate("/accountManager/accountTemplate.json")
                .set("$.parent_id", getAccountIdByContext(folder.parentId))
                .set("$.name", String.format("%s (%s)", folderName, folder.getName()))
                .set("$.folder_uid", folder.getName())
                .send(URL)
                .post("accountmanager/api/v1/organizations/vtb/accounts")
                .assertStatus(200)
                .jsonPath()
                .get("account.account_id");
        //Получение счёта по его account ID
        Account account = Account.builder()
                .accountId(accountId)
                .folderId(folder.getName())
                .build();
        //Сохранение счёта в память
        cacheService.saveEntity(account);
    }

//    @Step("Перевод со счета организации {sourceContext} на счет папки {targetContext} суммы {amount}")
//    public void transferMoneyFromOrganizationToFolder(String org, String targetContext, String amount) {
//        Organization organization = cacheService.entity(Organization.class)
//                .withField("title", org)
//                .getEntity();
//        transferMoneyFromAccountToFolder(organization.name, targetContext, amount);
//    }

    @Step("Перевод со счета {from} на счет {to} суммы {amount} c комментарием {comment}")
    public void transferMoney(String from, String to, String amount, String reason) {
        jsonHelper.getJsonTemplate("/accountManager/transaction.json")
                .set("$.from_account_id", from)
                .set("$.to_account_id", to)
                .set("$.amount", amount)
                .set("$.reason", reason)
                .send(URL)
                .post("accountmanager/api/v1/organizations/vtb/accounts/transfers")
                .assertStatus(200);
    }

    @Step("Запрос текущего баланса для папки {folderId}")
    public Float getCurrentBalance(String folderId) {
        String res = new Http(URL)
                .get(String.format("accountmanager/api/v1/folders/%s/accounts", folderId))
                .assertStatus(200)
                .jsonPath()
                .getString("account.current_balance");
        return Float.valueOf(Objects.requireNonNull(res));
    }

    @Step("Перевод со счета {sourceContext} на счет папки {targetContext} проекта {amount}")
    public void transferMoneyFromFolderToFolder(String sourceContext, String targetContext, String amount) {
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", sourceContext)
                .getEntity();
        transferMoneyFromAccountToFolder(folder.getName(), targetContext, amount);
    }

    private void transferMoneyFromAccountToFolder(String sourceContext, String targetContext, String amount) {
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", targetContext)
                .getEntity();
        String sourceAccountId = getAccountIdByContext(sourceContext);
        String targetAccountId = getAccountIdByContext(folder.getName());
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

    /**
     *
     * @param folderName имя папки
     */
    @Step("Удаление счета для папки {folderName}")
    public void deleteAccount(String folderName) {
        //Получение
        Organization organization = cacheService.entity(Organization.class).getEntity();
        //Получение папки по ее имени
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", folderName)
                .getEntity();
        //Получение счета по ID папки
        Account account = cacheService.entity(Account.class)
                .withField("folderId", folder.getName())
                .getEntity();
        log.info(String.format("Удаление счета %s для папки %s", account.accountId, folder.getName()));
        //Запрос на удаление
        JsonPath jsonPath = new Http(URL)
                .delete(String.format("accountmanager/api/v1/organizations/%s/accounts/%s?force_unlink=1", organization.getName(), account.accountId))
                .assertStatus(200)
                .jsonPath();
        //Выставление флага "Счёт удалён"
//        account.isDeleted = true;
        //Сохранение счёта
        cacheService.saveEntity(account);
    }
}
