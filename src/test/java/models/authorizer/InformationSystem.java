package models.authorizer;

import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

@Builder
@Getter
public class InformationSystem extends Entity {
    public String id;
    public Boolean isForOrders;

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
}
