package models.cloud.orderService.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.enums.Role;
import core.exception.CalculateException;
import core.exception.CreateEntityException;
import core.exception.NotFoundElementException;
import core.helper.Configure;
import core.helper.JsonTemplate;
import core.helper.Report;
import core.helper.StringUtils;
import core.helper.http.Http;
import core.utils.Waiting;
import core.utils.ssh.SshClient;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
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
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.portalBack.AccessGroup;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.opentest4j.TestAbortedException;
import ru.testit.annotations.LinkType;
import ru.testit.junit5.StepsAspects;
import ru.testit.services.LinkItem;
import steps.calculator.CalcCostSteps;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;
import steps.productCatalog.ProductCatalogSteps;
import steps.references.ReferencesStep;
import steps.tarifficator.CostSteps;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static core.helper.Configure.orderServiceURL;
import static core.utils.AssertUtils.assertContains;
import static org.hamcrest.Matchers.emptyOrNullString;
import static steps.productCatalog.GraphSteps.getGraphByIdAndEnv;
import static steps.productCatalog.ProductSteps.getProductByCloudAdmin;


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
    public static final String VM_IP_PATH = "product_data.find{it.type=='vm'}.ip";
    public static final String CONNECTION_URL = "data.find{it.data.config.containsKey('connection_url')}.data.config.connection_url";
    public static final String EXPAND_MOUNT_POINT = "Расширить";
    public static final String RESTART = "Перезагрузить";
    public static final String STOP_SOFT = "Выключить";
    public static final String START = "Включить";
    public static final String STOP_HARD = "Выключить принудительно";
    public static final String RESIZE = "Изменить конфигурацию";
    public final static String STATE_PATH = "data.find{it.data.config.name=='%s'}.data.state";

    @ToString.Include
    @Getter
    @Setter
    protected String platform;

    @Setter
    protected String segment;
    @Setter
    @Getter
    protected String availabilityZone, domain;

    protected String jsonTemplate;

    @Setter
    transient String link;
    @Setter
    @Getter
    transient String error;

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
    @Setter
    @ToString.Include
    protected String env;
    @Getter
    protected String productId;
    @Getter
    protected String productCatalogName;

    public String getLink() {
        log.debug("Get Link: {}", link);
        return link;
    }

    public String getDomain() {
        return Objects.requireNonNull(domain, "Поле domain пустое");
    }

    public String getSegment() {
        return Objects.requireNonNull(segment, "Поле segment пустое");
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
        save();
    }

    public String accessGroup(String type, String desc) {
        String accessGroupId;
        try {
            accessGroupId = PortalBackSteps.getAccessGroupByDesc(projectId, desc, type, domain);
        } catch (NullPointerException ignored) {
            AccessGroup accessGroup = AccessGroup.builder().projectName(projectId).description(desc).accountsType(type).domain(domain).build().createObject();
            accessGroupId = accessGroup.getPrefixName();
        }
        return accessGroupId;
    }

    public String accessGroup() {
        return accessGroup("personal", "AT-ORDER");
    }

    public String additionalAccessGroup() {
        return accessGroup("personal", "AT-ORDER-2");
    }

    protected String state(String name) {
        return (String) OrderServiceSteps.getProductsField(this, String.format(STATE_PATH, name));
    }

    @Step("Получение Id geoDistribution у продукта '{name}' с тегами '{tags}'")
    protected String getIdGeoDistribution(String name, String... tags) {
        StringJoiner tagsJoiner = new StringJoiner(",");
        Arrays.stream(tags).forEach(tagsJoiner::add);
        return Objects.requireNonNull(ReferencesStep.getJsonPathList(String.format("tags__contains=%s&directory__name=geo_distribution", tagsJoiner))
                .getString(String.format("find{it.name.contains('%s')}.id", name)), "Id geo_distribution not found " + name);
    }

    private String replaceGraphParams(String str) {
        return Objects.requireNonNull(str)
                .replace("${context::projectInfo.project_environment.environment_type}", envType().toUpperCase())
                .replace("${context::formData.platform}", getPlatform())
                .replace("${context::formData.os_vendor.ifNil(astra)}", "astra")
                .replace("${context::projectInfo.organization}", ((Organization) Organization.builder().build().createObject()).getName())
                .replace("${context::formData.default_nic.net_segment}", getSegment());
    }

    @SneakyThrows
    @Step("Получение Id geoDistribution у продукта '{name}'")
    protected String getIdGeoDistribution(String name) {
        Product productResponse = getProductByCloudAdmin(getProductId());
        Graph graphResponse = getGraphByIdAndEnv(productResponse.getGraphId(), envType());
        String tags = replaceGraphParams(JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getUiSchema().get("layout")))
                .getString("'ui:options'.attrs.tags__contains"));
        return Objects.requireNonNull(ReferencesStep.getJsonPathList(String.format("tags__contains=%s&directory__name=geo_distribution", tags))
                .getString(String.format("find{it.name.contains('%s')}.id", name)), "Id geo_distribution not found " + name);
    }

    @Step("Проверка дисков соответствию rules")
    public void checkVmDisk(Map<String, String> rules) {
        List<Map<String, String>> vmList = OrderServiceSteps.getProductsField(this,
                "data.findAll{it.type=='vm'}.data.config.collect{[(it.node_roles==null?'*':it.node_roles[0]):it.storage_profile]}", List.class);
        for (Map.Entry<String, String> rule : rules.entrySet())
            Assertions.assertTrue(vmList.stream().filter(e -> e.containsKey(rule.getKey())).allMatch(e -> e.get(rule.getKey()).equalsIgnoreCase(rule.getValue())),
                    "Диски не соответствуют правилу: " + rule.getKey() + ":" + rule.getValue());
    }

    @Override
    protected <T extends Entity> T createObject(boolean exclusiveAccess, boolean isPublic) {
        T entity = ObjectPoolService.create(this, exclusiveAccess, isPublic);
        try {
            ((IProduct) entity).addLinkProduct();
            ((IProduct) entity).checkPreconditionStatusProduct();
        } catch (Throwable e) {
            entity.close();
        }
        return entity;
    }

    public void checkCertsBySsh() {
        if (Configure.ENV.equalsIgnoreCase("prod")) {
            String[] certs = {"VTB Dev Environment Root CA"};
            if (envType().contains("test"))
                certs = new String[]{"VTB Test Environment Root CA"};
            else if (envType().contains("prod"))
                certs = new String[]{"VTB Group Root CA", "VTB Group VTB24 CA 8", "VTB Group INET CA 4"};
            assertContains(executeSsh("openssl storeutl -text -noout -certs /etc/ssl/certs/ca-certificates.crt | grep VTB"), certs);
        }
    }

    public void checkUserGroupBySsh() {
        String accessGroup = accessGroup();
        assertContains(executeSsh("sudo -u root realm list"), accessGroup);
        assertContains(executeSsh("sudo -u root ls /etc/sudoers.d"), String.format("group_superuser_%s", accessGroup));
    }

    public String executeSsh(SshClient client, String cmd) {
        Assumptions.assumeTrue(isDev(), "Тест включен только для dev среды");
        return client.execute(cmd);
    }

    public String executeSsh(String cmd) {
        return executeSsh(SshClient.builder().host(OrderServiceSteps.getProductsField(this, VM_IP_PATH).toString()).env(envType()).build(), cmd);
    }

    public void addLinkProduct() {
        if (Objects.nonNull(getOrderId())) {
            if (StepsAspects.getCurrentStep().get() != null) {
                Organization org = Organization.builder().type("default").build().createObject();
                StepsAspects.getCurrentStep().get().addLinkItem(
                        new LinkItem("Product URL", String.format("%s/vm/orders/%s/main?context=%s&type=project&org=%s",
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
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).data(new JSONObject().put("dumb", "empty")).build());
    }

    //Перезагрузить
    protected void restart(String action) {
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).build());
    }

    //Выключить принудительно
    protected void stopHard(String action) {
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).status(ProductStatus.STOPPED).build());
    }

    //Выключить
    protected void stopSoft(String action) {
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).status(ProductStatus.STOPPED).build());
    }

    //Включить
    protected void start(String action) {
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).status(ProductStatus.CREATED).build());
    }

    private void checkPreconditionStatusProduct() {
//        Assume.assumeTrue(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), status), getStatus().equals(status));
        if (!ProductStatus.CREATED.equals(getStatus()) && !ProductStatus.DELETED.equals(getStatus())) {
            close();
            throw new CreateEntityException(String.format("Текущий статус продукта %s не соответствует исходному %s", getStatus(), ProductStatus.CREATED));
        }
        String status = OrderServiceSteps.getStatusOrder(getOrderId(), getProjectId());
        if (status.equals("changing") || status.equals("pending")) {
            close();
            throw new CreateEntityException(String.format("Статус продукта %s не соответствует исходному", status));
        }
    }

    public boolean isActionExist(String action) {
        Assumptions.assumeTrue(isDev(), "Тест включен только для dev среды");
        return (Boolean) OrderServiceSteps.getProductsField(this, String.format("data.any{it.actions.name.contains('%s')}", action));
    }

    protected void checkConnectDb(String dbName, String user, String password, String url) throws ConnectException {
        String connectUrl = "jdbc:" + url + "/" + dbName;
        Connection connection;
        try {
            connection = DriverManager.getConnection(connectUrl, user, password);
            Assertions.assertTrue(Objects.requireNonNull(connection, "Подключение не создалось по url: " + connectUrl).isValid(1));
            connection.close();
        } catch (Exception e) {
            connectVmException("Ошибка подключения к " + getProductName() + " по url " + connectUrl + " : " + e);
        }
        log.debug("Успешное подключение к " + getProductName());
    }

    //Удалить рекурсивно
    @Step("Удаление продукта")
    protected void delete(String action) {
        if (envType().contains("prod")) {
            OrderServiceSteps.switchProtect(getOrderId(), getProjectId(), false);
        }
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).expectedStatus("deprovisioned").status(ProductStatus.DELETED).build());
        Assertions.assertEquals(0.0F, CalcCostSteps.getCostByUid(getOrderId(), getProjectId()), 0.0F, "Стоимость после удаления заказа больше 0.0");
        if (Objects.isNull(platform))
            return;
        if (platform.equalsIgnoreCase("vSphere") && Configure.ENV.equalsIgnoreCase("IFT")) {
            GlobalUser user = GlobalUser.builder().role(Role.IPAM).build().createObject();
            List<String> ipList = ((List<String>) OrderServiceSteps.getProductsField(this, "data.data.config.default_v4_address", List.class))
                    .stream().filter(Objects::nonNull).collect(Collectors.toList());

            RequestSpecification specification = RestAssured.given()
                    .baseUri("https://d5-phpipam.oslb-dev01.corp.dev.vtb")
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
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this)
                .data(new JSONObject().put("flavor", new JSONObject(flavor.toString())).put("check_agree", true)).build());
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
        domain = OrderServiceSteps.getDomainByProject(projectId);
        this.link = link;
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
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this)
                .data(new JSONObject().put("flavor", flavor.toJson()).put("warning", new JSONObject()).put("accept", true)).build());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");
    }

    @SneakyThrows
    protected String getRandomOsVersion() {
        Product productResponse = getProductByCloudAdmin(getProductId());
        Graph graphResponse = getGraphByIdAndEnv(productResponse.getGraphId(), envType());
        String urlAttrs = replaceGraphParams(JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getUiSchema().get("os_version")))
                .getString("'ui:options'.attrs.collect{k,v -> k+'='+v }.join('&')"));
        return Objects.requireNonNull(ReferencesStep.getJsonPathList(urlAttrs)
                .getString("collect{it.data.os.version}.shuffled()[0]"), "Версия ОС не найдена");
    }

    @Step("Проверка выполнения условий по ssh на всех nodes")
    public void runOnAllNodesBySsh(Consumer<SshClient> consumer) {
        TypeRef<List<String>> typeReference = new TypeRef<List<String>>() {
        };
        runOnAllNodesBySsh(consumer, OrderServiceSteps.getObjectClass(this, "product_data.hostname", typeReference));
    }

    @Step("Проверка выполнения условий по ssh")
    public void runOnAllNodesBySsh(Consumer<SshClient> consumer, List<String> hosts) {
        for (String host : hosts) {
            Report.checkStep("Проверка выполнения условия по ssh на vm " + host, () -> {
                SshClient client = SshClient.builder().host(host).env(envType()).build();
                consumer.accept(client);
            });
        }
    }

    @Step("Поиск node подходящей под условие")
    public SshClient findNodeBySsh(Predicate<SshClient> predicate, List<String> hosts) {
        for (String host : hosts) {
            SshClient client = SshClient.builder().host(host).env(envType()).build();
            if (predicate.test(client))
                return client;
        }
        throw new NotFoundElementException("Не найдена node соответствующая условию");
    }

    @SneakyThrows
    public String getFilter() {
        Product productResponse = getProductByCloudAdmin(getProductId());
        Graph graphResponse = getGraphByIdAndEnv(productResponse.getGraphId(), envType());
        return replaceGraphParams(JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getUiSchema().get("flavor")))
                .getString("'ui:options'.filter"));
    }

    @SneakyThrows
    protected boolean getSupport() {
        Product productResponse = getProductByCloudAdmin(getProductId());
        Graph graphResponse = getGraphByIdAndEnv(productResponse.getGraphId(), envType());
        Object support = graphResponse.getStaticData().get("on_support");
        if (support instanceof String || Objects.isNull(support)) {
            support = JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getJsonSchema().get("properties")))
                    .getBoolean("on_support.default");
        }
        return (Boolean) Objects.requireNonNull(support, "on_support не найден в графе");
    }

    @SneakyThrows
    protected String getRandomProductVersionByPathEnum(String path) {
        Product productResponse = getProductByCloudAdmin(getProductId());
        Graph graphResponse = getGraphByIdAndEnv(productResponse.getGraphId(), envType());
        return Objects.requireNonNull(JsonPath.from(new ObjectMapper().writeValueAsString(graphResponse.getJsonSchema().get("properties")))
                .getString(path + ".collect{e -> e}.shuffled()[0]"), "Версия продукта не найдена");
    }

    public Flavor getMaxFlavor() {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedListByFilter(this);
        Assertions.assertFalse(list.size() < 2, "Действие недоступно, либо кол-во flavor's < 2");
//        return list.get(list.size() - 1);
        return list.get(1);
    }

    public Flavor getMinFlavor() {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedListByFilter(this);
        return list.get(0);
    }

    public Flavor getMaxFlavorLinuxVm() {
        Project project = Project.builder().id(getProjectId()).build().createObject();
        String filter = String.format("flavor:vm:linux:%s:%s",
                project.getProjectEnvironmentPrefix().getEnvType().toLowerCase(),
                project.getProjectEnvironmentPrefix().getEnv().toLowerCase());
        List<Flavor> list = ReferencesStep.getFlavorsByPageFilterLinkedList(this, filter);
        Assertions.assertFalse(list.size() < 2, "Действие недоступно, либо кол-во flavor's < 2");
//        return list.get(list.size() - 1);
        return list.get(1);
    }

    public Flavor getMinFlavorLinuxVm() {
        Project project = Project.builder().id(getProjectId()).build().createObject();
        String filter = String.format("flavor:vm:linux:%s:%s",
                project.getProjectEnvironmentPrefix().getEnvType().toLowerCase(),
                project.getProjectEnvironmentPrefix().getEnv().toLowerCase());
        List<Flavor> list = ReferencesStep.getFlavorsByPageFilterLinkedList(this, filter);
        return list.get(0);
    }

    //Расширить
    protected void expandMountPoint(String action, String mount, int size) {
        Float sizeBefore = (Float) OrderServiceSteps.getProductsField(this, String.format(EXPAND_MOUNT_SIZE, mount, mount));
        OrderServiceSteps.runAction(ActionParameters.builder().filter(String.format("extra_mounts.find{it.mount == '%s'}", mount))
                .name(action).product(this).data(new JSONObject().put("size", size).put("mount", mount)).build());
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
            productId = new ProductCatalogSteps("/api/v1/projects/" + getProjectId() + "/products/").
                    getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(Objects.requireNonNull(getProductName()),
                            "is_open=true");
        }
        if (productCatalogName == null) {
            productCatalogName = getProductByCloudAdmin(productId).getName();
        }
    }

    protected void createProduct() {
        log.info("Отправка запроса на создание заказа " + productName);
        JSONObject data = deleteObjectIfNotFoundInUiSchema(toJson(), getProductId());
        data = new JsonTemplate(data).set("$.order.lifetime", "172800").build();
        JsonPath jsonPath = new Http(orderServiceURL)
                .setProjectId(projectId, Role.ORDER_SERVICE_ADMIN)
                .body(data)
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
        Product productResponse = getProductByCloudAdmin(productId);
        Graph graphResponse = getGraphByIdAndEnv(productResponse.getGraphId(), envType());
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

    public boolean isDev() {
        if (projectId == null) {
            Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(env)).isForOrders(true).build().createObject();
            setProjectId(project.getId());
        }
        return envType().equalsIgnoreCase("dev");
    }

    public boolean isTest() {
        if (projectId == null) {
            Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(env)).isForOrders(true).build().createObject();
            setProjectId(project.getId());
        }
        return envType().equalsIgnoreCase("test");
    }

    public boolean isProd() {
        if (projectId == null) {
            Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(env)).isForOrders(true).build().createObject();
            setProjectId(project.getId());
        }
        return envType().equalsIgnoreCase("prod");
    }

    public String envType() {
        Project project = Project.builder().id(projectId).build().createObject();
        return project.getProjectEnvironmentPrefix().getEnvType().toLowerCase();
    }

    public void connectVmException(String message) throws ConnectException {
        if (isDev())
            throw new ConnectException(message);
        throw new TestAbortedException("Тест отключен для продуктов в TEST и PROD средах");
    }

}
