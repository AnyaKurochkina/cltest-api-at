package models.orderService.products;

import core.helper.Http;
import io.qameta.allure.Step;
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
import models.subModels.Db;
import models.subModels.DbUser;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class PostgresPro extends IProduct {
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
    String postgresproVersion;
    String domain;
    public List<Db> database = new ArrayList<>();
    public List<DbUser> users = new ArrayList<>();
    Flavor flavor;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = new Http(OrderServiceSteps.URL)
                .setProjectId(projectId)
                .post("order-service/api/v1/projects/" + projectId + "/orders", toJson())
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
    }

    @Override
    public void init() {
        jsonTemplate = "/orders/postgresPro.json";
        productName = "PostgresPro";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if(projectId == null) {
            projectId = project.getId();
        }
        if(productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
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
                .set("$.order.attrs.postgrespro_version", postgresproVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .build();
    }
    
    @Override
    protected void delete() {
        delete("Удалить рекурсивно");
    }

    //Расширить
    public void expandMountPoint() {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, EXPAND_MOUNT_SIZE);
        orderServiceSteps.executeAction("expand_mount_point", this, new JSONObject("{\"size\": 10, \"mount\": \"/pg_data\"}"));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore < sizeAfter);
    }

    public void createDb(String dbName, String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq\"}", dbName)));
        String dbNameActual = (String) orderServiceSteps.getProductsField(this, DB_NAME_PATH);
        assertEquals("База данных не создалась именем" + dbName, dbName, dbNameActual);
        database.add(new Db(dbName, false));
        log.info("database = " + database);
        save();
    }

    //Добавить БД
    public void createDbTest() {
        createDb("testdb", "create_db");
    }

    //Удалить БД
    public void removeDb() {
        String dbName = database.get(0).getNameDB();
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, DB_SIZE_PATH);
        orderServiceSteps.executeAction("remove_db", this, new JSONObject(String.format("{db_name: \"%s\"}", dbName)));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, DB_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        database.get(0).setDeleted(true);
        log.info("database = " + database);
        save();
    }

    public void createDbmsUser(String username, String dbRole, String action) {
        String dbName = database.get(0).getNameDB();
        orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ\"}", dbName, dbRole, username)));
        String dbUserNameActual = (String) orderServiceSteps.getProductsField(this, DB_USERNAME_PATH);
        assertEquals("Имя пользователя отличается от создаваемого", dbName+ "_" + username, dbUserNameActual);
        users.add(new DbUser(dbName, dbUserNameActual, false));
        log.info("users = " + users);
        save();
    }

    //Добавить пользователя
    public void createDbmsUserTest() {
        createDbmsUser("testchelik", "user", "create_dbms_user");
    }

    //Сбросить пароль пользователя
    public void resetPassword() {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction("reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", users.get(0).getUsername(), password)));
    }

    //Сбросить пароль владельца
    public void resetDbOwnerPassword() {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction("reset_db_owner_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", database.get(0).getNameDB() + "_admin", password)));
    }

    //Удалить пользователя
    public void removeDbmsUser() {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, DB_USERNAME_SIZE_PATH);
        orderServiceSteps.executeAction("remove_dbms_user", this, new JSONObject(String.format("{\"user_name\":\"%s\"}", users.get(0).getUsername())));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, DB_USERNAME_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        users.get(0).setDeleted(true);
        log.info("users = " + users);
        save();
    }

    //Изменить конфигурацию
    @Override
    public void resize() {
        List<Flavor> list = referencesStep.getProductFlavorsLinkedList(this);
        Assert.assertTrue("У продукта меньше 2 flavors", list.size() > 1);
        Flavor flavor = list.get(list.size() - 1);
        orderServiceSteps.executeAction("resize_two_layer", this, new JSONObject("{\"flavor\": " + flavor.toString() + ",\"warning\":{}}"));
        int cpusAfter = (Integer) orderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getProductsField(this, MEMORY);
        assertEquals(flavor.data.cpus, cpusAfter);
        assertEquals(flavor.data.memory, memoryAfter);
    }

}




