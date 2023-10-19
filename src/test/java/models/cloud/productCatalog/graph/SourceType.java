package models.cloud.productCatalog.graph;

import lombok.Getter;

@Getter
public enum SourceType {
    TEMPLATE("template", "Шаблон"),
    SUBGRAPH("subgraph", "Подграф"),
    JINJA2_TEMPLATE("jinja2_template", "Шаблон Jinja2");

    private final String value;
    private final String displayName;

    SourceType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return value;
    }
}