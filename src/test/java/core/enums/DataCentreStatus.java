package core.enums;

public enum DataCentreStatus {

    DELETING("Удаляется"),
    DELETED("Удалено"),
    PROCESSING("Разворачивается"),
    READY("В порядке")
    ;

    public String getStatus() {
        return role;
    }

    private final String role;

    DataCentreStatus(String role) {
        this.role = role;
    }

}
