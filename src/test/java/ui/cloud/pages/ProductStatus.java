package ui.cloud.pages;

public class ProductStatus {
    final static String CREATING = "Разворачивается";
    final static String PENDING = "Изменение";
    final static String SUCCESS = "В порядке";
    final static String DELETING = "Удаляется";
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
