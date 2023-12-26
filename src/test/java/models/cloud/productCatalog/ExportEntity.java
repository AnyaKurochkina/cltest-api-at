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
    private Object id;
    private String name;
    private String version;

    public ExportEntity(Object id) {
        this.id = id;
    }

    public ExportEntity(Object id, String name) {
        this.id = id;
        this.name = name;
    }
}
