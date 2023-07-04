package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.productCatalog.ContextRestrictionsItem;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.productCatalog.ProductSteps.*;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Product extends Entity {

    @JsonProperty("is_open")
    private Boolean isOpen;
    @JsonProperty("version_list")
    private List<String> versionList;
    private String author;
    @JsonProperty("current_version")
    private String currentVersion;
    @JsonProperty("information_systems")
    private List<Object> informationSystems;
    @JsonProperty("icon_store_id")
    private String iconStoreId;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("icon_base64")
    private String iconBase64;
    @JsonProperty("version_create_dt")
    private String versionCreateDt;
    private String description;
    @JsonProperty("graph_version")
    private String graphVersion;
    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;
    private String title;
    @JsonProperty("graph_id")
    private String graphId;
    private String version;
    @JsonProperty("max_count")
    private int maxCount;
    private Integer number;
    @JsonProperty("version_changed_by_user")
    private String versionChangedByUser;
    private String name;
    @JsonProperty("restricted_paths")
    private List<Object> restrictedPaths;
    @JsonProperty("allowed_groups")
    private List<Object> allowedGroups;
    @JsonProperty("graph_version_pattern")
    private String graphVersionPattern;
    @JsonProperty("id")
    private String productId;
    @JsonProperty("graph_version_calculated")
    private String graphVersionCalculated;
    private String category;
    @JsonProperty("category_v2")
    private Categories categoryV2;
    @JsonProperty("on_request")
    private OnRequest onRequest;
    @JsonProperty("last_version")
    private String lastVersion;
    private Map<String, String> info;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("extra_data")
    private Map<String, String> extraData;
    @JsonProperty("in_general_list")
    private Boolean inGeneralList;
    @JsonProperty("allowed_developers")
    private List<String> allowedDevelopers;
    @JsonProperty("restricted_developers")
    private List<String> restrictedDevelopers;
    @JsonProperty("context_restrictions")
    private List<ContextRestrictionsItem> contextRestrictions;
    @JsonProperty("version_fields")
    private List<String> versionFields;
    private Object envs;
    @JsonProperty("tag_list")
    private List<String> tagList;
    @JsonProperty("skip_request_resource_pools")
    private Boolean skipRequestResourcePools;
    @JsonProperty("skip_reservation")
    private Boolean skipReservation;
    @JsonProperty("skip_validate_checker")
    private Boolean skipValidateChecker;
    @JsonProperty("skip_restriction_service")
    private Boolean skipRestrictionService;

    @Override
    public Entity init() {
        if (graphId == null) {
            Graph graph = Graph.builder().name("graph_for_product_api_test").build().createObject();
            graphId = graph.getGraphId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        String categoryV2 = null;
        String onRequest = null;
        if (this.categoryV2 != null) {
            categoryV2 = this.categoryV2.getValue();
        }
        if (this.onRequest != null) {
            onRequest = this.onRequest.getValue();
        }
        return JsonHelper.getJsonTemplate("productCatalog/products/createProduct.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.graph_id", graphId)
                .set("$.graph_version", graphVersion)
                .set("$.version", version)
                .set("$.category", category)
                .set("$.info", info)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .set("$.icon_url", iconUrl)
                .set("$.is_open", isOpen)
                .set("$.current_version", currentVersion)
                .set("$.extra_data", extraData)
                .set("$.information_systems", informationSystems)
                .set("$.in_general_list", inGeneralList)
                .setIfNullRemove("$.category_v2", categoryV2)
                .setIfNullRemove("$.on_request", onRequest)
                .setIfNullRemove("$.number", number)
                .set("$.allowed_groups", allowedGroups)
                .set("$.restricted_groups", restrictedGroups)
                .set("$.context_restrictions", contextRestrictions)
                .set("$.description", description)
                .set("$.author", author)
                .set("$.max_count", maxCount)
                .set("$.tag_list", tagList)
                .build();
    }

    @Override
    protected void create() {
        if (isProductExists(name)) {
            deleteProductByName(name);
        }
        Product createProduct = createProduct(toJson());
        StringUtils.copyAvailableFields(createProduct, this);
        Assertions.assertNotNull(productId, "Продукт с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        Product product = getProductById(productId);
        if (product.isOpen) {
            partialUpdateProduct(productId, new JSONObject().put("is_open", false));
        }
        deleteProductById(productId);
        assertFalse(isProductExists(name));
    }
}