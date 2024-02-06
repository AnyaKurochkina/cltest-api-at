package models.cloud.productCatalog.template;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static steps.productCatalog.ProductCatalogSteps.getProductCatalogAdmin;
import static steps.productCatalog.TemplateSteps.deleteTemplateById;
import static tests.routes.TemplateProductCatalogApi.apiV1TemplatesCreate;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)

public class Template extends AbstractEntity implements IProductCatalog {

    @JsonProperty("additional_input")
    private Boolean additionalInput;
    private String title;
    private String color;
    @JsonProperty("version_list")
    private List<String> versionList;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("icon_store_id")
    private String iconStoreId;
    @JsonProperty("icon_base64")
    private String iconBase64;
    private String type;
    private String object_info;
    private String description;
    private String run;
    @JsonProperty("priority_can_be_overridden")
    private Boolean priorityCanBeOverridden;
    @JsonProperty("log_can_be_overridden")
    private Boolean logCanBeOverridden;
    private Integer timeout;
    private Map<String, Map<String, String>> output;
    @JsonProperty("coords_x")
    private Integer coordsX;
    @JsonProperty("printed_output")
    private Object printedOutput;
    @JsonProperty("printed_output_can_be_overridden")
    private Boolean printedOutputCanBeOverridden;
    @JsonProperty("restricted_paths")
    private List<Object> restrictedPaths;
    private Integer id;
    @JsonProperty("coords_y")
    private Integer coordsY;
    private Object rollback;
    @JsonProperty("allowed_paths")
    private List<Object> allowedPaths;
    @JsonProperty("log_level")
    private String logLevel;
    @JsonProperty("version_create_dt")
    private String versionCreateDt;
    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;
    private Integer priority;
    private String version;
    private Map<String, Map<String, String>> input;
    @JsonProperty("extra_data")
    private Object extraData;
    @JsonProperty("version_changed_by_user")
    private String versionChangedByUser;
    private String name;
    @JsonProperty("allowed_groups")
    private List<Object> allowedGroups;
    @JsonProperty("additional_output")
    private Boolean additionalOutput;
    @JsonProperty("last_version")
    private String lastVersion;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("current_version")
    private String currentVersion;
    @JsonProperty("tag_list")
    private List<String> tagList;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/templates/createTemplate.json")
                .set("$.name", name)
                .set("$.version", version)
                .set("$.type", type)
                .set("$.title", title)
                .set("$.object_info", object_info)
                .set("$.run", run)
                .set("$.rollback", rollback)
                .set("$.input", input)
                .set("$.output", output)
                .set("$.printed_output", printedOutput)
                .set("$.additional_input", additionalInput)
                .set("$.additional_output", additionalOutput)
                .set("$.printed_output_can_be_overridden", printedOutputCanBeOverridden)
                .set("$.log_level", logLevel)
                .set("$.log_can_be_overridden", logCanBeOverridden)
                .set("$.timeout", timeout)
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .set("$.description", description)
                .set("$.tag_list", tagList)
                .build();
    }

    @Step("Создание шаблона")
    public Template createObject() {
        return getProductCatalogAdmin()
                .body(this.toJson())
                .api(apiV1TemplatesCreate)
                .extractAs(Template.class)
                .deleteMode(Mode.AFTER_TEST);
    }

    @Override
    public void delete() {
        deleteTemplateById(id);
    }

    @Override
    protected int getPrioritise() {
        return 5;
    }
}
