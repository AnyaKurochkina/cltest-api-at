package models.cloud.productCatalog;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Env {
    DEV("dev"),
    TEST("test"),
    PROD("prod"),
    TEST_LT("test_lt"),
    SOME_VALUE("dsfsdfsdf");
    private final String value;

    Env(String value) {
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
