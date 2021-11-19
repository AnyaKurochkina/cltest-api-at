package httpModels.productCatalog.createService.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateServiceResponse {

    @JsonProperty("allowed_paths")
    private List<Object> allowedPaths;

    @JsonProperty("is_published")
    private Boolean isPublished;

    @JsonProperty("icon")
    private Object icon;

    @JsonProperty("graph_version")
    private String graphVersion;

    @JsonProperty("description")
    private String description;

    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;

    @JsonProperty("graph_id")
    private String graphId;

    @JsonProperty("data_source")
    private DataSource dataSource;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("direction_id")
    private String directionId;

    @JsonProperty("extra_data")
    private ExtraData extraData;

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
}