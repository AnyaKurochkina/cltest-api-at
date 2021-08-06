package models.orderService.interfaces;

import core.CacheService;
import static org.junit.Assert.*;
import steps.orderService.OrderServiceSteps;

import java.util.Map;

public interface IProduct {
    CacheService cacheService = new CacheService();

    public static String RABBITMQ_USER = "data.find{it.type=='cluster'}.config.users[0]";
    public static String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public static String CPUS = "data.find{it.type=='vm'}.config.flavor.cpus";
    public static String MEMORY = "data.find{it.type=='vm'}.config.flavor.memory";
    public static String KAFKA_CLUSTER_TOPIC = "data.find{it.type=='cluster'}.config.topics.any{it.topic_name=='%s'}";
    public static String KAFKA_CLUSTER_ACL = "data.find{it.type=='cluster'}.config.acls.any{it.topic_name=='%s'}";

    public String getOrderId();

    public String getProjectId();

    public String getProductName();

    public String getEnv();

    public String getProductId();

    public void order();

    default void restart() {
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

    default void rabbitmq_create_user() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String user = "testapiuser";
        String actionId = orderServiceSteps.executeAction("Создать пользователя RabbitMQ", String.format("{rabbitmq_users: [{user: \"%s\", password: \"%s\"}]}", user, user), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String username = (String) orderServiceSteps.getFiledProduct(this, RABBITMQ_USER);
        assertEquals(user, username);
    }

}
