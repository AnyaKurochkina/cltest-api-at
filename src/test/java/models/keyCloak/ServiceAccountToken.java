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
    public Entity create() {
        ServiceAccount serviceAccount = ObjectPoolService.create(ServiceAccount.builder()
                        .name(serviceAccountName)
                        .build());
        token = KeyCloakSteps.getNewServiceAccountToken(serviceAccount);
        time = System.currentTimeMillis() / 1000L;
        return this;
    }

}
