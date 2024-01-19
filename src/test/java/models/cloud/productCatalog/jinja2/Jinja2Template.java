package models.cloud.productCatalog.jinja2;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static core.helper.StringUtils.getRandomStringApi;
import static steps.productCatalog.Jinja2Steps.*;
import static steps.productCatalog.TemplateSteps.createTemplateByName;

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
    private String version_changed_by_user;
    private String version;
    @JsonProperty("current_version")
    private String currentVersion;
    private String object_info;
    private String last_version;
    private String version_create_dt;
    private List<String> version_list;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("template_id")
    private Integer templateId;
    @JsonProperty("template_version")
    private String templateVersion;
    @JsonProperty("template_version_pattern")
    private String templateVersionPattern;

    @Override
    public Entity init() {
        if (templateId == null) {
            Template template = createTemplateByName(getRandomStringApi(6));
            templateId = template.getId();
            templateVersion = "";
            templateVersionPattern = "";
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/jinja2/createJinja.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.object_info", object_info)
                .set("$.description", description)
                .set("$.jinja2_template", jinja2Template)
                .set("$.jinja2_data", jinja2Data)
                .set("$.version_changed_by_user", version_changed_by_user)
                .setIfNullRemove("$.version", version)
                .set("$.current_version", currentVersion)
                .set("$.last_version", last_version)
                .set("$.version_create_dt", version_create_dt)
                .set("$.version_list", version_list)
                .set("$.template_id", templateId)
                .set("$.template_version", templateVersion)
                .set("$.template_version_pattern", templateVersionPattern)
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
