package models.keyCloak;

import core.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import models.Entity;
import org.json.JSONObject;
import steps.keyCloak.KeyCloakSteps;

@Builder
@Getter
@Setter
public class UserToken extends Entity implements Token {
    String token;
    Long time;
    Role role;

    @Override
    public Entity init() {
        if(role == null){
            role = Role.CLOUD_ADMIN;
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    protected void create() {
        token = KeyCloakSteps.getNewUserToken(role);
        time = System.currentTimeMillis() / 1000L;
    }

    @Override
    protected void delete() {}

}
