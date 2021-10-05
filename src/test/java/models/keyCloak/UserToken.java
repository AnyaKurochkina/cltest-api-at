package models.keyCloak;

import core.helper.ObjectPoolService;
import lombok.Builder;
import models.Entity;
import models.EntityOld;
import models.authorizer.ServiceAccount;
import steps.keyCloak.KeyCloakSteps;

@Builder
public class UserToken extends Entity {
    public String token;
    public Long time;

    @Override
    public void create() {
        token = KeyCloakSteps.getNewUserToken();
        time = System.currentTimeMillis() / 1000L;
    }

}
