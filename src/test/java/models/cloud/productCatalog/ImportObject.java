package models.cloud.productCatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ImportObject {

    @JsonProperty("is_pinned_versions")
    private Boolean isPinnedVersions;
    @JsonProperty("model_name")
    private String modelName;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("object_name")
    private String objectName;
    private List<String> messages;
    @JsonProperty("object_id")
    private String objectId;
    private String version;
    private String status;
}