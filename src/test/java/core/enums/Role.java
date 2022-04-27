package core.enums;

import com.google.gson.annotations.SerializedName;

public enum Role {
    @SerializedName("admin")
    ADMIN("admin"),

    @SerializedName("order-service.admin")
    ORDER_SERVICE_ADMIN("order-service.admin"),

    @SerializedName("viewer")
    VIEWER("viewer"),

    @SerializedName("t1.admin")
    T1ADMIN("roles/admin");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
