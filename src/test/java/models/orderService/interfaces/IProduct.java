package models.orderService.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.exception.CalculateException;
import core.exception.CreateEntityException;
import core.helper.http.Http;
import core.utils.Waiting;
import httpModels.productCatalog.graphs.getGraph.response.GetGraphResponse;
import httpModels.productCatalog.product.getProduct.response.GetProductResponse;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.ObjectPoolService;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.productCatalog.Graph;
import models.productCatalog.Product;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.productCatalog.ProductCatalogSteps;
import steps.references.ReferencesStep;
import steps.tarifficator.CostSteps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static core.helper.Configure.OrderServiceURL;


@SuperBuilder
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = false)
@Log4j2
public abstract class IProduct extends Entity {
    //    public static final String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    private static final String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm' && it.data.config.extra_mounts.find{it.mount=='%s'}}.data.config.extra_mounts.find{it.mount=='%s'}.size";
    private static final String CHECK_EXPAND_MOUNT_SIZE = "data.find{it.type=='vm' && it.data.config.extra_mounts.find{it.mount=='%s'}}.data.config.extra_mounts.find{it.mount=='%s' && it.size>%d}.size";
    public static final String CPUS = "data.find{it.type=='vm'}.data.config.flavor.cpus";
    public static final String MEMORY = "data.find{it.type=='vm'}.data.config.flavor.memory";
    public static final String KAFKA_CLUSTER_TOPIC = "data.find{it.type=='cluster'}.data.config.topics.any{it.topic_name=='%s'}";
    public static final String KAFKA_CLUSTER_ACL_TOPICS = "data.find{it.data.config.containsKey='acls'}.data.config.acls.findAll{it.topic_names && it.client_role=='%s'}.any{it.topic_names.any{value -> value=='%s'}}";
    public static final String KAFKA_CLUSTER_ACL_TRANSACTIONS = "data.find{it.type=='cluster'}.data.config.transaction_acls.any{it.transaction_id=='%s'}";

    public static final String CONNECTION_URL = "data.find{it.data.config.containsKey('connection_url')}.data.config.connection_url";
    public static final String EXPAND_MOUNT_POINT = "Расширить";
    public static final String RESTART = "Перезагрузить";
    public static final String STOP_SOFT = "Выключить";
    public static final String START = "Включить";
    public static final String STOP_HARD = "Выключить принудительно";
    public static final String RESIZE = "Изменить конфигурацию";

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

    @Override
    protected <T extends Entity> T createObject(boolean exclusiveAccess, boolean isPublic) {
        T entity = ObjectPoolService.create(this, exclusiveAccess, isPublic);
        ((IProduct) entity).checkPreconditionStatusProduct();
        return entity;
    }

    @SneakyThrows
    @Step("Сравнение стоимости продукта с ценой предбиллинга при заказе")
    protected void compareCostOrderAndPrice() {
        try {
            Float preBillingCost = CostSteps.getPreBillingTotalCost(this);
            Float currentCost = CostSteps.getCurrentCost(this);
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
        OrderServiceSteps.executeAction(action, this, new JSONObject("{\"dumb\":\"empty\"}"), this.getProjectId());
    }

    //Перезагрузить
    protected void restart(String action) {
        OrderServiceSteps.executeAction(action, this, null, this.getProjectId());
    }

    //Выключить принудительно
    protected void stopHard(String action) {
        OrderServiceSteps.executeAction(action, this, null, ProductStatus.STOPPED, this.getProjectId());
    }

    //Выключить
    protected void stopSoft(String action) {
        OrderServiceSteps.executeAction(action, this, null, ProductStatus.STOPPED, this.getProjectId());
    }

    //Включить
    protected void start(String action) {
        OrderServiceSteps.executeAction(action, this, null, ProductStatus.CREATED, this.getProjectId());
    }

    @SneakyThrows
    private void checkPreconditionStatusProduct() {
//        Assume.assumeTrue(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), status), getStatus().equals(status));
        if (!ProductStatus.CREATED.equals(getStatus()) && !ProductStatus.DELETED.equals(getStatus())) {
            close();
            throw new CreateEntityException(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), ProductStatus.CREATED));
        }
    }

    @SneakyThrows
    protected void checkConnectDb(String dbName, String user, String password, String url) {
        String connectUrl = "jdbc:" + url + "/" + dbName;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectUrl, user, password);
            Assertions.assertTrue(Objects.requireNonNull(connection, "Подключение не создалось по url: " + connectUrl).isValid(1));
        } catch (Exception e) {
            throw new Exception("Ошибка подключения к " + getProductName() + " по url " + connectUrl + " : " + e);
        }
        connection.close();
        log.debug("Успешное подключение к " + getProductName());
    }

    //Удалить рекурсивно
    @Step("Удаление продукта")
    protected void delete(String action) {
        OrderServiceSteps.executeAction(action, this, null, ProductStatus.DELETED, this.getProjectId());
        Assertions.assertEquals(0.0F, CalcCostSteps.getCostByUid(this), 0.0F, "Стоимость после удаления заказа больше 0.0");
    }

    public boolean productStatusIs(ProductStatus status) {
        return OrderServiceSteps.productStatusIs(this, status);
    }

    //Изменить конфигурацию
    protected void resize(String action, Flavor flavor) {
        OrderServiceSteps.executeAction(action, this, new JSONObject("{\"flavor\": " + flavor.toString() + "}"), this.getProjectId());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");

    }

    //Изменить конфигурацию
    protected void resize(String action) {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedList(this);
        Assertions.assertTrue(list.size() > 1, "У продукта меньше 2 flavors");
        Flavor flavor = list.get(list.size() - 1);
        OrderServiceSteps.executeAction(action, this, new JSONObject("{\"flavor\": " + flavor.toString() + "}"), this.getProjectId());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");

    }

    @SneakyThrows
    protected String getRandomOsVersion() {
        GetProductResponse productResponse = (GetProductResponse) new ProductCatalogSteps(Product.productName).getById(getProductId(), GetProductResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getById(productResponse.getGraphId(), GetGraphResponse.class);
        String urlAttrs = JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getUiSchema().get("os_version")))
                .getString("'ui:options'.attrs.collect{k,v -> k+'='+v }.join('&')");
        return Objects.requireNonNull(ReferencesStep.getJsonPathList(urlAttrs)
                .getString("collect{it.data.os.version}.shuffled()[0]"), "Версия ОС не найдена");
    }

    @SneakyThrows
    protected boolean getSupport() {
        GetServiceResponse productResponse = (GetServiceResponse) new ProductCatalogSteps(Product.productName).getById(getProductId(), GetServiceResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getById(productResponse.getGraphId(), GetGraphResponse.class);
        Boolean support = (Boolean) graphResponse.getStaticData().get("on_support");
        return Objects.requireNonNull(support, "on_support не найден в графе");
    }

    @SneakyThrows
    protected String getRandomProductVersionByPathEnum(String path) {
        GetProductResponse productResponse = (GetProductResponse) new ProductCatalogSteps(Product.productName).getById(getProductId(), GetProductResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getById(productResponse.getGraphId(), GetGraphResponse.class);
        return Objects.requireNonNull(JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getJsonSchema().get("properties")))
                .getString(path + ".collect{e -> e}.shuffled()[0]"), "Версия продукта не найдена");
    }

    public Flavor getMaxFlavor() {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedList(this);
        return list.get(list.size() - 1);
    }

    public Flavor getMinFlavor() {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedList(this);
        return list.get(0);
    }

    //Расширить
    protected void expandMountPoint(String action, String mount, int size) {
        Float sizeBefore = (Float) OrderServiceSteps.getProductsField(this, String.format(EXPAND_MOUNT_SIZE, mount, mount));
        OrderServiceSteps.executeAction(action, this, new JSONObject("{\"size\": " + size + ", \"mount\": \"" + mount + "\"}"), this.getProjectId());
        float sizeAfter = (Float) OrderServiceSteps.getProductsField(this, String.format(CHECK_EXPAND_MOUNT_SIZE, mount, mount, sizeBefore.intValue()));
        Assertions.assertEquals(sizeBefore, sizeAfter - size, 0.05, "sizeBefore >= sizeAfter");
    }

    protected void initProduct() {
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            setProjectId(project.getId());
        }
        if (label == null) {
            label = UUID.randomUUID().toString();
        }
        if (productId == null) {
            productId = new ProductCatalogSteps(Product.productName).
                    getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(Objects.requireNonNull(getProductName()),
                            "is_open=true&env=" + Objects.requireNonNull(project.getProjectEnvironment().getEnvType().toLowerCase()));
        }
    }

    protected void createProduct() {
        log.info("Отправка запроса на создание заказа " + productName);
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(projectId)
                .body(deleteObjectIfNotFoundInUiSchema(toJson(), getProductId()))
                .post("projects/" + projectId + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");
        OrderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        compareCostOrderAndPrice();
    }

    @SneakyThrows
    private JSONObject deleteObjectIfNotFoundInUiSchema(JSONObject jsonObject, String productId) {
        GetProductResponse productResponse = (GetProductResponse) new ProductCatalogSteps(Product.productName).getById(productId, GetProductResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getById(productResponse.getGraphId(), GetGraphResponse.class);
        List<String> parameters = (List<String>) graphResponse.getUiSchema().get("ui:order");
        Iterator<String> iterator = jsonObject.getJSONObject("order").getJSONObject("attrs").keys();
        while (iterator.hasNext()) {
            if(!parameters.contains(iterator.next()))
                iterator.remove();
        }
        return jsonObject;
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
