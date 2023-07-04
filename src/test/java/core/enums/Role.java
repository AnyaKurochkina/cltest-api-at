package core.enums;

import com.google.gson.annotations.SerializedName;

public enum Role {
    @SerializedName("product-catalog.viewer")
    PRODUCT_CATALOG_VIEWER("product-catalog.viewer"),

    @SerializedName("roles/admin")
    CLOUD_ADMIN("roles/admin"),

    @SerializedName("roles/notifications.admin")
    NOTIFICATIONS_ADMIN("roles/notifications.admin"),

    @SerializedName("roles/portal.access-group-admin")
    ACCESS_GROUP_ADMIN("roles/portal.access-group-admin"),

    @SerializedName("roles/day2-core.service-manager")
    DAY2_SERVICE_MANAGER("roles/day2-core.service-manager"),

    @SerializedName("roles/accountmanager.transfer-admin")
    ACCOUNT_MANAGER_TRANSFER_ADMIN("roles/accountmanager.transfer-admin"),

    @SerializedName("roles/order-service.admin")
    ORDER_SERVICE_ADMIN("roles/order-service.admin"),

    @SerializedName("tarifficator.admin")
    TARIFFICATOR_ADMIN("tarifficator.admin"),

    @SerializedName("product-catalog.admin")
    PRODUCT_CATALOG_ADMIN("product-catalog.admin"),

    @SerializedName("tag-service.admin")
    TAG_SERVICE_ADMIN("tag-service.admin"),

    @SerializedName("ipam")
    IPAM("ipam"),

    @SerializedName("roles/viewer")
    VIEWER("roles/viewer"),

    @SerializedName("superviewer")
    SUPERVIEWER("superviewer"),

    @SerializedName("superadmin")
    SUPERADMIN("superadmin");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
