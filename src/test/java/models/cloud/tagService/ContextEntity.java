package models.cloud.tagService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import models.Entity;
import org.json.JSONObject;

import java.util.Objects;

@SuperBuilder @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
abstract class ContextEntity extends Entity {
    @JsonIgnore
    String contextType, contextId;

    @Override
    public Entity init() {
        Objects.requireNonNull(contextId, "Не задан contextId");
        if(Objects.isNull(contextType)){
            if(contextId.startsWith("proj"))
                contextType = "projects";
            else if(contextId.startsWith("fold"))
                contextType = "folders";
            else
                contextType = "organizations";
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return serialize();
    }
}
