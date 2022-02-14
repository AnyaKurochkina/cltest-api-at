package models.keyCloak;

import core.enums.Role;
import lombok.Builder;
import models.Entity;
import org.json.JSONObject;
import steps.keyCloak.KeyCloakSteps;

@Builder
public class UserToken extends Entity {
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
        token = KeyCloakSteps.getNewUserToken(Role.ADMIN);
        time = System.currentTimeMillis() / 1000L;
    }

    @Override
    protected void delete() {}

}
