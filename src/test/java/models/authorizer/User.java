package models.authorizer;

import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

@Builder
@Getter
public class User extends Entity {
    String username;
    String password;
    String role;

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
