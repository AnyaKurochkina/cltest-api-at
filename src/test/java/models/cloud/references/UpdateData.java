package models.cloud.references;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateData {
    private String directory;
    private String page;
    @JsonProperty("update_data")
    private LinkedHashMap<String, String> updateData;
}
