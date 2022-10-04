package models.t1;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.t1.imageService.ImageServiceSteps.createImageGroup;
import static steps.t1.imageService.ImageServiceSteps.deleteImageGroupById;


@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class ImageGroups extends Entity {

    private String id;
    private String name;
    private String title;
    private List<Image> images;
    private List<Object> tags;
    private String distro;
    private String logo;
    @JsonProperty("synced_at")
    private String syncedAt;
    private String jsonTemplate;

    @Override
    public Entity init() {
        jsonTemplate = "t1/createImageGroup.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.images", images)
                .set("$.tags", tags)
                .set("$.distro", distro)
                .set("$.synced_at", syncedAt)
                .build();
    }

    @Override
    protected void create() {
        ImageGroups imageGroup = createImageGroup(toJson());
        StringUtils.copyAvailableFields(imageGroup, this);
        assertNotNull(id, "ImageGroup с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteImageGroupById(id);
    }
}
