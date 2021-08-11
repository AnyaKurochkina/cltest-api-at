package models.orderService.interfaces;

import static org.junit.Assert.*;
import lombok.Getter;
import lombok.ToString;
import models.Entity;
import steps.orderService.OrderServiceSteps;

import java.util.Map;

@ToString(onlyExplicitlyIncluded = true)
public abstract class IProduct extends Entity {
    public static String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public static String CPUS = "data.find{it.type=='vm'}.config.flavor.cpus";
    public static String MEMORY = "data.find{it.type=='vm'}.config.flavor.memory";
    public static String KAFKA_CLUSTER_TOPIC = "data.find{it.type=='cluster'}.config.topics.any{it.topic_name=='%s'}";
    public static String KAFKA_CLUSTER_ACL = "data.find{it.type=='cluster'}.config.acls.any{it.topic_name=='%s'}";

    protected transient OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @Getter
    protected String orderId;
    @Getter
    protected String projectId;
    @Getter
    protected String productName;
    @Getter
    @ToString.Include
    protected String env;
    @Getter
    protected String productId;

    public abstract void order();

    public void runActionsBeforeOtherTests(){
        expandMountPoint();
        restart();
        stopSoft();
        resize();
        start();
        stopHard();
    }
    public void runActionsAfterOtherTests() {
        delete();
    }

    public void restart() {
        String actionId = orderServiceSteps.executeAction("Перезагрузить", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }
    public void stopHard() {
        String actionId = orderServiceSteps.executeAction("Выключить принудительно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void stopSoft() {
        String actionId = orderServiceSteps.executeAction("Выключить", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void start() {
        String actionId = orderServiceSteps.executeAction("Включить", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void delete() {
        String actionId = orderServiceSteps.executeAction("Удалить", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void resize() {
        Map<String, String> map = orderServiceSteps.getFlavorByProduct(this);
        String actionId = orderServiceSteps.executeAction("Изменить конфигурацию", map.get("flavor"), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int cpusAfter = (Integer) orderServiceSteps.getFiledProduct(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getFiledProduct(this, MEMORY);
        assertEquals(Integer.parseInt(map.get("cpus")), cpusAfter);
        assertEquals(Integer.parseInt(map.get("memory")), memoryAfter);
    }

    public void expandMountPoint() {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction("Расширить", "{\"size\": 10, \"mount\": \"/app\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore<sizeAfter);
    }

}
