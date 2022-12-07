package models.cloud.productCatalog.visualTeamplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Name {
    private String value;
    private String label;
    private String type;

    public Name(String value) {
        this.value = value;
    }
}
