package models.cloud.productCatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportData {
    @JsonProperty("export_objects_data")
    private List<ExportEntity> list;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/exportEntity.json")
                .set("$.export_objects_data", list)
                .build();
    }
}
