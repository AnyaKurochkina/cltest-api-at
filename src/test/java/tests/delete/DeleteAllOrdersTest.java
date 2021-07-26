package tests.delete;

import core.exception.CustomException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import steps.Hooks;

import java.io.IOException;

@Order(4)
public class DeleteAllOrdersTest extends Hooks {

    @Test
    @DisplayName("Удаление заказа из проекта")
    public void DeleteOrders() throws IOException, ParseException, CustomException {
        steps.deleteAllOrders.DeleteAllOrders deleteAllOrders = new steps.deleteAllOrders.DeleteAllOrders();
        deleteAllOrders.deleteOrders("proj-aeak5d9285");
    }
}
