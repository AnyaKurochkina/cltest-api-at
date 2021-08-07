package models.orderService.products;

import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.subModels.PostgreSqlDB;
import models.subModels.PostgreSqlUsers;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Log4j2
@SuperBuilder
public class PostgreSQL extends IProduct {
    public static String DB_NAME_PATH = "data.find{it.type=='app'}.config.dbs[0].db_name";
    public static String DB_SIZE_PATH = "data.find{it.type=='app'}.config.dbs.size()";
    public static String DB_USERNAME_PATH = "data.find{it.type=='app'}.config.db_users[0].user_name";
    public static String DB_USERNAME_SIZE_PATH = "data.find{it.type=='app'}.config.db_users.size()";
    String segment;
    String dataCentre;
    String platform;
    String osVersion;
    String postgresqlVersion;
    String domain;
    @Builder.Default
    String status = "NOT_CREATED";
    @Builder.Default
    boolean isDeleted = false;
    @Builder.Default
    public List<PostgreSqlDB> database = new ArrayList<>();
    @Builder.Default
    public List<PostgreSqlUsers> users = new ArrayList<>();

    @Override
    public void order() {
        productName = "PostgreSQL";
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        projectId = project.id;
        productId = orderServiceSteps.getProductId(this);
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = jsonHelper.getJsonTemplate("/orders/" + productName.toLowerCase() + ".json")
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.postgresql_version", postgresqlVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .send(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        status = "CREATED";
        cacheService.saveEntity(this);
    }

    @Override
    public void delete() {
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void expandMountPoint() {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction("Расширить", "{\"size\": 10, \"mount\": \"/pg_data\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore<sizeAfter);
    }

    public void createDb(String dbName) {
        String actionId = orderServiceSteps.executeAction("Добавить БД", String.format("{db_name: \"%s\", db_admin_pass: \"KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq\"}", dbName), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String dbNameActual = (String) orderServiceSteps.getFiledProduct(this, DB_NAME_PATH);
        assertEquals("База данных не создалась именем" + dbName, dbName, dbNameActual);
        database.add(new PostgreSqlDB(dbName, false));
        log.info("database = " + database);
        cacheService.saveEntity(this);
    }

    public void removeDb() {
        String dbName = database.get(0).getNameDB();
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, DB_SIZE_PATH);
        String actionId = orderServiceSteps.executeAction("Удалить БД", String.format("{db_name: \"%s\"}", dbName), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, DB_SIZE_PATH);
        assertTrue(sizeBefore>sizeAfter);
        database.get(0).setDeleted(true);
        log.info("database = " + database);
        cacheService.saveEntity(this);
    }

    public void createDbmsUser(String username, String dbRole) {
        String dbName = database.get(0).getNameDB();
        String actionId = orderServiceSteps.executeAction("Добавить пользователя", String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ\"}", dbName, dbRole, username), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String dbUserNameActual = (String) orderServiceSteps.getFiledProduct(this, DB_USERNAME_PATH);
        assertEquals("Имя пользователя отличается от создаваемого", String.format("%s_%s", dbName, username), dbUserNameActual);
        users.add(new PostgreSqlUsers(dbName, dbUserNameActual,false));
        log.info("users = " + users);
        cacheService.saveEntity(this);
    }


    public void resetPassword() {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        String actionId = orderServiceSteps.executeAction("Сбросить пароль", String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", users.get(0).getUsername(), password), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }


    public void resetDbOwnerPassword() {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        String actionId = orderServiceSteps.executeAction("Сбросить пароль", String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", database.get(0).getNameDB() + "_admin", password), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void removeDbmsUser() {
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, DB_USERNAME_SIZE_PATH);
        String actionId = orderServiceSteps.executeAction("Удалить пользователя", String.format("{\"user_name\":\"%s\"}", users.get(0).getUsername()), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, DB_USERNAME_SIZE_PATH);
        assertTrue(sizeBefore>sizeAfter);
        users.get(0).setDeleted(true);
        log.info("users = " + users);
        cacheService.saveEntity(this);
    }

    @Override
    public String toString() {
        return "PostgreSQL {" +
                "env='" + env + '\'' +
                ", segment='" + segment + '\'' +
                ", dataCentre='" + dataCentre + '\'' +
                ", platform='" + platform + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", postgreSQL='" + postgresqlVersion + '\'' +
                '}';
    }
}
