package models.productCatalog.graph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedHashMap;
import java.util.List;

@Log4j2
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Modification {
    String name;
    LinkedHashMap<String, Object> data;
    List<Env> envs;
    Integer order;
    String path;
    @JsonProperty("root_path")
    RootPath rootPath;
    @JsonProperty("update_type")
    UpdateType updateType;
}
