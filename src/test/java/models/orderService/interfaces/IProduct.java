package models.orderService.interfaces;

import core.CacheService;
import static org.junit.Assert.*;
import steps.orderService.OrderServiceSteps;

import java.util.Map;

public interface IProduct {
    CacheService cacheService = new CacheService();

    public static String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public static String CPUS = "data.find{it.type=='vm'}.config.flavor.cpus";
    public static String MEMORY = "data.find{it.type=='vm'}.config.flavor.memory";

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
        int cpusAfter = (Integer) orderServiceSteps.getFiledProduct(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getFiledProduct(this, MEMORY);
        assertEquals(Integer.parseInt(map.get("cpus")), cpusAfter);
        assertEquals(Integer.parseInt(map.get("memory")), memoryAfter);
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
