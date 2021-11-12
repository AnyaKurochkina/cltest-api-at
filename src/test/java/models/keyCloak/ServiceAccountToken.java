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
    public void init() {

    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    protected void create() {
        ServiceAccount serviceAccount = ServiceAccount.builder().name(serviceAccountName).build().createObject();
        token = KeyCloakSteps.getNewServiceAccountToken(serviceAccount);
        time = System.currentTimeMillis() / 1000L;
    }

}
