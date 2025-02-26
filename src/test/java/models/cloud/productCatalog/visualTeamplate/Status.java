package models.cloud.productCatalog.visualTeamplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status {
    private String value;
    private String label;
    private String type;

    public Status(String value) {
        this.value = value;
    }
}
