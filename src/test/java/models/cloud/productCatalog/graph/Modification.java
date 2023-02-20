package models.cloud.productCatalog.graph;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.cloud.productCatalog.Env;
import org.json.JSONObject;

import java.util.List;

@Log4j2
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Modification {
    String name;
    Object data;
    List<Env> envs;
    @JsonProperty("env_names")
    List<String> envNames;
    Integer order;
    String path;
    @JsonProperty("root_path")
    RootPath rootPath;
    @JsonProperty("update_type")
    UpdateType updateType;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/graphs/mod.json")
                .set("$.name", name)
                .set("$.data", data)
                .set("$.envs", envs)
                .set("$.env_names", envNames)
                .set("$.order", order)
                .set("$.path", path)
                .set("$.root_path", rootPath)
                .set("$.updateType", updateType)
                .build();
    }
}
