package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Payment {
    PAID("paid", "Платный"),
    PARTLY_PAID("partly_paid", "Частично платный"),
    FREE("free", "Бесплатный");

    private final String value;
    @Getter
    private final String title;

    Payment(String value, String title) {
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
