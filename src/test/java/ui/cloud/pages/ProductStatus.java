package ui.cloud.pages;

class ProductStatus {
    final static String CREATING = "Разворачивается";
    final static String PENDING = "Изменение";
    final static String SUCCESS = "В порядке";
    final static String DELETING = "Удаляется";
    final static String ERROR = "Ошибка";

    String status;

    ProductStatus(String status) {
        this.status = status;
    }

    public static boolean isNeedWaiting(String status) {
        return CREATING.equals(status) || PENDING.equals(status) || DELETING.equals(status);
    }
}
