package models.accountManager;

import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import models.authorizer.Folder;
import models.authorizer.Organization;
import org.json.JSONObject;
import steps.accountManager.AccountSteps;

import java.util.Objects;

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
            parentId = AccountSteps.getAccountIdByContext(folder.getParentId());
            if(parentId == null) {
                Account account = Account.builder().folder(Folder.getParent(folder)).build().createObject();
                parentId = account.getAccountId();
            }
        }
        if(organization == null){
            organization = ((Organization) Organization.builder().build().createObject()).getName();
        }
        return this;
    }

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("/accountManager/accountTemplate.json")
                .set("$.parent_id", Objects.requireNonNull(parentId))
                .set("$.name", String.format("%s (%s)", folder.getTitle(), folderId))
                .set("$.folder_uid", folderId)
                .build();
    }

    @Override
    @Step("Создание счета")
    protected void create() {
        accountId = new Http(Configure.AccountManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(toJson())
                .post(String.format("/api/v1/organizations/%s/accounts", organization))
                .assertStatus(200)
                .jsonPath()
                .getString("account.account_id");
    }

    @Override
    @Step("Удаление счета")
    protected void delete() {
        new Http(Configure.AccountManagerURL)
                .setRole(Role.ACCOUNT_MANAGER_TRANSFER_ADMIN)
                .delete(String.format("/api/v1/organizations/%s/accounts/%s?force_unlink=1", organization, accountId))
                .assertStatus(200);
    }
}
