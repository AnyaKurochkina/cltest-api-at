package steps;

import core.helper.Configurier;
import core.helper.Http;
import core.helper.ShareData;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static core.helper.JsonHelper.shareData;

@Log4j2
public class AccountManager extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");


    @Step("Перевод со счета {sourceContext} на счет папки {targetContext} проекта {amount}")
    public void transferMoneyFromAccountToFolder(String sourceContext, String targetContext, String amount) throws IOException, ParseException {
        String sourceAccountId = getAccountIdByContext(sourceContext);
        String targetAccountId = getAccountIdByContext(ShareData.getString(targetContext));
        System.out.println("SourceAccountId = " + sourceAccountId);
        System.out.println("targetAccountId = " + targetAccountId);
        log.info("Отправка запроса на перевод денег с счета папки " + sourceContext + " на счет " + ShareData.getString(targetContext));

        jsonHelper.getJsonTemplate("/accountManager/transaction.json")
                .set("$.from_account_id", sourceAccountId)
                .set("$.to_account_id", targetAccountId)
                .set("$.amount", amount)
                .set("$.reason", "Перевод в рамках тестирования на сумму " + amount)
                .send(URL)
                .post("accountmanager/api/v1/organizations/vtb/accounts/transfers")
                .assertStatus(200);
    }



    @Step("Создание счета для папки {folderId} для {pathToParentFolder}")
    public void createAccount(String folderId, String pathToParentFolder) {
        log.info("Изменение базового шаблона запроса при создании папки с типом: " + folderId);
        String name = testVars.getVariable("businessBlockTitle") + " (" + ShareData.getString(folderId) + ")";

        String accountId = jsonHelper.getJsonTemplate("/accountmanager/accountTemplate.json")
                .set("$.parent_id", getAccountIdByContext(pathToParentFolder))
                .set("$.name", name)
                .send(URL)
                .post("accountmanager/api/v1/organizations/vtb/accounts")
                .assertStatus(200)
                .jsonPath()
                .get("account.account_id");

        testVars.setVariables("AccountId", accountId);
    }

    @Step("Связка счета с папкой {folderId}")
    public void linkAccountWithFolder(String folderId){
        log.info(String.format("Account ID of the last folder: %s",testVars.getVariable("AccountId")));
        log.info(String.format("folderId : %s", ShareData.getString(folderId)));

        new Http(URL)
                .post("accountmanager/api/v1/folders/" + ShareData.getString(folderId) + "/accounts/" + testVars.getVariable("AccountId"))
                .assertStatus(200);
    }

    @Step("дебаг")
    public void debug(){
        try{Thread.sleep(5000);}catch (Exception ex){ex.printStackTrace();}
    }

    public String getAccountIdByContext(String context) {
        log.info("Получение account_id для контекста - " + context);
        return new Http(URL)
                .get("accountmanager/api/v1/organizations/vtb/accounts")
                .jsonPath().get("list.find{it.name.contains('" + context.toLowerCase() + "')}.account_id");
    }
}
