package models.orderService.interfaces;

import static org.junit.Assert.*;

import core.exception.CustomException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import models.Entity;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
    @Setter
    protected transient List<String> actions;

    @Setter
    @Getter
    private ProductStatus status = ProductStatus.NOT_CREATED;
    @Getter
    @ToString.Include
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

    public abstract JSONObject getJsonParametrizedTemplate();


    private boolean invokeAction(String action) throws Throwable {
        boolean invoke = false;
        for (Method method : this.getClass().getMethods()) {
            if (method.isAnnotationPresent(Action.class)) {
                if (method.getAnnotation(Action.class).value().equals(action)) {
                    method.invoke(this, action);
                    invoke = true;
                }
            }
        }
        return invoke;
    }

    @SneakyThrows
    public void runActionsBeforeOtherTests() {
        Throwable error = null;
        for (String action : actions) {
            try {
                if (!invokeAction(action))
                    throw new CustomException("Action '" + action + "' не найден у продукта " + getProductName());
            } catch (Throwable e) {
                e.printStackTrace();
                error = e;
            }
        }
        if (error != null)
            throw error;
    }

    public void runActionsAfterOtherTests() {
        String value = "";
        try {
            value = this.getClass().getMethod("delete", String.class).getAnnotation(Action.class).value();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        delete(value);
    }

    public String getJsonTemplate() {
        return jsonTemplate;
    }

    @Action("Перезагрузить")
    public void restart(String action) {
        String actionId = orderServiceSteps.executeAction(action, this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Action("Выключить принудительно")
    public void stopHard(String action) {
        String actionId = orderServiceSteps.executeAction(action, this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Action("Выключить")
    public void stopSoft(String action) {
        String actionId = orderServiceSteps.executeAction(action, this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Action("Включить")
    public void start(String action) {
        String actionId = orderServiceSteps.executeAction(action, this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Action("Удалить")
    public void delete(String action) {
        String actionId = orderServiceSteps.executeAction(action, this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        setStatus(ProductStatus.DELETED);
        cacheService.saveEntity(this);
    }

    @Action("Изменить конфигурацию")
    public void resize(String action) {
        Map<String, String> map = orderServiceSteps.getFlavorByProduct(this);
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(map.get("flavor")));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int cpusAfter = (Integer) orderServiceSteps.getFiledProduct(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getFiledProduct(this, MEMORY);
        assertEquals(Integer.parseInt(map.get("cpus")), cpusAfter);
        assertEquals(Integer.parseInt(map.get("memory")), memoryAfter);
    }

    @Action("Расширить")
    public void expandMountPoint(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject("{\"size\": 10, \"mount\": \"/app\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue("sizeBefore >= sizeAfter", sizeBefore < sizeAfter);
    }

}
