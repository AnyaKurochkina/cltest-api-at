package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Payment {
    PAID("paid", "Платный"),
    PARTLY_PAID("partly_paid", "Частично платный"),
    FREE("free", "Бесплатный");

    private final String value;
    @Getter
    private final String displayName;

    Payment(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
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
