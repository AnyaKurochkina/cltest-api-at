package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum OnRequest {
    ONLY_REQUEST("only_request", "Готовый продукт"),
    PREVIEW("preview", "Продукт в разработке"),
    TEST("test", "Тест");

    private final String value;
    @Getter
    private final String title;

    OnRequest(String value, String title) {
        this.value = value;
        this.title = title;
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
