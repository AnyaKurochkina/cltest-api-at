package models.cloud.productCatalog.template;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.template.createTemplate.response.CreateTemplateResponse;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import httpModels.productCatalog.template.getTemplate.response.Input;
import httpModels.productCatalog.template.getTemplate.response.Output;
import httpModels.productCatalog.template.getTemplate.response.PrintedOutput;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

@Log4j2
@Builder
@Getter

public class Template extends Entity implements IProductCatalog {

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
    private String type;
    private String description;
    private String run;
    @JsonProperty("priority_can_be_overridden")
    private Boolean priorityCanBeOverridden;
    @JsonProperty("log_can_be_overridden")
    private Boolean logCanBeOverridden;
    private Integer timeout;
    private Output output;
    @JsonProperty("coords_x")
    private Integer coordsX;
    @JsonProperty("printed_output")
    private PrintedOutput printedOutput;
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
    private Object logLevel;
    @JsonProperty("version_create_dt")
    private String versionCreateDt;
    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;
    private Integer priority;
    private String version;
    private Input input;
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

    public static final String productName = "/api/v1/templates/";

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/templates/createTemplate.json")
                .set("$.name", name)
                .set("$.version", version)
                .set("$.type", type)
                .set("$.title", title)
                .set("$.run", run)
                .set("$.rollback", rollback)
                .set("$.input", input)
                .set("$.output", output)
                .set("$.timeout", timeout)
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .set("$.description", description)
                .build();
    }

    @Override
    @Step("Создание шаблона")
    protected void create() {
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, name);
        if (productCatalogSteps.isExists(name)) {
            productCatalogSteps.deleteByName(name, GetTemplateListResponse.class);
        }
        CreateTemplateResponse createTemplateResponse = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateTemplateResponse.class);
        id = createTemplateResponse.getId();
        Assertions.assertNotNull(id, "Шаблон с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
         new Http(ProductCatalogURL)
                 .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + id + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, "productCatalog/templates/createTemplate.json");
        Assertions.assertFalse(productCatalogSteps.isExists(name));
    }
}
