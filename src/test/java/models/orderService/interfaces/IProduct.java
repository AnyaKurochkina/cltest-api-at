package models.orderService.interfaces;

import static org.junit.Assert.*;

import lombok.Getter;
import lombok.ToString;
import models.Entity;
import org.json.JSONObject;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@ToString(onlyExplicitlyIncluded = true)
public abstract class IProduct extends Entity {
    public static String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public static String CPUS = "data.find{it.type=='vm'}.config.flavor.cpus";
    public static String MEMORY = "data.find{it.type=='vm'}.config.flavor.memory";
    public static String KAFKA_CLUSTER_TOPIC = "data.find{it.type=='cluster'}.config.topics.any{it.topic_name=='%s'}";
    public static String KAFKA_CLUSTER_ACL = "data.find{it.type=='cluster'}.config.acls.any{it.topic_name=='%s'}";

    protected transient OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
    protected transient String jsonTemplate;

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

    public abstract void init();

    public abstract JSONObject getJsonParametrizedTemplate();


//    protected void callAction(String func) {
//        Method method = null;
//        try {
//            method = this.getClass().getMethod(func);
//            method.invoke(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void runActionsBeforeOtherTests() {
        boolean x = true;
        try {
            expandMountPoint();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            restart();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            stopSoft();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            resize();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            start();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            stopHard();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        Assert.assertTrue(x);
    }

    public void runActionsAfterOtherTests() {
        delete();
    }

    public String getJsonTemplate() {
        return jsonTemplate;
    }

    public void restart() {
        String actionId = orderServiceSteps.executeAction("Перезагрузить", this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void stopHard() {
        String actionId = orderServiceSteps.executeAction("Выключить принудительно", this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void stopSoft() {
        String actionId = orderServiceSteps.executeAction("Выключить", this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void start() {
        String actionId = orderServiceSteps.executeAction("Включить", this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void delete() {
        String actionId = orderServiceSteps.executeAction("Удалить", this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void resize() {
        Map<String, String> map = orderServiceSteps.getFlavorByProduct(this);
        String actionId = orderServiceSteps.executeAction("Изменить конфигурацию", this, new JSONObject(map.get("flavor")));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int cpusAfter = (Integer) orderServiceSteps.getFiledProduct(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getFiledProduct(this, MEMORY);
        assertEquals(Integer.parseInt(map.get("cpus")), cpusAfter);
        assertEquals(Integer.parseInt(map.get("memory")), memoryAfter);
    }

    public void expandMountPoint() {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction("Расширить", this, new JSONObject("{\"size\": 10, \"mount\": \"/app\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue("sizeBefore >= sizeAfter", sizeBefore < sizeAfter);
    }

}
