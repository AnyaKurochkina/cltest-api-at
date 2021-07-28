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
public class AccountDelete extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");


    @Step("Удаление счета для папки {folderName}")
    public void deleteAccount(String folderName) {
        Organization organization = cacheService.entity(Organization.class).getEntity();
        Folder folder = cacheService.entity(Folder.class)
                .setField("name", folderName)
                .getEntity();
        Account account = cacheService.entity(Account.class)
                .setField("folderId", folder.id)
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
