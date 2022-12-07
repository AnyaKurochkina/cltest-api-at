package models.cloud.productCatalog.visualTeamplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullTemplate {

    private String type;
    private String name;
    private List<Object> value;
}
