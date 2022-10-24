package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
import models.subModels.Db;
import models.subModels.DbUser;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static models.orderService.products.PostgreSQL.DB_CONN_LIMIT;
import static models.orderService.products.PostgreSQL.IS_DB_CONN_LIMIT;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class PostgresSQLCluster extends IProduct {
    private final static String DB_NAME_PATH = "data.find{it.data.config.containsKey('dbs')}.data.config.dbs.any{it.db_name=='%s'}";
    private final static String DB_USERNAME_PATH = "data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}";
    @ToString.Include
    String segment;
    String dataCentre;
    String osVersion;
    @ToString.Include
    String postgresqlVersion;
    String domain;
    @Builder.Default
    public List<Db> database = new ArrayList<>();
    @Builder.Default
    public List<DbUser> users = new ArrayList<>();
    Flavor flavor;
    private String adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/postgressql_cluster.json";
        if (productName == null)
            productName = "PostgreSQL Cluster Astra Linux";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (postgresqlVersion == null)
            postgresqlVersion = getRandomProductVersionByPathEnum("postgresql_version.enum");
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.postgresql_version", postgresqlVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();
    }

    //Расширить pg_data
    public void expandMountPoint() {
        int size = 11;
        String mount = "/pg_data";
        Float sizeBefore = (Float) OrderServiceSteps.getProductsField(this, String.format(EXPAND_MOUNT_SIZE, mount, mount));
        OrderServiceSteps.executeAction("expand_mount_point_postgresql_pgdata", this, new JSONObject("{\"size\": " + size + ", \"mount\": \"" + mount + "\"}"), this.getProjectId());
        float sizeAfter = (Float) OrderServiceSteps.getProductsField(this, String.format(CHECK_EXPAND_MOUNT_SIZE, mount, mount, sizeBefore.intValue()));
        Assertions.assertEquals(sizeBefore, sizeAfter - size, 0.05, "sizeBefore >= sizeAfter");
    }

    public void createDb(String dbName) {
        if (database.contains(new Db(dbName)))
            return;
        OrderServiceSteps.executeAction(getEnv().equalsIgnoreCase("LT")?"postgresql_cluster_create_db":"postgresql_cluster_create_db_nonprod",
                this, new JSONObject(String.format("{conn_limit: -1, db_name: \"%s\", db_admin_pass: \"%s\"}", dbName, adminPassword)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)), "База данных не создалась c именем" + dbName);
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    public void checkConnection(String dbName) {
        checkConnectDb(dbName, dbName + "_admin", adminPassword, ((String) OrderServiceSteps.getProductsField(this, CONNECTION_URL)).split(",")[0]);
    }

    //Удалить БД
    public void removeDb(String dbName) {
        OrderServiceSteps.executeAction("postgresql_cluster_remove_db", this, new JSONObject("{\"db_name\": \"" + dbName + "\"}"), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.removeIf(db -> db.getNameDB().equals(dbName));
        save();
    }

    public void resize(Flavor flavor) {
        resize("resize_postgresql_cluster", flavor);
    }

    public void createDbmsUser(String username, String dbRole, String dbName) {
        OrderServiceSteps.executeAction("postgresql_cluster_create_dbms_user",
                this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ\"}",
                        dbName, dbRole, username)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, String.format("%s_%s", dbName, username))),
                "Имя пользователя отличается от создаваемого");
        users.add(new DbUser(dbName, username));
        log.info("users = " + users);
        save();
    }

    //Сбросить пароль пользователя
    public void resetPassword(String username) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.executeAction("postgresql_cluster_reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", username, password)), this.getProjectId());
    }

    //Сбросить пароль владельца
    public void resetDbOwnerPassword(String dbName) {
        Assertions.assertTrue(database.stream().anyMatch(db -> db.getNameDB().equals(dbName)), String.format("Базы %s не существует", dbName));
        adminPassword = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.executeAction("postgresql_cluster_reset_db_owner_password", this, new JSONObject(String.format("{\"user_name\":\"%S\",\"user_password\":\"%s\"}", dbName + "_admin", adminPassword)), this.getProjectId());
    }

    //Удалить пользователя
    public void removeDbmsUser(String username, String dbName) {
        OrderServiceSteps.executeAction("postgresql_cluster_remove_dbms_user", this, new JSONObject(String.format("{\"user_name\":\"%s\"}", String.format("%s_%s", dbName, username))), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, String.format("%s_%s", dbName, username))),
                String.format("Пользователь: %s не удалился из базы данных: %s", String.format("%s_%s", dbName, username), dbName));
        log.info("users = " + users);
        save();
    }

    public void setConnLimit(String dbName, int count) {
        OrderServiceSteps.executeAction("postgresql_cluster_set_conn_limit", this, new JSONObject().put("db_name", dbName).put("conn_limit", count), this.getProjectId());
        Assertions.assertEquals(count, (Integer) OrderServiceSteps.getProductsField(this, String.format(DB_CONN_LIMIT, dbName)));
    }

    public void restart() {
        restart("reset_two_layer");
    }

    public void stopSoft() {
        stopSoft("stop_two_layer");
    }

    public void start() {
        start("start_two_layer");
    }

    public void stopHard() {
        stopHard("stop_hard_two_layer");
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_postgresql_cluster");
    }
}
