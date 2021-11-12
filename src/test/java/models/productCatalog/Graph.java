package models.productCatalog;

import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

@Builder
@Getter
public class Graph extends Entity {

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    protected void create() {

    }
}
