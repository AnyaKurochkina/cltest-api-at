package models.cloud.keyCloak;

import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

@Builder
@Getter
public class Service extends Entity implements KeyCloakClient {
    public String id;
    public String secret;

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
