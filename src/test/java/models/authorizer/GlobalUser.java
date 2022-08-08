package models.authorizer;

import core.enums.Role;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

@Builder
@Getter
public class GlobalUser extends Entity {
    String username;
    String password;
    String email;
    Role role;

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
    }

    @Override
    protected void delete() {

    }
}
