package models.cloud.productCatalog.enums;

import lombok.Getter;

@Getter
public enum LogLevel {
    SHORT("short", "Ограниченное"),
    FULL("full", "Расширенное"),
    EMPTY("", "Отсутствует");

    private final String value;
    private final String displayName;

    LogLevel(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return value;
    }
}