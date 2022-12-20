package models.t1.imageService;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.t1.imageService.ImageServiceSteps.createLogo;
import static steps.t1.imageService.ImageServiceSteps.deleteLogoById;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Logo extends Entity {
    private String id;
    private String name;
    @JsonProperty("os_distro")
    private String osDistro;
    private String logo;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("t1/createLogo.json")
                .set("$.name", name)
                .set("$.os_distro", osDistro)
                .set("$.logo", logo)
                .build();
    }

    @Override
    protected void create() {
        Logo logo = createLogo(toJson());
        StringUtils.copyAvailableFields(logo, this);
        assertNotNull(id, "Logo с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteLogoById(id);
    }
}
