package httpModels.productCatalog.product.getProducts.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.ItemImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ListItem implements ItemImpl {

    @JsonProperty("allowed_paths")
    private List<Object> allowedPaths;

    @JsonProperty("version_list")
    private List<String> versionList;

    @JsonProperty("is_open")
    private Boolean isOpen;

    @JsonProperty("author")
    private String author;

    @JsonProperty("information_systems")
    private List<Object> informationSystems;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("graph_version")
    private String graphVersion;

    @JsonProperty("description")
    private String description;

    @JsonProperty("envs")
    private List<String> envs;

    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;

    @JsonProperty("graph_id")
    private String graphId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("version")
    private String version;

    @JsonProperty("max_count")
    private Integer maxCount;

    @JsonProperty("last_version")
    private String lastVersion;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("name")
    private String name;

    @JsonProperty("restricted_paths")
    private List<Object> restrictedPaths;

    @JsonProperty("graph_version_pattern")
    private String graphVersionPattern;

    @JsonProperty("allowed_groups")
    private List<Object> allowedGroups;

    @JsonProperty("id")
    private String id;

    @JsonProperty("category")
    private String category;

    @JsonProperty("graph_version_calculated")
    private String graphVersionCalculated;

    @JsonProperty("version_create_dt")
    private String version_create_dt;

    @JsonProperty("version_changed_by_user")
    private String version_changed_by_user;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }
}