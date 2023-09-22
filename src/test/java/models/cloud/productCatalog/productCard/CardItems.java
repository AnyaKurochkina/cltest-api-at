package models.cloud.productCatalog.productCard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CardItems {
    @JsonProperty("obj_type")
    private String objType;
    @JsonProperty("obj_id")
    private String objId;
    @JsonProperty("obj_keys")
    private ObjectKeys objKeys;
    @JsonProperty("version_arr")
    private List<Integer> versionArr;
    @JsonProperty("dump")
    private Object dump;
    @JsonProperty("version")
    private String version;
    @JsonProperty("is_obj_exists")
    private Boolean isObjExists;
    @JsonProperty("is_obj_version_exists")
    private Boolean isObjVersionExists;
    @JsonProperty("is_obj_equal")
    private Boolean isObjEqual;
}
