package models.productCatalog.graph;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UpdateType {
    UPDATE("update"),
    REPLACE("replace"),
    DELETE("delete");
    private final String value;

    UpdateType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
