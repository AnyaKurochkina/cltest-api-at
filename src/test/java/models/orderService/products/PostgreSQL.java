package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.portalBack.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.subModels.Db;
import models.subModels.DbUser;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class PostgreSQL extends IProduct {
    private final static String DB_NAME_PATH = "data.find{it.data.config.containsKey('dbs')}.data.config.dbs.any{it.db_name=='%s'}";
    //    private final static String DB_SIZE_PATH = "data.find{it.type=='app'}.config.dbs.size()";
    private final static String DB_USERNAME_PATH = "data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}";
    private final static String DB_OWNER_NAME_PATH = "data.find{it.data.config.containsKey('db_owners')}.data.config.db_owners.user_name";
    private final static String DB_CONNECTION_URL = "data.find{it.data.config.containsKey('connection_url')}.data.config.connection_url";
    //    private final static String DB_USERNAME_SIZE_PATH = "data.find{it.type=='app'}.config.db_users.size()";
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String osVersion;
    @ToString.Include
    String postgresqlVersion;
    String domain;
    @Builder.Default
    public List<Db> database = new ArrayList<>();
    @Builder.Default
    public List<DbUser> users = new ArrayList<>();
    Flavor flavor;
    String dbAdminPass;
    //URL example = jdbc:postgresql://dhzorg-pgc001ln.corp.dev.vtb:5432/createdb12345
    String dbUrl;
    String dbAdminUser;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/postgresql.json";
        productName = "PostgreSQL";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (postgresqlVersion == null)
            postgresqlVersion = getRandomProductVersionByPathEnum("postgresql_version.enum");
        if(dataCentre == null)
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
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.postgresql_version", postgresqlVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .set("$.order.label", getLabel())
                .build();
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point", "/pg_data", 10);
    }

    public void createDb(String dbName) {
        if (database.contains(new Db(dbName)))
            return;
        dbAdminPass = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";
        OrderServiceSteps.executeAction("create_db", this,
                new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"%s\"}", dbName, dbAdminPass)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)),
                "База данных не создалась c именем " + dbName);
        dbAdminUser = dbName + "_admin";
        dbUrl = "jdbc:" + OrderServiceSteps.getProductsField(this, DB_CONNECTION_URL) + "/" + dbName;
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    //Удалить БД
    public void removeDb(String dbName) {
        OrderServiceSteps.executeAction("remove_db", this, new JSONObject("{\"db_name\": \"" + dbName + "\"}"), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.removeIf(db -> db.getNameDB().equals(dbName));
        save();
    }

    public void createDbmsUser(String username, String dbRole, String dbName) {
        OrderServiceSteps.executeAction("create_dbms_user", this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ\"}", dbName, dbRole, username)), this.getProjectId());
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
        OrderServiceSteps.executeAction("reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)), this.getProjectId());
    }

    //Изменить default_transaction_isolation
    public void updateDti(String username) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.executeAction("reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)), this.getProjectId());
    }

    //Сбросить пароль владельца
    public void resetDbOwnerPassword(String defaultTransactionIsolation) {
        OrderServiceSteps.executeAction("postgresql_update_dti", this,
                new JSONObject(String.format("{\"default_transaction_isolation\":\"%s\"}", defaultTransactionIsolation)), this.getProjectId());
    }

    //Удалить пользователя
    public void removeDbmsUser(String username, String dbName) {
        OrderServiceSteps.executeAction("remove_dbms_user", this, new JSONObject(String.format("{\"user_name\":\"%s\"}", String.format("%s_%s", dbName, username))), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(
                this, String.format(DB_USERNAME_PATH, String.format("%s_%s", dbName, username))),
                String.format("Пользователь: %s не удалился из базы данных: %s", String.format("%s_%s", dbName, username), dbName));
        users.remove(new DbUser(dbName, username));
        log.info("users = " + users);
        save();
    }

    public void resize(Flavor flavor) {
        OrderServiceSteps.executeAction("resize_two_layer", this, new JSONObject("{\"flavor\": " + flavor.toString() + ",\"warning\":{}}"), this.getProjectId());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter);
        Assertions.assertEquals(flavor.data.memory, memoryAfter);
    }

    @SneakyThrows
    public void checkConnection(String url, String user, String password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            Assertions.assertTrue(connection.isValid(1));
        }catch (Throwable t){
            t.printStackTrace();
        }  finally {
            assert connection != null;
            connection.close();
        }
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

    public void updateMaxConnections(String loadProfile, int maxConnections) {
        OrderServiceSteps.executeAction("postgresql_update_max_connections", this,
                new JSONObject(String.format("{\"load_profile\":\"%s\", max_connections: %d}", loadProfile, maxConnections)), this.getProjectId());
    }
}




