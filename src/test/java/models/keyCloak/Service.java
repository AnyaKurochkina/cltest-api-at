package models.keyCloak;

import lombok.Builder;
import models.Entity;
import org.json.JSONObject;

@Builder
public class Service extends Entity {
    public String clientId;
    public String clientSecret;

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
