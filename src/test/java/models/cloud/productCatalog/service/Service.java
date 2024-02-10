package models.cloud.productCatalog.service;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static steps.productCatalog.ServiceSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Setter
public class Service extends AbstractEntity implements IProductCatalog {

    @JsonProperty("turn_off_inventory")
    private Boolean turnOffInventory;
    @JsonProperty("current_version")
    private String currentVersion;
    @JsonProperty("version_list")
    private List<String> versionList;
    @JsonProperty("is_published")
    private Boolean isPublished;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("icon_store_id")
    private String iconStoreId;
    @JsonProperty("icon_base64")
    private String iconBase64;
    @JsonProperty("description")
    private String description;
    @JsonProperty("service_info")
    private String serviceInfo;
    @JsonProperty("graph_version")
    private String graphVersion;
    @JsonProperty("title")
    private String title;
    @JsonProperty("direction_id")
    private String directionId;
    @JsonProperty("inventory_actions")
    private List<Object> inventoryActions;
    @JsonProperty("graph_version_pattern")
    private String graphVersionPattern;
    @JsonProperty("hide_node_name_output")
    private Boolean hideNodeNameOutput;
    @JsonProperty("id")
    private String id;
    @JsonProperty("start_btn_label")
    private String startBtnLabel;
    @JsonProperty("version_create_dt")
    private String versionCreateDt;
    @JsonProperty("restricted_groups")
    private List<String> restrictedGroups;
    @JsonProperty("graph_id")
    private String graphId;
    @JsonProperty("version")
    private String version;
    @JsonProperty("data_source")
    private Object dataSource;
    @JsonProperty("check_rules")
    private List<Object> checkRules;
    @JsonProperty("auto_open_form")
    private Boolean autoOpenForm;
    @JsonProperty("last_version")
    private String lastVersion;
    @JsonProperty("extra_data")
    private Object extraData;
    @JsonProperty("version_changed_by_user")
    private String versionChangedByUser;
    @JsonProperty("name")
    private String name;
    @JsonProperty("allowed_groups")
    private List<String> allowedGroups;
    @JsonProperty("version_fields")
    private List<String> versionFields;
    @JsonProperty("graph_version_calculated")
    private String graphVersionCalculated;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("auto_open_results")
    private Boolean autoOpenResults;
    @JsonProperty("direction_title")
    private String directionTitle;
    @JsonProperty("allowed_developers")
    private List<String> allowedDevelopers;
    @JsonProperty("restricted_developers")
    private List<String> restrictedDevelopers;
    @JsonProperty("direction_name")
    private String directionName;

    public JSONObject toJson() {
        if (directionId == null) {
            OrgDirection orgDirection = OrgDirection.builder()
                    .name("direction_for_services_api_test")
                    .title("test_api")
                    .build()
                    .createObject();
            directionId = orgDirection.getId();
        }
        return JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                .set("$.name", name)
                .set("$.graph_id", graphId)
                .set("$.graph_version", graphVersion)
                .set("$.version", version)
                .set("$.is_published", isPublished)
                .set("$.title", title)
                .set("$.current_version", currentVersion)
                .set("$.auto_open_results", autoOpenResults)
                .set("$.direction_id", directionId)
                .set("$.direction_name", directionName)
                .set("$.service_info", serviceInfo)
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .setIfNullRemove("$.start_btn_label", startBtnLabel)
                .set("$.description", description)
                .set("$.restricted_groups", restrictedGroups)
                .set("$.allowed_groups", allowedGroups)
                .build();
    }

    public Service createObject() {
        if (isServiceExists(name)) {
            deleteServiceByName(name);
        }
        return createService(toJson())
                .extractAs(Service.class)
                .deleteMode(Mode.AFTER_TEST);
    }

    @Override
    public void delete() {
        if (isPublished) {
            partialUpdateServiceByName(name, new JSONObject().put("is_published", false));
        }
        deleteServiceById(id);
        Assertions.assertFalse(isServiceExists(name));
    }

    protected int getPriority() {
        return 0;
    }
}
