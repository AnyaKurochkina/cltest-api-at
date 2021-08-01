package models.orderService.interfaces;

import core.CacheService;
import static org.junit.Assert.*;
import steps.orderService.OrderServiceSteps;

public interface IProduct {
    CacheService cacheService = new CacheService();
    public static String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public String getOrderId();

    public String getProjectId();

    public String getProductName();

    public void order();

    default void reset() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Перезагрузить", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }
    default void stopHard() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Выключить принудительно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    default void stopSoft() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Выключить", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    default void start() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Включить", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    default void delete() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Удалить", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    default void expand_mount_point() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction("Расширить", "{\"size\": 10, \"mount\": \"/app\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore<sizeAfter);
    }

}
