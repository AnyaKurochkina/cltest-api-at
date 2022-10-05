package models.orderService.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.enums.Role;
import core.exception.CalculateException;
import core.exception.CreateEntityException;
import core.helper.Configure;
import core.helper.StringUtils;
import core.helper.http.Http;
import core.utils.Waiting;
import httpModels.productCatalog.graphs.getGraph.response.GetGraphResponse;
import httpModels.productCatalog.product.getProduct.response.GetProductResponse;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.response.ValidatableResponseOptions;
import io.restassured.specification.RequestSpecification;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.ObjectPoolService;
import models.authorizer.GlobalUser;
import models.authorizer.Organization;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironmentPrefix;
import models.productCatalog.graph.Graph;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.TestAbortedException;
import ru.testit.annotations.LinkType;
import ru.testit.junit5.StepsAspects;
import ru.testit.services.LinkItem;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.productCatalog.ProductCatalogSteps;
import steps.references.ReferencesStep;
import steps.tarifficator.CostSteps;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.stream.Collectors;

import static core.helper.Configure.OrderServiceURL;
import static org.hamcrest.Matchers.emptyOrNullString;


@SuperBuilder
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = false)
@Log4j2
public abstract class IProduct extends Entity {
    //    public static final String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm'}.config.extra_disks.size()";
    public static final String EXPAND_MOUNT_SIZE = "data.find{it.type=='vm' && it.data.config.extra_mounts.find{it.mount=='%s'}}.data.config.extra_mounts.find{it.mount=='%s'}.size";
    public static final String CHECK_EXPAND_MOUNT_SIZE = "data.find{it.type=='vm' && it.data.config.extra_mounts.find{it.mount=='%s'}}.data.config.extra_mounts.find{it.mount=='%s' && it.size>%d}.size";
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

    @ToString.Include
    @Getter
    String platform;

    protected String jsonTemplate;

    @Getter
    @Setter
    transient String link, error;

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
    @Setter
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

    @Step("Получение Id geoDistribution у продукта '{product}' с именем '{name}'")
    protected String getIdGeoDistribution(String product, String name) {
        Organization org = Organization.builder().build().createObject();
        return Objects.requireNonNull(ReferencesStep
                .getJsonPathList(String.format("tags__contains=%s,%s,%s&directory__name=geo_distribution", envType().toUpperCase(), product, org.getName()))
                .getString(String.format("find{it.name == '%s'}.id", name)), "Id geo_distribution not found");
    }

    @Override
    protected <T extends Entity> T createObject(boolean exclusiveAccess, boolean isPublic) {
        T entity = ObjectPoolService.create(this, exclusiveAccess, isPublic);
        ((IProduct) entity).addLinkProduct();
        ((IProduct) entity).checkPreconditionStatusProduct();
        return entity;
    }

    public void addLinkProduct() {
        if (Objects.nonNull(getOrderId())) {
            if (StepsAspects.getCurrentStep().get() != null) {
                Organization org = Organization.builder().build().createObject();
                StepsAspects.getCurrentStep().get().addLinkItem(
                        new LinkItem("Product URL", String.format("%svm/orders/%s/main?context=%s&type=project&org=%s",
                                Configure.getAppProp("base.url"), getOrderId(), getProjectId(), org.getName()), "", LinkType.RELATED));
            }
        }
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

    public static String certPath = "data.find{it.data.config.containsKey('certificate_expiration')}.data.config.certificate_expiration";
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

    private void checkPreconditionStatusProduct() {
//        Assume.assumeTrue(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), status), getStatus().equals(status));
        if (!ProductStatus.CREATED.equals(getStatus()) && !ProductStatus.DELETED.equals(getStatus())) {
            close();
            throw new CreateEntityException(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), ProductStatus.CREATED));
        }
        String status = OrderServiceSteps.getStatus(this);
        if (status.equals("changing") || status.equals("pending")) {
            close();
            throw new CreateEntityException(String.format("Статус продукта %s не соответствует исходному", status));
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
            connectVmException("Ошибка подключения к " + getProductName() + " по url " + connectUrl + " : " + e);
        }
        assert connection != null;
        connection.close();
        log.debug("Успешное подключение к " + getProductName());
    }

    //Удалить рекурсивно
    @Step("Удаление продукта")
    protected void delete(String action) {
        OrderServiceSteps.executeAction(action, this, null, ProductStatus.DELETED, this.getProjectId());
        Assertions.assertEquals(0.0F, CalcCostSteps.getCostByUid(this), 0.0F, "Стоимость после удаления заказа больше 0.0");
        if(Objects.isNull(platform))
            return;
        if (platform.equalsIgnoreCase("vSphere") && Configure.ENV.equalsIgnoreCase("IFT")) {
            GlobalUser user = GlobalUser.builder().role(Role.IPAM).build().createObject();
            List<String> ipList = ((List<String>) OrderServiceSteps.getProductsField(this, "data.data.config.default_v4_address", List.class))
                    .stream().filter(Objects::nonNull).collect(Collectors.toList());

            RequestSpecification specification = RestAssured.given()
                    .baseUri("https://d5-phpipam.apps.dk5-soul01.corp.dev.vtb")
                    .config(RestAssured.config().sslConfig(Http.sslConfig));

            String token = RestAssured.given().spec(specification).auth().preemptive().basic(user.getUsername(), user.getPassword())
                    .post("/api/cloud/user/")
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getString("data.token");

            ValidatableResponseOptions<ValidatableResponse, Response> options = RestAssured.given().spec(specification).header("token", token)
                    .get("/api/cloud/subnets/56291/addresses")
                    .then()
                    .statusCode(200);
            for (String ip : ipList)
                options.body(String.format("data.find{it.ip=='%s'}.hostname", ip), emptyOrNullString());
        }
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

    //example: https://cloud.vtb.ru/vm/orders/ecb3567b-afa6-43a4-8a49-6e0ef5b1a952/topics?context=proj-7ll0yy5zsc&type=project&org=vtb
    public <T extends Entity> T buildFromLink(String link) {
        projectId = StringUtils.findByRegex("context=([^&]*)", link);
        orderId = StringUtils.findByRegex("orders/([^/]*)/", link);
        productId = ((String) OrderServiceSteps.getProductsField(this, "product_id"));
        return (T) this;
    }

    public <T extends Entity> T buildFromLink() {
        projectId = StringUtils.findByRegex("context=([^&]*)", link);
        orderId = StringUtils.findByRegex("orders/([^/]*)/", link);
        return (T) this;
    }

    //Изменить конфигурацию
    protected void resize(String action) {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedListByFilter(this);
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
        GetProductResponse productResponse = (GetProductResponse) new ProductCatalogSteps("/api/v1/products/").getById(getProductId(), GetProductResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getByIdAndEnv(productResponse.getGraphId(), envType(), GetGraphResponse.class);
        String urlAttrs = JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getUiSchema().get("os_version")))
                .getString("'ui:options'.attrs.collect{k,v -> k+'='+v }.join('&')");
        return Objects.requireNonNull(ReferencesStep.getJsonPathList(urlAttrs)
                .getString("collect{it.data.os.version}.shuffled()[0]"), "Версия ОС не найдена");
    }

    @SneakyThrows
    public String getFilter() {
        GetProductResponse productResponse = (GetProductResponse) new ProductCatalogSteps("/api/v1/products/").getById(getProductId(), GetProductResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getByIdAndEnv(productResponse.getGraphId(), envType(), GetGraphResponse.class);
        return JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getUiSchema().get("flavor")))
                .getString("'ui:options'.filter");
    }

    @SneakyThrows
    protected boolean getSupport() {
        GetProductResponse productResponse = (GetProductResponse) new ProductCatalogSteps("/api/v1/products/").getById(getProductId(), GetProductResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getByIdAndEnv(productResponse.getGraphId(), envType(), GetGraphResponse.class);
        Boolean support = (Boolean) graphResponse.getStaticData().get("on_support");
        if(Objects.isNull(support)) {
            support = JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getJsonSchema().get("properties")))
                    .getBoolean("on_support.default");
        }
        return Objects.requireNonNull(support, "on_support не найден в графе");
    }

    @SneakyThrows
    protected String getRandomProductVersionByPathEnum(String path) {
        GetProductResponse productResponse = (GetProductResponse) new ProductCatalogSteps("/api/v1/products/").getById(getProductId(), GetProductResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getByIdAndEnv(productResponse.getGraphId(), envType(), GetGraphResponse.class);
        return Objects.requireNonNull(JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getJsonSchema().get("properties")))
                .getString(path + ".collect{e -> e}.shuffled()[0]"), "Версия продукта не найдена");
    }

    public Flavor getMaxFlavor() {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedListByFilter(this);
        Assertions.assertFalse(list.size() < 2, "Действие недоступно, либо кол-во flavor's < 2");
        return list.get(list.size() - 1);
    }

    public Flavor getMinFlavor() {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedListByFilter(this);
        return list.get(0);
    }

    //Расширить
    protected void expandMountPoint(String action, String mount, int size) {
        Float sizeBefore = (Float) OrderServiceSteps.getProductsField(this, String.format(EXPAND_MOUNT_SIZE, mount, mount));
        OrderServiceSteps.executeActionWidthFilter(action, this, new JSONObject("{\"size\": " + size + ", \"mount\": \"" + mount + "\"}"), this.getProjectId(),
                String.format("extra_mounts.find{it.mount == '%s'}", mount));
        float sizeAfter = (Float) OrderServiceSteps.getProductsField(this, String.format(CHECK_EXPAND_MOUNT_SIZE, mount, mount, sizeBefore.intValue()));
        Assertions.assertEquals(sizeBefore, sizeAfter - size, 0.05, "sizeBefore >= sizeAfter");
    }

    protected void initProduct() {
        Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            setProjectId(project.getId());
        }
        if (label == null) {
            label = "AT-API-" + UUID.randomUUID();
        }
        if (productId == null) {
            productId = new ProductCatalogSteps("/api/v1/products/").
                    getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(Objects.requireNonNull(getProductName()),
                            "is_open=true&env=" + Objects.requireNonNull(project.getProjectEnvironmentPrefix().getEnvType().toLowerCase()));
        }
    }

    protected void createProduct() {
//        Waiting.sleep((int) ((Math.random() * 20000) + 10000));
        log.info("Отправка запроса на создание заказа " + productName);
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(projectId)
                .body(deleteObjectIfNotFoundInUiSchema(toJson(), getProductId()))
                .post("/v1/projects/" + projectId + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");
        OrderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        compareCostOrderAndPrice();
    }

    @SneakyThrows
    private JSONObject deleteObjectIfNotFoundInUiSchema(JSONObject jsonObject, String productId) {
        GetProductResponse productResponse = (GetProductResponse) new ProductCatalogSteps("/api/v1/products/").getById(productId, GetProductResponse.class);
        GetGraphResponse graphResponse = (GetGraphResponse) new ProductCatalogSteps(Graph.productName).getByIdAndEnv(productResponse.getGraphId(), envType(), GetGraphResponse.class);
        List<String> parameters = (List<String>) graphResponse.getUiSchema().get("ui:order");
        if (Objects.isNull(parameters))
            return jsonObject;
        if (graphResponse.getJsonSchema().containsKey("dependencies"))
            parameters.addAll(((Map<String, Object>) graphResponse.getJsonSchema().get("dependencies")).keySet());
        Iterator<String> iterator = jsonObject.getJSONObject("order").getJSONObject("attrs").keys();
        while (iterator.hasNext()) {
            if (!parameters.contains(iterator.next()))
                iterator.remove();
        }
        return jsonObject;
    }

    protected boolean isTest() {
        return envType().contains("test");
    }

    protected String envType() {
        Project project = Project.builder().id(projectId).build().createObject();
        return project.getProjectEnvironmentPrefix().getEnvType().toLowerCase();
    }

    public void connectVmException(String message) throws ConnectException {
        if (!isTest())
            throw new ConnectException(message);
        throw new TestAbortedException("Тест отключен для продуктов в TEST средах");
    }

}
