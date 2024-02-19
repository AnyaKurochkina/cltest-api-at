package models.cloud.references;

import core.helper.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.AbstractEntity;
import org.json.JSONObject;

import static steps.references.ReferencesStep.deletePrivateDirectoryByName;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Directories extends AbstractEntity {
    private String id;
    private String name;
    private String description;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("references/createDirectory.json")
                .set("name", name)
                .set("description", description)
                .build();
    }

    @Override
    public String toString() {
        return String.format("{\"id\": %s,\"name\": %s,\"description\": %s}", id, name, description);
    }

    @Override
    public void delete() {
        deletePrivateDirectoryByName(name);
    }
}
