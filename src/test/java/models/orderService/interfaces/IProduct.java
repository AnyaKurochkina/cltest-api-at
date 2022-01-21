package models.orderService.interfaces;

import core.exception.CalculateException;
import core.exception.CreateEntityException;
import core.helper.Http;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;
import steps.tarifficator.CostSteps;

import java.util.List;
import java.util.UUID;

import static core.helper.Configure.OrderServiceURL;


@SuperBuilder
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = false)
@Log4j2
public abstract class IProduct extends Entity {
//    public static final String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    private static final String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm' && it.config.extra_mounts.find{it.mount=='%s'}}.config.extra_mounts.find{it.mount=='%s'}.size";
    private static final String CHECK_EXPAND_MOUNT_SIZE = "data.find{it.type=='vm' && it.config.extra_mounts.find{it.mount=='%s'}}.config.extra_mounts.find{it.mount=='%s' && it.size>%d}.size";
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

    @Builder.Default
    protected transient OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
    @Builder.Default
    protected transient ReferencesStep referencesStep = new ReferencesStep();
    protected String jsonTemplate;

    @Getter
    private ProductStatus status;

    @Getter
    protected String orderId;
    @Getter
    protected String label;
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


    public void setStatus(ProductStatus status) {
        this.status = status;
        save();
    }

    @SneakyThrows
    @Step("Сравнение стоимости продукта с ценой предбиллинга при заказе")
    protected void compareCostOrderAndPrice() {
        try {
            CostSteps costSteps = new CostSteps();
            Float preBillingCost = costSteps.getPreBillingCost(this);
            Float currentCost = costSteps.getCurrentCost(this);
            for (int i = 0; i < 15; i++) {
                Waiting.sleep(20000);
                if (Float.compare(currentCost, preBillingCost) > 0.00001)
                    continue;
                break;
            }
            Assertions.assertEquals(preBillingCost, currentCost, 0.00001, "Стоимость предбиллинга отличается от стоимости продукта " + this);
        } catch (Throwable e) {
            throw new CalculateException(e);
        }
    }

    //Обновить сертификаты
    protected void updateCerts(String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"dumb\":\"empty\"}"));
    }

    //Перезагрузить
    protected void restart(String action) {
        orderServiceSteps.executeAction(action, this, null);
    }

    //Выключить принудительно
    protected void stopHard(String action) {
        orderServiceSteps.executeAction(action, this, null, ProductStatus.STOPPED);
    }

    //Выключить
    protected void stopSoft(String action) {
        orderServiceSteps.executeAction(action, this, null, ProductStatus.STOPPED);
    }

    //Включить
    protected void start(String action) {
        orderServiceSteps.executeAction(action, this, null, ProductStatus.CREATED);
    }

    @SneakyThrows
    public void checkPreconditionStatusProduct(ProductStatus status) {
//        Assume.assumeTrue(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), status), getStatus().equals(status));
        if(!status.equals(getStatus()))
            throw new CreateEntityException(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), status));
    }

    //Удалить рекурсивно
    @Step("Удаление продукта")
    protected void delete(String action) {
        CalcCostSteps calcCostSteps = new CalcCostSteps();
        orderServiceSteps.executeAction(action, this, null, ProductStatus.DELETED);
        Assertions.assertEquals(0.0F, calcCostSteps.getCostByUid(this), 0.0F, "Стоимость после удаления заказа больше 0.0");
    }

    //Изменить конфигурацию
    protected void resize(String action) {
        List<Flavor> list = referencesStep.getProductFlavorsLinkedList(this);
        Assertions.assertTrue(list.size() > 1, "У продукта меньше 2 flavors");
        Flavor flavor = list.get(list.size() - 1);
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"flavor\": " + flavor.toString() + "}"));
        int cpusAfter = (Integer) orderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");
    }

    //Расширить
    protected void expandMountPoint(String action, String mount, int size) {
        Float sizeBefore = (Float) orderServiceSteps.getProductsField(this, String.format(EXPAND_MOUNT_SIZE, mount, mount));
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"size\": " + size + ", \"mount\": \"" + mount + "\"}"));
        float sizeAfter = (Float) orderServiceSteps.getProductsField(this, String.format(CHECK_EXPAND_MOUNT_SIZE, mount, mount, sizeBefore.intValue()));
        Assertions.assertEquals(sizeBefore, sizeAfter - size, 0.05, "sizeBefore >= sizeAfter");
    }
    protected void initProduct(){
        if(projectId == null) {
            Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
            projectId = project.getId();
        }
        if(label == null) {
            label = UUID.randomUUID().toString();
        }
        if(productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
    }
    protected void createProduct(){
        log.info("Отправка запроса на создание заказа " + productName);
//        JsonPath jsonPath = new Http(OrderServiceURL)
//                .setProjectId(projectId)
//                .body(toJson())
//                .post("projects/" + projectId + "/orders")
//                .assertStatus(201)
//                .jsonPath();
//        orderId = jsonPath.get("[0].id");
//        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        compareCostOrderAndPrice();
    }


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
