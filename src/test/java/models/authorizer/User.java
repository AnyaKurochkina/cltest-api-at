package models.authorizer;

import lombok.Builder;
import models.Entity;
import org.json.JSONObject;

@Builder
public class User extends Entity {
    public String username;
    public String password;

    @Override
    public void init() {

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
