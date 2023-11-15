package api.cloud.defectolog.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import core.helper.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefectPage {
    private Integer id;
    private Defect defect;
    private Integer pageNumber;
    private List<String> healthy;
    private List<String> patients;
    private List<String> diff;
    private Json extInfo;
}