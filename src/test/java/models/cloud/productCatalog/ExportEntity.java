package models.cloud.productCatalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportEntity {
    private String id;
    private String version;

    public ExportEntity(String id) {
        this.id = id;
    }
}
