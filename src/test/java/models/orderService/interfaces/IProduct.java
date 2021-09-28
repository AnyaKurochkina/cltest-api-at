package models.orderService.interfaces;

import static io.qameta.allure.Allure.getLifecycle;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import core.CacheService;
import core.exception.DeferredException;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.EntityOld;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString(onlyExplicitlyIncluded = true, includeFieldNames = false)
@Log4j2
public abstract class IProduct extends Entity {
    public static String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public static String CPUS = "data.find{it.type=='vm'}.config.flavor.cpus";
    public static String MEMORY = "data.find{it.type=='vm'}.config.flavor.memory";
    public static String KAFKA_CLUSTER_TOPIC = "data.find{it.type=='cluster'}.config.topics.any{it.topic_name=='%s'}";
    public static String KAFKA_CLUSTER_ACL_TOPICS = "data.find{it.type=='cluster'}.config.acls.any{it.topic_name=='%s'}";
    public static String KAFKA_CLUSTER_ACL_TRANSACTIONS = "data.find{it.type=='cluster'}.config.transaction_acls.any{it.transaction_id=='%s'}";

    protected transient OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
    protected transient ReferencesStep referencesStep = new ReferencesStep();
    protected transient CacheService cacheService = new CacheService();
    protected transient String jsonTemplate;
    @Setter
    protected List<String> actions;

    @Setter
    @Getter
    private ProductStatus status;
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

    /**
     * Заказ продукта, реализуется в каждом продукте по своему
     */
    public abstract void order();

    /**
     * Метод для выбора json шаблона заказа
     * @return возвращает готовый параметризованный json
     */
    public abstract JSONObject getJsonParametrizedTemplate();

    /**
     * @param action экшен
     * @return - возвращаем статус вызова экшена
     * @throws Throwable необходим для метода invoke (java.lang.reflect.Method)
     */
    @SneakyThrows
    public boolean invokeAction(String action) {
        boolean invoke = false;
        //Перебираем методы класса
        for (Method method : this.getClass().getMethods()) {
            /* Если аннотиация @Action присутствует над методом и ее знаечение соответсвтует названию экшена,
            то вызываем его и изменяем состояние вызова в true */
            if (method.isAnnotationPresent(Action.class)
                    && method.getAnnotation(Action.class).value().equals(action)) {
                method.invoke(this, action);
                invoke = true;
            }
        }
        return invoke;
    }

    /**
     * Вызываем экшены кроме удаления в цикле, если экшен не выполнился или выполнился с ошибкой,
     * то сохраняем его ошибку и после прохождения всех экшенов отображаем её
     */
    @SneakyThrows
    public void runActionsBeforeOtherTests() {
        //Инициализация отложенного исключения
        DeferredException exception = new DeferredException();
        for (String action : actions) {
            try {
                if (!invokeAction(action)) {
                    fail("Action '" + action + "' не найден у продукта " + getProductName() + " с id " + getOrderId());
                }
            } catch (Throwable e) {
                exception.addException(e, getOrderId());
            }
        }
        exception.trowExceptionIfNotEmpty();
    }

    /**
     * Выполнение экшенов "Удаление заказа"
     */
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
        orderServiceSteps.executeAction(action, this, null);
    }

    @Action("Выключить принудительно")
    public void stopHard(String action) {
        orderServiceSteps.executeAction(action, this, null);
    }

    @Action("Выключить")
    public void stopSoft(String action) {
        orderServiceSteps.executeAction(action, this, null);
    }

    @Action("Включить")
    public void start(String action) {
        orderServiceSteps.executeAction(action, this, null);
    }

    @Action("Удалить")
    public void delete(String action) {
        CalcCostSteps calcCostSteps = new CalcCostSteps();
        orderServiceSteps.executeAction(action, this, null);
        setStatus(ProductStatus.DELETED);
        Assert.assertEquals("Стоимость после удаления заказа больше 0.0", 0.0F, calcCostSteps.getCostByUid(this), 0.0F);
    }

    @Action("Изменить конфигурацию")
    public void resize(String action) {
        List<Flavor> list = referencesStep.getProductFlavorsLinkedList(this);
        Assert.assertTrue("У продукта меньше 2 flavors", list.size() > 1);
        Flavor flavor = list.get(list.size() - 1);
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"flavor\": " + flavor.toString() + "}"));
        int cpusAfter = (Integer) orderServiceSteps.getFiledProduct(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getFiledProduct(this, MEMORY);
        assertEquals(flavor.data.cpus, cpusAfter);
        assertEquals(flavor.data.memory, memoryAfter);
    }

    @Action("Расширить")
    public void expandMountPoint(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"size\": 10, \"mount\": \"/app\"}"));
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
