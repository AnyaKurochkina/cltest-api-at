package models.t1.imageService;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.t1.imageService.ImageServiceSteps.*;


@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class ImageGroup extends Entity {

    private String id;
    private String name;
    private String title;
    private List<Image> images;
    private List<Object> tags;
    private String distro;
    private String logo;
    @JsonProperty("logo_id")
    private String logoId;
    @JsonProperty("synced_at")
    private String syncedAt;

    @Override
    public Entity init() {
        if (logoId == null) {
            Logo createLogo = Logo.builder().build().createObject();
            logoId = createLogo.getId();
            logo = createLogo.getLogo();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("t1/createImageGroup.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.images", images)
                .set("$.tags", tags)
                .set("$.distro", distro)
                .set("$.synced_at", syncedAt)
                .set("$.logo", logo)
                .set("$.logo_id", logoId)
                .build();
    }

    @Override
    protected void create() {
        if(isImageGroupExist(name, true)) {
            deleteImageGroupByName(name);
        }
        ImageGroup imageGroup = createImageGroup(toJson());
        StringUtils.copyAvailableFields(imageGroup, this);
        assertNotNull(id, "ImageGroup с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteImageGroupById(id);
    }
}
