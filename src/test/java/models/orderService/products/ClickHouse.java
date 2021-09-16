package models.orderService.products;

import core.CacheService;
import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Flavor;
import models.subModels.Db;
import org.json.JSONObject;
import org.junit.Action;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class ClickHouse extends IProduct {
    Flavor flavor;
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String osVersion;
    String domain;
    public List<Db> database = new ArrayList<>();
    public static final String REFRESH_VM_CONFIG = "Актуализировать конфигурацию";
    public static final String CLICKHOUSE_CREATE_DB = "Добавить БД";

    public static String DB_NAME_PATH = "data.find{it.type=='app'}.config.dbs[0].db_name";

    @Override
    public void order() {
        JSONObject template = getJsonParametrizedTemplate();
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = new Http(OrderServiceSteps.URL)
                .setProjectId(projectId)
                .post("order-service/api/v1/projects/" + projectId + "/orders", template)
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        cacheService.saveEntity(this);
    }

    public ClickHouse() {
        jsonTemplate = "/orders/clickhouse.json";
        productName = "ClickHouse";
    }

    @Action(REFRESH_VM_CONFIG)
    public void refreshVmConfig(String action) {
        orderServiceSteps.executeAction(action, this, null);
    }

    @Action(CLICKHOUSE_CREATE_DB)
    public void createDbTest(String action) {
        createDb("db_1", action);
    }

    public void createDb(String dbName, String action) {
        Db db = new Db(dbName, false);
        orderServiceSteps.executeAction(action, this, new JSONObject(CacheService.toJson(db)));
        String dbNameActual = (String) orderServiceSteps.getFiledProduct(this, DB_NAME_PATH);
        assertEquals("База данных не создалась именем" + dbName, dbName, dbNameActual);
        database.add(db);
        cacheService.saveEntity(this);
    }

    @Override
    public JSONObject getJsonParametrizedTemplate() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(true)
                .getEntity();
        if(productId == null) {
            projectId = project.id;
            productId = orderServiceSteps.getProductId(this);
        }
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        List<Flavor> flavorList = referencesStep.getProductFlavorsLinkedList(this);
        flavor = flavorList.get(0);
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", ((ProjectEnvironment) cacheService.entity(ProjectEnvironment.class).withField("env", project.env).getEntity()).envType.contains("TEST"))
                .build();

    }
}
