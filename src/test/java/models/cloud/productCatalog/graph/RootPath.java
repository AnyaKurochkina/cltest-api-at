package models.cloud.productCatalog.graph;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RootPath {
    UI_SCHEMA("ui_schema"),
    JSON_SCHEMA("json_schema"),
    STATIC_DATA("static_data");
    private final String value;

    RootPath(String value) {
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
