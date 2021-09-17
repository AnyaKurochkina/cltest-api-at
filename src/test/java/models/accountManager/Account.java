package models.accountManager;

import lombok.Builder;
import models.EntityOld;

@Builder
public class Account extends EntityOld {
    public String accountId;
    public String folderId;
    @Builder.Default
    public boolean isDeleted = false;
}
