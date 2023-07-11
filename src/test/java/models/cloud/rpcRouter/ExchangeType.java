package models.cloud.rpcRouter;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExchangeType {
    DIRECT("direct"),
    TOPIC("topic"),
    FANOUT("fanout");

    private final String value;

    ExchangeType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

