package models.productCatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionDiff {

    @JsonProperty("compare_with_version")
    private String compareWithVersion;
    @JsonProperty("changed_by_user")
    private String changedByUser;
    private LinkedHashMap<String, Object> diff;

}
