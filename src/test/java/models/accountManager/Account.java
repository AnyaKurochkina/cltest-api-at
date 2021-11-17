package models.accountManager;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import models.authorizer.Folder;
import models.authorizer.Organization;
import org.json.JSONObject;
import steps.accountManager.AccountSteps;

@Builder
@Getter
public class Account extends Entity {
    public String accountId;
    public String folderId;
    public String parentId;
    public Folder folder;
    public String organization;

    @Override
    public Entity init() {
        if(folder == null) {
            folder = Folder.builder().build().createObject();
            folderId = folder.getName();
        }
        if(folderId == null) {
            folderId = folder.getName();
        }
        if(parentId == null){
            AccountSteps accountSteps = new AccountSteps();
            parentId = accountSteps.getAccountIdByContext(folder.getParentId());
        }
        if(organization == null){
            organization = ((Organization) Organization.builder().build().createObject()).getName();
        }
        return this;
    }

    public JSONObject toJson() {
        return jsonHelper.getJsonTemplate("/accountManager/accountTemplate.json")
                .set("$.parent_id", parentId)
                .set("$.name", String.format("%s (%s)", folder.getTitle(), folderId))
                .set("$.folder_uid", folderId)
                .build();
    }

    @Override
    @Step("Создание счета")
    protected void create() {
        accountId = new Http(Configure.AccountManagerURL)
                .post(String.format("organizations/%s/accounts", organization), toJson())
                .assertStatus(200)
                .jsonPath()
                .getString("account.account_id");
    }

    @Override
    @Step("Удаление счета")
    protected void delete() {
        new Http(Configure.AccountManagerURL)
                .delete(String.format("organizations/%s/accounts/%s?force_unlink=1", organization, accountId))
                .assertStatus(200);
    }
}
