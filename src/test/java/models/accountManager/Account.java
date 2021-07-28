package models.accountManager;

import lombok.Builder;
import models.Entity;

@Builder
public class Account extends Entity {
    public String accountId;
    public String folderId;
    public boolean isDeleted = false;
}
