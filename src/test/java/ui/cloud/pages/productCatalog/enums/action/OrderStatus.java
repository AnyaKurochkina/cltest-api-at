package ui.cloud.pages.productCatalog.enums.action;

public enum OrderStatus {
    NEW("new"),
    PENDING("pending"),
    SUCCESS("success"),
    DAMAGED("damaged"),
    MAINTENANCE("maintenance"),
    REMOVING("removing"),
    DEPROVISIONED("deprovisioned"),
    DEPROVISIONED_ERROR("deprovisioned_error"),
    CHANGING("changing"),
    FAILURE("failure"),
    SCHEDULED("scheduled"),
    CANCELED("canceled"),
    WARNING("warning"),
    LOCKED("locked");
    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

}
