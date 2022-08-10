package ui.cloud.pages.productCatalog.enums.graph;

import lombok.Getter;

public enum GraphType {
    ACTION("action"),
    CREATING("creating"),
    SERVICE("service");

    @Getter
    private final String value;

    GraphType(String value) {
        this.value = value;
    }
}
