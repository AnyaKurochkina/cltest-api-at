package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Flavor;
import models.subModels.PostgreSqlDB;
import models.subModels.PostgreSqlUsers;
import org.json.JSONObject;
import org.junit.Action;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class PostgreSQL extends IProduct {
    public static String DB_NAME_PATH = "data.find{it.type=='app'}.config.dbs[0].db_name";
    public static String DB_SIZE_PATH = "data.find{it.type=='app'}.config.dbs.size()";
    public static String DB_USERNAME_PATH = "data.find{it.type=='app'}.config.db_users[0].user_name";
    public static String DB_USERNAME_SIZE_PATH = "data.find{it.type=='app'}.config.db_users.size()";
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String osVersion;
    @ToString.Include
    String postgresqlVersion;
    String domain;
    public List<PostgreSqlDB> database = new ArrayList<>();
    public List<PostgreSqlUsers> users = new ArrayList<>();
    Flavor flavor;

    @Override
    public void order() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        projectId = project.id;
        productId = orderServiceSteps.getProductId(this);
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = new Http(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders",
                        getJsonParametrizedTemplate())
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        cacheService.saveEntity(this);
    }

    public PostgreSQL() {
        jsonTemplate = "/orders/postgresql.json";
        productName = "PostgreSQL";
    }

    @Override
    public JSONObject getJsonParametrizedTemplate() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
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
                .set("$.order.attrs.postgresql_version", postgresqlVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", env.toUpperCase().contains("TEST"))
                .build();
    }

    @Override
    @Action("Удалить рекурсивно")
    public void delete(String action) {
        super.delete(action);
    }

    @Override
    @Action("Расширить")
    public void expandMountPoint(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject("{\"size\": 10, \"mount\": \"/pg_data\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore < sizeAfter);
    }

    public void createDb(String dbName, String action) {
        String actionId = orderServiceSteps.executeAction("Добавить БД", this, new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq\"}", dbName)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String dbNameActual = (String) orderServiceSteps.getFiledProduct(this, DB_NAME_PATH);
        assertEquals("База данных не создалась именем" + dbName, dbName, dbNameActual);
        database.add(new PostgreSqlDB(dbName, false));
        log.info("database = " + database);
        cacheService.saveEntity(this);
    }

    @Action("Добавить БД")
    public void createDbTest(String action) {
        createDb("testdb", action);
    }

    @Action("Удалить БД")
    public void removeDb(String action) {
        String dbName = database.get(0).getNameDB();
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, DB_SIZE_PATH);
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{db_name: \"%s\"}", dbName)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, DB_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        database.get(0).setDeleted(true);
        log.info("database = " + database);
        cacheService.saveEntity(this);
    }

    public void createDbmsUser(String username, String dbRole, String action) {
        String dbName = database.get(0).getNameDB();
        String actionId = orderServiceSteps.executeAction("Добавить пользователя", this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ\"}", dbName, dbRole, username)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String dbUserNameActual = (String) orderServiceSteps.getFiledProduct(this, DB_USERNAME_PATH);
        assertEquals("Имя пользователя отличается от создаваемого", String.format("%s_%s", dbName, username), dbUserNameActual);
        users.add(new PostgreSqlUsers(dbName, dbUserNameActual, false));
        log.info("users = " + users);
        cacheService.saveEntity(this);
    }

    @Action("Добавить пользователя")
    public void createDbmsUserTest(String action) {
        createDbmsUser("testuser", "user", action);
    }

    @Action("Сбросить пароль")
    public void resetPassword(String action) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", users.get(0).getUsername(), password)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Action("Сбросить пароль")
    public void resetDbOwnerPassword(String action) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", database.get(0).getNameDB() + "_admin", password)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Action("Удалить пользователя")
    public void removeDbmsUser(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, DB_USERNAME_SIZE_PATH);
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%s\"}", users.get(0).getUsername())));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, DB_USERNAME_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        users.get(0).setDeleted(true);
        log.info("users = " + users);
        cacheService.saveEntity(this);
    }

}




