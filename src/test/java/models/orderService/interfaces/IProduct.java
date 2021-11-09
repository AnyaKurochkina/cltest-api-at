package models.orderService.interfaces;

import core.CacheService;
import core.exception.DeferredException;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import org.junit.Assume;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@SuperBuilder
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = false)
@Log4j2
public abstract class IProduct extends Entity {
    public static final String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public static final String CPUS = "data.find{it.type=='vm'}.config.flavor.cpus";
    public static final String MEMORY = "data.find{it.type=='vm'}.config.flavor.memory";
    public static final String KAFKA_CLUSTER_TOPIC = "data.find{it.type=='cluster'}.config.topics.any{it.topic_name=='%s'}";
    public static final String KAFKA_CLUSTER_ACL_TOPICS = "data.find{it.type=='cluster'}.config.acls.any{it.topic_name=='%s'}";
    public static final String KAFKA_CLUSTER_ACL_TRANSACTIONS = "data.find{it.type=='cluster'}.config.transaction_acls.any{it.transaction_id=='%s'}";

    public static final String EXPAND_MOUNT_POINT = "Расширить";
    public static final String RESTART = "Перезагрузить";
    public static final String STOP_SOFT = "Выключить";
    public static final String START = "Включить";
    public static final String STOP_HARD = "Выключить принудительно";
    public static final String RESIZE = "Изменить конфигурацию";

    protected transient OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
    protected transient ReferencesStep referencesStep = new ReferencesStep();
    protected transient CacheService cacheService = new CacheService();
    protected transient String jsonTemplate;
    @Setter
    protected List<String> actions;

    @Getter
    private ProductStatus status;

    @Getter
    protected String orderId;
    @Getter
    @Setter
    protected String projectId;
    @Getter
    protected String productName;
    @Getter
    @ToString.Include
    protected String env;
    @Getter
    protected String productId;
    /**
     * Метод для выбора json шаблона заказа
     * @return возвращает готовый параметризованный json
     */
//    public abstract JSONObject getJsonParametrizedTemplate();

    public JSONObject toJson() {
        return null;
    }

    /**
     * @param action экшен
     * @return - возвращаем статус вызова экшена
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

//    public String getJsonTemplate() {
//        return jsonTemplate;
//    }

    public void setStatus(ProductStatus status) {
        this.status = status;
        save();
    }

    //Обновить сертификаты
    protected void updateCerts(String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"dumb\":\"empty\"}"));
    }

    //Перезагрузить
    protected void restart(String action) {
        orderServiceSteps.executeAction("reset_two_layer", this, null);
    }

    //Выключить принудительно
    protected void stopHard(String action) {
        orderServiceSteps.executeAction("stop_hard_two_layer", this, null);
        setStatus(ProductStatus.STOPPED);
    }

    //Выключить
    protected void stopSoft(String action) {
        orderServiceSteps.executeAction("stop_two_layer", this, null);
        setStatus(ProductStatus.STOPPED);
    }

    //Включить
    protected void start(String action) {
        orderServiceSteps.executeAction("start_two_layer", this, null);
        setStatus(ProductStatus.CREATED);
    }

    public void checkPreconditionStatusProduct(ProductStatus status){
        Assume.assumeTrue(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), status), getStatus().equals(status));
    }

    //Удалить рекурсивно
    @Step("Удаление продукта")
    protected void delete(String action) {
        CalcCostSteps calcCostSteps = new CalcCostSteps();
        orderServiceSteps.executeAction(action, this, null);
        setStatus(ProductStatus.DELETED);
        Assert.assertEquals("Стоимость после удаления заказа больше 0.0", 0.0F, calcCostSteps.getCostByUid(this), 0.0F);
    }

    //Изменить конфигурацию
    protected void resize(String action) {
        List<Flavor> list = referencesStep.getProductFlavorsLinkedList(this);
        Assert.assertTrue("У продукта меньше 2 flavors", list.size() > 1);
        Flavor flavor = list.get(list.size() - 1);
        orderServiceSteps.executeAction("resize_vm", this, new JSONObject("{\"flavor\": " + flavor.toString() + "}"));
        int cpusAfter = (Integer) orderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getProductsField(this, MEMORY);
        assertEquals("Конфигурация cpu не изменилась или изменилась неверно", flavor.data.cpus, cpusAfter);
        assertEquals("Конфигурация ram не изменилась или изменилась неверно", flavor.data.memory, memoryAfter);
    }

    //Расширить
    protected void expandMountPoint(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, EXPAND_MOUNT_SIZE);
        orderServiceSteps.executeAction("expand_mount_point", this, new JSONObject("{\"size\": 10, \"mount\": \"/app\"}"));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, EXPAND_MOUNT_SIZE);
        assertTrue("sizeBefore >= sizeAfter", sizeBefore < sizeAfter);
    }

//    @Step
//    @SneakyThrows
//    public void toStringProductStep() {
//        AllureLifecycle allureLifecycle = Allure.getLifecycle();
//        String id = allureLifecycle.getCurrentTestCaseOrStep().get();
//        List<Parameter> list = new ArrayList<>();
//        List<Field> fieldList = new ArrayList<>(Arrays.asList(getClass().getSuperclass().getDeclaredFields()));
//        fieldList.addAll(Arrays.asList(getClass().getDeclaredFields()));
//        for (Field field : fieldList) {
//            if (Modifier.isStatic(field.getModifiers()))
//                continue;
//            field.setAccessible(true);
//            if (field.get(this) != null) {
//                Parameter parameter = new Parameter();
//                parameter.setName(field.getName());
//                parameter.setValue(field.get(this).toString());
//                list.add(parameter);
//            }
//        }
//        allureLifecycle.updateStep(id, s -> s.setName("Получен продукт " + getProductName() + " с параметрами"));
//        allureLifecycle.updateStep(id, s -> s.setParameters(list));
//    }

}
