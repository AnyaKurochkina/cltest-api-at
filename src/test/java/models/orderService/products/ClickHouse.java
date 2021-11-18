package models.orderService.products;

import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.DbUser;
import models.subModels.Flavor;
import models.subModels.Db;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class ClickHouse extends IProduct {
    Flavor flavor;
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String osVersion;
    String domain;
    @Builder.Default
    public List<Db> database = new ArrayList<>();
    @Builder.Default
    public List<DbUser> users = new ArrayList<>();

    //Проверить конфигурацию
    private static final String REFRESH_VM_CONFIG = "check_vm";
    //Добавить БД
    private static final String CLICKHOUSE_CREATE_DB = "clickhouse_create_db";
    //Удалить БД
    private static final String CLICKHOUSE_DELETE_DB = "clickhouse_remove_db";
    //Добавить пользователя
    private static final String CLICKHOUSE_CREATE_DBMS_USER = "clickhouse_create_dbms_user";
    //Удалить пользователя
    private static final String CLICKHOUSE_DELETE_DBMS_USER = "clickhouse_remove_dbms_user";

    private final static String DB_NAME_PATH = "data.find{it.config.containsKey('dbs')}.config.dbs.any{it.db_name=='%s'}";
//    private final static String DB_SIZE_PATH = "data.find{it.type=='app'}.config.dbs.size()";
    private final static String DB_USERNAME_PATH = "data.find{it.config.containsKey('db_users')}.config.db_users.any{it.user_name=='%s'}";
//    private final static String DB_USERNAME_SIZE_PATH = "data.find{it.type=='app'}.config.db_users.size()";

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
        compareCostOrderAndPrice();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/clickhouse.json";
        productName = "ClickHouse";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if(projectId == null) {
            projectId = project.getId();
        }
        if(productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
        return this;
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void refreshVmConfig() {
        orderServiceSteps.executeAction(REFRESH_VM_CONFIG, this, null);
    }

    public void removeDb(String dbName) {
        orderServiceSteps.executeAction(CLICKHOUSE_DELETE_DB, this, new JSONObject("{\"db_name\": \"" + dbName + "\"}"));
        Assert.assertFalse((Boolean) orderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.removeIf(db -> db.getNameDB().equals(dbName));
        save();
    }

    //Сбросить пароль
    public void resetPassword(String username) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction("clickhouse_reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)));
    }

    public void removeDbmsUser(String username, String dbName) {
        orderServiceSteps.executeAction(CLICKHOUSE_DELETE_DBMS_USER, this, new JSONObject(String.format("{\"user_name\":\"%s\"}", username)));
        Assertions.assertFalse((Boolean) orderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, username)),
                String.format("Пользователь: %s не удалился из базы данных: %s",  username, dbName));
        log.info("users = " + users);
        save();
    }

    public void createDb(String dbName) {
        orderServiceSteps.executeAction(CLICKHOUSE_CREATE_DB, this, new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq\"}", dbName)));
        Assert.assertTrue("База данных не создалась c именем " + dbName,
                (Boolean) orderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.add(new Db(dbName, false));
        log.info("database = " + database);
        save();
    }

    public void createDbmsUser(String username, String dbRole, String dbName) {
        orderServiceSteps.executeAction(CLICKHOUSE_CREATE_DBMS_USER,
                this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"txLhQ3UoykznQ2i2qD_LEMUQ_-U\"}",
                        dbName, dbRole, username)));
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, username)),
                "Имя пользователя отличается от создаваемого");
        users.add(new DbUser(dbName, username, false));
        log.info("users = " + users);
        save();
    }

    public void expandMountPoint(){
        expandMountPoint("expand_mount_point", "/app", 10);
    }
    //Перезагрузить по питанию
    public void restart() {
        restart("reset_two_layer");
    }
    //Выключить принудительно
    public void stopHard() {
        stopHard("stop_hard_two_layer");
    }

    //Выключить
    public void stopSoft() {
        stopSoft("stop_two_layer");
    }

    //Включить
    public void start() {
        start("start_two_layer");
    }

//    @Override
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
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .build();

    }
}
