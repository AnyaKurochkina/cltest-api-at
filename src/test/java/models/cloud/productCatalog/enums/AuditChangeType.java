package models.cloud.productCatalog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditChangeType {

    CREATE("create"),
    UPDATE("update"),
    DELETE("delete");

    private final String value;
}
