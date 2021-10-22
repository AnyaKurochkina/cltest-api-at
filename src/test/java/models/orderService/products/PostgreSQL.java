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
import models.subModels.Flavor;
import models.subModels.Db;
import models.subModels.DbUser;
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
    public List<Db> database = new ArrayList<>();
    public List<DbUser> users = new ArrayList<>();
    Flavor flavor;

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

    public PostgreSQL() {
        jsonTemplate = "/orders/postgresql.json";
        productName = "PostgreSQL";
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
                .set("$.order.attrs.postgresql_version", postgresqlVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", ((ProjectEnvironment) cacheService.entity(ProjectEnvironment.class).withField("env", project.env).getEntity()).envType.contains("TEST"))
                .build();
    }

    //Расширить
    @Override
    @Action("expand_mount_point")
    public void expandMountPoint(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, EXPAND_MOUNT_SIZE);
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"size\": 10, \"mount\": \"/pg_data\"}"));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore < sizeAfter);
    }

    public void createDb(String dbName, String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq\"}", dbName)));
        String dbNameActual = (String) orderServiceSteps.getProductsField(this, DB_NAME_PATH);
        assertEquals("База данных не создалась именем" + dbName, dbName, dbNameActual);
        database.add(new Db(dbName, false));
        log.info("database = " + database);
        cacheService.saveEntity(this);
    }

    //Добавить БД
    @Action("create_db")
    public void createDbTest(String action) {
        createDb("testdb", action);
    }

    //Удалить БД
    @Action("remove_db")
    public void removeDb(String action) {
        String dbName = database.get(0).getNameDB();
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, DB_SIZE_PATH);
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{db_name: \"%s\"}", dbName)));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, DB_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        database.get(0).setDeleted(true);
        log.info("database = " + database);
        cacheService.saveEntity(this);
    }

    public void createDbmsUser(String username, String dbRole, String action) {
        String dbName = database.get(0).getNameDB();
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ\"}", dbName, dbRole, username)));
        String dbUserNameActual = (String) orderServiceSteps.getProductsField(this, DB_USERNAME_PATH);
        assertEquals("Имя пользователя отличается от создаваемого", dbName+ "_" + username, dbUserNameActual);
        users.add(new DbUser(dbName, dbUserNameActual, false));
        log.info("users = " + users);
        cacheService.saveEntity(this);
    }

    //Добавить пользователя
    @Action("create_dbms_user")
    public void createDbmsUserTest(String action) {
        createDbmsUser("testchelik", "user", action);
    }

    //Сбросить пароль пользователя
    @Action("reset_db_user_password")
    public void resetPassword(String action) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", users.get(0).getUsername(), password)));
    }

    //Сбросить пароль владельца
    @Action("reset_db_owner_password")
    public void resetDbOwnerPassword(String action) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", database.get(0).getNameDB() + "_admin", password)));
    }

    //Удалить пользователя
    @Action("remove_dbms_user")
    public void removeDbmsUser(String action) {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, DB_USERNAME_SIZE_PATH);
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%s\"}", users.get(0).getUsername())));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, DB_USERNAME_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        users.get(0).setDeleted(true);
        log.info("users = " + users);
        cacheService.saveEntity(this);
    }


    @Override
    @Action("postgresql_resize")
    public void resize(String action) {
        List<Flavor> list = referencesStep.getProductFlavorsLinkedList(this);
        Assert.assertTrue("У продукта меньше 2 flavors", list.size() > 1);
        Flavor flavor = list.get(list.size() - 1);
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"flavor\": " + flavor.toString() + ",\"warning\":{}}"));
        int cpusAfter = (Integer) orderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getProductsField(this, MEMORY);
        assertEquals(flavor.data.cpus, cpusAfter);
        assertEquals(flavor.data.memory, memoryAfter);
    }
}




