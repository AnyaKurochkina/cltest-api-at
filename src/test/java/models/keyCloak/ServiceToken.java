package models.keyCloak;

import lombok.Builder;
import models.Entity;
import models.authorizer.ServiceAccount;
import org.json.JSONObject;
import steps.keyCloak.KeyCloakSteps;

@Builder
public class ServiceToken extends Entity {
    public String token;
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
        Service service = Service.builder().build().createObject();
        token = KeyCloakSteps.getNewToken(service);
        time = System.currentTimeMillis() / 1000L;
    }

    @Override
    protected void delete() {}

}
