package models.cloud.authorizer;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import java.util.List;

@Log4j2
@Getter
@Builder
public class ProjectPool extends Entity {
    public List<String> id;

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
