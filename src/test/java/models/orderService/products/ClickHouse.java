package models.orderService.products;

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
import models.subModels.DbUser;
import models.subModels.Flavor;
import models.subModels.Db;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public List<DbUser> users = new ArrayList<>();

    //Проверить конфигурацию
    public static final String REFRESH_VM_CONFIG = "check_vm";
    //Создать БД
    public static final String CLICKHOUSE_CREATE_DB = "clickhouse_create_db";
    //Удалить БД
    public static final String CLICKHOUSE_DELETE_DB = "clickhouse_remove_db";
    //Добавить пользователя
    public static final String CLICKHOUSE_CREATE_DBMS_USER = "clickhouse_create_dbms_user";
    //Удалить пользователя
    public static final String CLICKHOUSE_DELETE_DBMS_USER = "clickhouse_remove_dbms_user";

    public static String DB_NAME_PATH = "data.find{it.type=='app'}.config.dbs.any{it.db_name=='%s'}";
    public static String DB_SIZE_PATH = "data.find{it.type=='app'}.config.dbs.size()";
    public static String DB_USERNAME_PATH = "data.find{it.type=='app'}.config.db_users.any{it.user_name=='%s'}";
    public static String DB_USERNAME_SIZE_PATH = "data.find{it.type=='app'}.config.db_users.size()";

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

    //Удалить рекурсивно
    @Action("delete_two_layer")
    public void delete(String action) {
        super.delete(action);
    }

    @Action(REFRESH_VM_CONFIG)
    public void refreshVmConfig(String action) {
        orderServiceSteps.executeAction(action, this, null);
    }

    @Action(CLICKHOUSE_DELETE_DB)
    private void deleteDb(String action) {
        String dbName = database.get(0).getNameDB();
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, DB_SIZE_PATH);
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{db_name: \"%s\"}", dbName)));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, DB_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        database.get(0).setDeleted(true);
        cacheService.saveEntity(this);
    }


    //Сбросить пароль
    @Action("clickhouse_reset_db_user_password")
    public void resetPassword(String action) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", users.get(0).getUsername(), password)));
    }

    @Action(CLICKHOUSE_DELETE_DBMS_USER)
    private void deleteDbmsUser(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, DB_USERNAME_SIZE_PATH);
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%s\"}", users.get(0).getUsername())));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, DB_USERNAME_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        users.get(0).setDeleted(true);
        log.info("users = " + users);
        cacheService.saveEntity(this);
    }

    @Action(CLICKHOUSE_CREATE_DB)
    public void createDbTest(String action) {
        createDb("db_1", action);
    }

    @Action(CLICKHOUSE_CREATE_DBMS_USER)
    public void createDbmsUserTest(String action) {
        createDbmsUser("chelik", "txLhQ3UoykznQ2i2qD_LEMUQ_-U", action);
    }

    public void createDb(String dbName, String action) {
        Db db = new Db(dbName, false);
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"db_name\":\"%s\"}", dbName)));
        Assert.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.add(db);
        cacheService.saveEntity(this);
    }

    public void createDbmsUser(String username, String password, String action) {
        String dbName = database.get(0).getNameDB();
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"db_name\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\"}", dbName, username, password)));
        Assert.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(DB_USERNAME_PATH, username)));
        users.add(new DbUser(dbName, username, false));
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
