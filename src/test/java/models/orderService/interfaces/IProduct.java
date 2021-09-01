package models.orderService.interfaces;

import static io.qameta.allure.Allure.getLifecycle;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import models.Entity;
import models.subModels.Flavor;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ToString(onlyExplicitlyIncluded = true)
public abstract class IProduct extends Entity {
    public static String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public static String CPUS = "data.find{it.type=='vm'}.config.flavor.cpus";
    public static String MEMORY = "data.find{it.type=='vm'}.config.flavor.memory";
    public static String KAFKA_CLUSTER_TOPIC = "data.find{it.type=='cluster'}.config.topics.any{it.topic_name=='%s'}";
    public static String KAFKA_CLUSTER_ACL_TOPICS = "data.find{it.type=='cluster'}.config.acls.any{it.topic_name=='%s'}";
    public static String KAFKA_CLUSTER_ACL_TRANSACTIONS = "data.find{it.type=='cluster'}.config.transaction_acls.any{it.transaction_id=='%s'}";

    protected transient OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
    protected transient ReferencesStep referencesStep = new ReferencesStep();
    protected transient String jsonTemplate;
    @Setter
    protected transient List<String> actions;

    @Setter
    @Getter
    private ProductStatus status = ProductStatus.NOT_CREATED;
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
                    fail("Action '" + action + "' не найден у продукта " + getProductName());
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
        List<Flavor> list = referencesStep.getProductFlavorsLinkedList(this);
        Assert.assertTrue("У продукта меньше 2 flavors", list.size() > 1);
        Flavor flavor = list.get(list.size() - 1);
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(flavor.toString()));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int cpusAfter = (Integer) orderServiceSteps.getFiledProduct(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getFiledProduct(this, MEMORY);
        assertEquals(flavor.data.cpus, cpusAfter);
        assertEquals(flavor.data.memory, memoryAfter);
    }

    @Action("Расширить")
    public void expandMountPoint(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject("{\"size\": 10, \"mount\": \"/app\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue("sizeBefore >= sizeAfter", sizeBefore < sizeAfter);
    }

    @Step
    @SneakyThrows
    public void toStringProductStep() {
        AllureLifecycle allureLifecycle = getLifecycle();
        String id = allureLifecycle.getCurrentTestCaseOrStep().get();
        List<Parameter> list = new ArrayList<>();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(getClass().getSuperclass().getDeclaredFields()));
        fieldList.addAll(Arrays.asList(getClass().getDeclaredFields()));
        for (Field field : fieldList) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;
            field.setAccessible(true);
            if (field.get(this) != null) {
                Parameter parameter = new Parameter();
                parameter.setName(field.getName());
                parameter.setValue(field.get(this).toString());
                list.add(parameter);
            }
        }
        allureLifecycle.updateStep(id, s -> s.setName("Получен продукт " + getProductName() + " с параметрами"));
        allureLifecycle.updateStep(id, s -> s.setParameters(list));
    }
}
