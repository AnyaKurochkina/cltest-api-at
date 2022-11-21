package models.t1;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

@Builder
@Getter
@Setter
@Log4j2
@ToString(onlyExplicitlyIncluded = true)
public class Project extends Entity {
    @ToString.Include
    String name;
    String title;

    @Override
    public Entity init() {
        return null;
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
