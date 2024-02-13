package models.cloud.productCatalog.jinja2;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;

import java.util.List;

import static core.helper.StringUtils.getRandomStringApi;
import static steps.productCatalog.Jinja2Steps.deleteJinjaById;
import static steps.productCatalog.ProductCatalogSteps.getProductCatalogAdmin;
import static steps.productCatalog.TemplateSteps.createTemplateByName;
import static tests.routes.Jinja2ProductCatalogApi.apiV1Jinja2TemplatesCreate;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Jinja2Template extends AbstractEntity implements IProductCatalog {

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

    public JSONObject toJson() {
        if (templateId == null) {
            Template template = createTemplateByName(getRandomStringApi(6));
            templateId = template.getId();
            templateVersion = "";
            templateVersionPattern = "";
        }
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

    @Step("Создание шаблона Jinja2")
    public Jinja2Template createObject() {
        return getProductCatalogAdmin()
                .body(this.toJson())
                .api(apiV1Jinja2TemplatesCreate)
                .extractAs(Jinja2Template.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Override
    public void delete() {
        deleteJinjaById(id);
    }

    @Override
    protected int getPriority() {
        return 4;
    }
}
