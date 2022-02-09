package models.keyCloak;

import lombok.Builder;
import models.Entity;
import models.authorizer.ServiceAccount;
import org.json.JSONObject;
import steps.keyCloak.KeyCloakSteps;

@Builder
public class ServiceAccountToken extends Entity {
    public String token;
    public String serviceAccountName;
    public Long time;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    protected void create() {
        ServiceAccount serviceAccount = ServiceAccount.builder().id(serviceAccountName).build().createObject();
        token = KeyCloakSteps.getNewServiceAccountToken(serviceAccount);
        time = System.currentTimeMillis() / 1000L;
    }

    @Override
    protected void delete() {}

}
