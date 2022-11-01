package ui.cloud.pages.productCatalog.enums.action;

public enum ActionType {
    ON("on"),
    OFF("off"),
    DELETE("delete");
    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
