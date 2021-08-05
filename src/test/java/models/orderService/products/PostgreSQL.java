package models.orderService.products;

import core.helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.subModels.PostgreSqlDB;
import models.subModels.Role;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Log4j2
@Builder
public class PostgreSQL extends Entity implements IProduct {
    public static String DB_NAME_PATH = "data.find{it.type=='app'}.config.dbs[0].db_name";
    public static String DB_SIZE_PATH = "data.find{it.type=='app'}.config.dbs.size()";
    public String env;
    public String segment;
    public String dataCentre;
    public String platform;
    public String osVersion;
    public String postgresql_version;
    public String orderId;
    public String projectId;
    public String productId;
    public String domain;
    @Builder.Default
    public String productName = "PostgreSQL";
    @Builder.Default
    public String status = "NOT_CREATED";
    @Builder.Default
    public boolean isDeleted = false;
    @Builder.Default
    public List<PostgreSqlDB> database = new ArrayList<>();

    @Override
    public void order() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        JsonHelper jsonHelper = new JsonHelper();
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
                .set("$.order.attrs.postgresql_version", postgresql_version)
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
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void expand_mount_point() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        String actionId = orderServiceSteps.executeAction("Расширить", "{\"size\": 10, \"mount\": \"/pg_data\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, EXPAND_MOUNT_SIZE);
        assertTrue(sizeBefore<sizeAfter);
    }

    public void create_db(String db_name) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Добавить БД", String.format("{db_name: \"%s\", db_admin_pass: \"KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq\"}", db_name), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String db_name_actual = (String) orderServiceSteps.getFiledProduct(this, DB_NAME_PATH);
        assertEquals("База данных не создалась именем" + db_name, db_name, db_name_actual);
        database.add(new PostgreSqlDB(db_name, false));
        log.info("database = " + database);
        cacheService.saveEntity(this);
    }

    public void remove_db() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String db_name = database.get(0).getNameDB();
        int sizeBefore = (Integer) orderServiceSteps.getFiledProduct(this, DB_SIZE_PATH);
        String actionId = orderServiceSteps.executeAction("Удалить БД", String.format("{db_name: \"%s\"}", db_name), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = (Integer) orderServiceSteps.getFiledProduct(this, DB_SIZE_PATH);
        assertTrue(sizeBefore>sizeAfter);
        database.get(0).setDeleted(true);
        log.info("database = " + database);
        cacheService.saveEntity(this);
    }

    @Override
    public String getOrderId(){
        return orderId;
    }

    @Override
    public String getProductName(){
        return productName;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public String getEnv() {
        return env;
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return "PostgreSQL {" +
                "env='" + env + '\'' +
                ", segment='" + segment + '\'' +
                ", dataCentre='" + dataCentre + '\'' +
                ", platform='" + platform + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", postgreSQL='" + postgresql_version + '\'' +
                '}';
    }
}
