package ui.cloud.pages;

public class ProductStatus {
    final public static String CREATING = "Разворачивается";
    final public static String PENDING = "Изменение";
    final public static String SUCCESS = "В порядке";
    final public static String DELETING = "Удаляется";
    final public static String ERROR = "Ошибка";
    String status;

    ProductStatus(String status) {
        this.status = status;
    }

    public static boolean isNeedWaiting(String status) {
        return CREATING.equals(status) || PENDING.equals(status) || DELETING.equals(status);
    }

    public static boolean isStatus(String status) {
        switch (status) {
            case CREATING:
            case PENDING:
            case SUCCESS:
            case DELETING:
            case ERROR:
                return true;
        }
        return false;
    }
}
