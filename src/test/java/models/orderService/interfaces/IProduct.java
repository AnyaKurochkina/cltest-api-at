package models.orderService.interfaces;

import core.CacheService;
import static org.junit.Assert.*;
import steps.orderService.OrderServiceSteps;

import java.util.Map;

public interface IProduct {
    CacheService cacheService = new CacheService();

    public String getOrderId();

    public String getProjectId();

    public String getProductName();

    public String getEnv();

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

    default void resize() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        Map<String, String> map = orderServiceSteps.getFlavorByProduct(this);
        String actionId = orderServiceSteps.executeAction("Изменить конфигурацию", map.get("flavor"), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    default void expand_mount_point() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        int sizeBefore = orderServiceSteps.getExpandMountSize(this);
        String actionId = orderServiceSteps.executeAction("Расширить", "{\"size\": 10, \"mount\": \"/app\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = orderServiceSteps.getExpandMountSize(this);
        assertTrue(sizeBefore<sizeAfter);
    }

}
