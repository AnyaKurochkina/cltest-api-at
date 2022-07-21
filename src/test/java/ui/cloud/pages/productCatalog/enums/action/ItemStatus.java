package ui.cloud.pages.productCatalog.enums.action;

public enum ItemStatus {
    ON("on"),
    OFF("off"),
    DELETED("deleted"),
    REBOOT("reboot"),
    PROBLEM("problem");
    private final String value;

    ItemStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
