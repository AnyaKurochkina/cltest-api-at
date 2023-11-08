package api.cloud.defectolog.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.cloud.tagService.Inventory;

import java.util.List;
import java.util.Map;

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
    private Map<String, Object> extInfo;
}