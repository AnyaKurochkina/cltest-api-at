package models.keyCloak;

import lombok.Builder;
import models.Entity;
import org.json.JSONObject;
import steps.keyCloak.KeyCloakSteps;

@Builder
public class UserToken extends Entity {
    public String token;
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
        token = KeyCloakSteps.getNewUserToken();
        time = System.currentTimeMillis() / 1000L;
    }

}
