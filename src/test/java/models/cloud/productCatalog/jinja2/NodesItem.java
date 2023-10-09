package models.cloud.productCatalog.jinja2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodesItem {

    @JsonProperty("jinja2template_version_pattern")
    private String jinja2templateVersionPattern;

    @JsonProperty("node_name")
    private String nodeName;

    @JsonProperty("jinja2template_version")
    private String jinja2templateVersion;

    @JsonProperty("jinja2template_version_calculated")
    private String jinja2templateVersionCalculated;
}