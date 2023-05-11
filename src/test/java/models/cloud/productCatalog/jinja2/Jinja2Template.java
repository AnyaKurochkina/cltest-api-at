package models.cloud.productCatalog.jinja2;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import static steps.productCatalog.Jinja2Steps.*;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Jinja2Template extends Entity implements IProductCatalog {

    @JsonProperty("jinja2_template")
    private String jinja2Template;
    @JsonProperty("jinja2_data")
    private Object jinja2Data;
    private String name;
    private String description;
    private String id;
    private String title;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/jinja2/createJinja.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.jinja2_template", jinja2Template)
                .set("$.jinja2_data", jinja2Data)
                .build();
    }

    @Override
    @Step("Создание шаблона Jinja2")
    protected void create() {
        if (isJinja2Exists(name)) {
            deleteJinjaByName(name);
        }
        Jinja2Template jinja2 = createJinja(toJson());
        StringUtils.copyAvailableFields(jinja2, this);
        Assertions.assertNotNull(id, "Jinja с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление jinja2")
    protected void delete() {
        deleteJinjaById(id);
        Assertions.assertFalse(isJinja2Exists(name));
    }
}
