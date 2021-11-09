package models.orderService.products;

import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
    public List<Db> database = new ArrayList<>();
    public List<DbUser> users = new ArrayList<>();

    //Проверить конфигурацию
    public static final String REFRESH_VM_CONFIG = "check_vm";
    //Добавить БД
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
        jsonTemplate = "/orders/clickhouse.json";
        productName = "ClickHouse";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if(projectId == null) {
            projectId = project.getId();
        }
        if(productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void refreshVmConfig() {
        orderServiceSteps.executeAction(REFRESH_VM_CONFIG, this, null);
    }

    public void removeDb() {
        String dbName = database.get(0).getNameDB();
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, DB_SIZE_PATH);
        orderServiceSteps.executeAction(CLICKHOUSE_DELETE_DB, this, new JSONObject(String.format("{db_name: \"%s\"}", dbName)));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, DB_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        database.get(0).setDeleted(true);
        save();
    }

    //Сбросить пароль
    public void resetPassword() {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction("clickhouse_reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", users.get(0).getUsername(), password)));
    }

    public void removeDbmsUser() {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, DB_USERNAME_SIZE_PATH);
        orderServiceSteps.executeAction(CLICKHOUSE_DELETE_DBMS_USER, this, new JSONObject(String.format("{\"user_name\":\"%s\"}", users.get(0).getUsername())));
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, DB_USERNAME_SIZE_PATH);
        assertTrue(sizeBefore > sizeAfter);
        users.get(0).setDeleted(true);
        log.info("users = " + users);
        save();
    }

    public void createDb(String dbName) {
        Db db = new Db(dbName, false);
        orderServiceSteps.executeAction(CLICKHOUSE_CREATE_DB, this, new JSONObject(String.format("{\"db_name\":\"%s\"}", dbName)));
        Assert.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.add(db);
        save();
    }

    public void createDbmsUser(String username, String password) {
        String dbName = database.get(0).getNameDB();
        orderServiceSteps.executeAction(CLICKHOUSE_CREATE_DBMS_USER, this, new JSONObject(String.format("{\"db_name\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\"}", dbName, username, password)));
        Assert.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(DB_USERNAME_PATH, username)));
        users.add(new DbUser(dbName, username, false));
        save();
    }

    public void expandMountPoint(){
        expandMountPoint("expand_mount_point");
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
