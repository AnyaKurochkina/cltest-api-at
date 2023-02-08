package models.cloud.productCatalog.visualTeamplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Type {
    private String value;
    private String label;

    public Type(String value) {
        this.value = value;
    }
}
