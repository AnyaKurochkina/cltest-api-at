package models.cloud.productCatalog.visualTeamplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Type {
    private String value;
    private String label;

    public Type(String value) {
        this.value = value;
    }
}
