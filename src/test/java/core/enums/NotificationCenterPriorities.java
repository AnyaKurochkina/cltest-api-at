package core.enums;

public enum NotificationCenterPriorities {

    //************Приоритеты*****************
    HIGH("Высокий", "HIGH"),
    COMMON("Средний", "COMMON"),
    LOW("Низкий", "LOW"),
    //***********Каналы**********************
    LENTA("Лента", "lenta"),
    WS("Колокольчик", "ws"),
    EMAIL("Почта", "email");


    private final String uiName;
    private final String backName;


    NotificationCenterPriorities(String uiName, String backName) {
        this.uiName = uiName;
        this.backName = backName;
    }

    public String getUiName() {
        return uiName;
    }

    public String getBackName() {
        return backName;
    }
}
