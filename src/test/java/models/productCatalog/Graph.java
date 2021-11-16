package models.productCatalog;

import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

@Builder
@Getter
public class Graph extends Entity {

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
