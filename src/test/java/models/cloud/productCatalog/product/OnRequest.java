package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OnRequest {
    ONLY_REQUEST("only_request"),
    PREVIEW("preview"),
    TEST("test");
    private final String value;

    OnRequest(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
