package models.cloud.productCatalog.graph;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.service.Service;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static steps.productCatalog.ActionSteps.deleteActionById;
import static steps.productCatalog.ActionSteps.isActionExists;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.ProductSteps.*;
import static steps.productCatalog.ServiceSteps.*;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Graph extends AbstractEntity implements IProductCatalog {
    @JsonProperty("version_list")
    private List<String> versionList;
    @JsonProperty("author")
    private String author;
    @JsonProperty("version_create_dt")
    private String versionCreateDt;
    @JsonProperty("description")
    private String description;
    @JsonProperty("damage_order_on_error")
    private Boolean damageOrderOnError;
    @JsonProperty("type")
    private String type;
    @JsonProperty("title")
    private String title;
    @JsonProperty("version")
    private String version;
    @JsonProperty("last_version")
    private String lastVersion;
    @JsonProperty("graph")
    private List<GraphItem> graph;
    @JsonProperty("output")
    private Map<String, String> output;
    @JsonProperty("printed_output")
    private Object printedOutput;
    @JsonProperty("json_schema")
    private Map<String, Object> jsonSchema;
    @JsonProperty("version_changed_by_user")
    private String versionChangedByUser;
    @JsonProperty("name")
    private String name;
    @JsonProperty("static_data")
    private Map<String, Object> staticData;
    @JsonProperty("id")
    private String graphId;
    @JsonProperty("ui_schema")
    private Map<String, Object> uiSchema;
    @JsonProperty("modifications")
    private List<Modification> modifications;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("is_sequential")
    private Boolean isSequential;
    @JsonProperty("allowed_developers")
    private List<String> allowedDevelopers;
    @JsonProperty("version_fields")
    private List<String> versionFields;
    @JsonProperty("restricted_developers")
    private List<String> restrictedDevelopers;
    @JsonProperty("current_version")
    private String currentVersion;
    private String object_info;
    @JsonProperty("lock_order_on_error")
    private Boolean lockOrderOnError;
    @JsonProperty("default_item")
    private Object defaultItem;
    @JsonProperty("tag_list")
    private List<String> tagList;

    @SneakyThrows
    public JSONObject toJson() {
        JSONArray mod = null;
        JSONArray nodes = null;
        if (modifications != null) {
            mod = new JSONArray(JsonHelper.getCustomObjectMapper().writeValueAsString(modifications));
        }
        if (graph != null) {
            nodes = new JSONArray(JsonHelper.getCustomObjectMapper().writeValueAsString(graph));
        }
        return JsonHelper.getJsonTemplate("productCatalog/graphs/createGraph.json")
                .set("$.name", name)
                .set("$.object_info", object_info)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.type", type)
                .set("$.author", author)
                .set("$.graph", nodes)
                .set("$.version", version)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .set("$.damage_order_on_error", damageOrderOnError)
                .set("$.lock_order_on_error", lockOrderOnError)
                .set("$.allowed_developers", allowedDevelopers)
                .set("$.restricted_developers", restrictedDevelopers)
                .set("$.tag_list", tagList)
                .setIfNullRemove("$.modifications", mod)
                .setIfNullRemove("$.json_schema", jsonSchema)
                .setIfNullRemove("$.ui_schema", uiSchema)
                .setIfNullRemove("$.static_data", staticData)
                .setIfNullRemove("$.default_item", defaultItem)
                .build();
    }

    public void delete() {
        deleteGraphById(graphId);
    }

    private void deleteIfExist(String name) {
        if (isGraphExists(name)) {
            String id = getGraphByNameFilter(name).getGraphId();
            List<GetUsedListResponse> list = getObjectArrayUsedGraph(id).jsonPath().getList("", GetUsedListResponse.class);
            for (GetUsedListResponse resp : list) {
                String type = resp.getType();
                switch (type) {
                    case ("Action"):
                        if (isActionExists(resp.getName())) {
                            deleteActionById(resp.getId());
                        }
                        break;
                    case ("Product"):
                        if (isProductExists(resp.getName())) {
                            Product product = getProductById(resp.getId());
                            if (product.getIsOpen()) {
                                partialUpdateProduct(product.getProductId(), new JSONObject().put("is_open", false));
                            }
                            deleteProductById(resp.getId());
                        }
                        break;
                    case ("Service"):
                        if (isServiceExists(resp.getName())) {
                            Service getService = getServiceById(resp.getId());
                            if (getService.getIsPublished()) {
                                partialUpdateServiceById(getService.getId(), new JSONObject().put("is_published", false));
                            }
                            deleteServiceById(resp.getId());
                        }
                        break;
                    case ("Graph"):
                        if (isGraphExists(resp.getName())) {
                            deleteIfExist(resp.getName());
                        }
                        break;
                }
            }
            deleteGraphById(id);
        }
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
