package ui.cloud.pages.orders;

import ui.elements.Table;

public class OrdersPage extends Table {
    public OrdersPage() {
        super("Продукт");
    }

    public void openOrder(String label) {
        getRowByColumnValue("Продукт", label).getElementByColumn("Продукт").click();
    }
}
