package models.keyCloak;

import core.helper.ObjectPoolService;
import lombok.Builder;
import models.Entity;
import models.EntityOld;
import models.authorizer.ServiceAccount;
import steps.keyCloak.KeyCloakSteps;

@Builder
public class ServiceAccountToken extends Entity {
    public String token;
    public String serviceAccountName;
    public Long time;

    @Override
    public void create() {
        ServiceAccount serviceAccount = ServiceAccount.builder().name(serviceAccountName).build().createObject();
        token = KeyCloakSteps.getNewServiceAccountToken(serviceAccount);
        time = System.currentTimeMillis() / 1000L;
    }

}
