package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.utils.ssh.SshClient;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Db;
import models.cloud.subModels.DbUser;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

import java.util.ArrayList;
import java.util.List;

import static core.utils.AssertUtils.assertContains;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class PostgreSQL extends IProduct {
    private final static String DB_NAME_PATH = "data.find{it.data.config.containsKey('dbs')}.data.config.dbs.any{it.db_name=='%s'}";
    public final static String DB_CONN_LIMIT = "data.find{it.data.config.containsKey('dbs')}.data.config.dbs.find{it.db_name=='%s'}.conn_limit";
    public final static String IS_DB_CONN_LIMIT = "data.find{it.data.config.containsKey('dbs')}.data.config.dbs.find{it.db_name=='%s'}.containsKey('conn_limit')";
    private final static String DB_USERNAME_PATH = "data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}";
    private final static String DB_OWNER_NAME_PATH = "data.find{it.data.config.containsKey('db_owners')}.data.config.db_owners.user_name";
    //    private final static String DB_USERNAME_SIZE_PATH = "data.find{it.type=='app'}.config.db_users.size()";
    String osVersion;
    @ToString.Include
    String postgresqlVersion;
    @Builder.Default
    public List<Db> database = new ArrayList<>();
    @Builder.Default
    public List<DbUser> users = new ArrayList<>();
    Flavor flavor;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/postgresql.json";
        if (productName == null)
            productName = "PostgreSQL (Astra Linux)";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (postgresqlVersion == null)
            postgresqlVersion = getRandomProductVersionByPathEnum("postgresql_version.enum");
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (dataCentre == null)
            setDataCentre(OrderServiceSteps.getDataCentre(this));
        if (platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        String accessGroup = getAccessGroup();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.postgresql_version", postgresqlVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", !isDev())
                .set("$.order.label", getLabel())
                .build();
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/pg_data", 10);
    }

    public void createDb(String dbName, String dbAdminPass) {
        if (database.contains(new Db(dbName)))
            return;

        OrderServiceSteps.executeAction("create_db", this,
                new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"%s\"}", dbName, dbAdminPass)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)),
                "База данных не создалась c именем " + dbName);
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    public void createNonProd(String dbName, String dbAdminPass) {
        if (database.contains(new Db(dbName)))
            return;

        OrderServiceSteps.executeAction("create_db_nonprod", this,
                new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"%s\", conn_limit: -1}", dbName, dbAdminPass)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)),
                "База данных не создалась c именем " + dbName);
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    public void setConnLimit(String dbName, int count) {
        OrderServiceSteps.executeAction("set_conn_limit", this, new JSONObject().put("db_name", dbName).put("conn_limit", count), this.getProjectId());
        Assertions.assertEquals(count, (Integer) OrderServiceSteps.getProductsField(this, String.format(DB_CONN_LIMIT, dbName)));
        if (isDev())
            Assertions.assertEquals(String.valueOf(count), StringUtils.findByRegex("\\s(.*)\\n\\(",
                    executeSsh("psql -c \"select datconnlimit from pg_database where datname='dbname';\"")));
    }

    public void removeConnLimit(String dbName) {
        OrderServiceSteps.executeAction("remove_conn_limit", this, new JSONObject().put("db_name", dbName).put("conn_limit", -1), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(IS_DB_CONN_LIMIT, dbName)));
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

    public String getIp() {
        return ((String) OrderServiceSteps.getProductsField(this, "data.find{it.type=='vm'}.data.config.default_v4_address"));
    }

    //Сбросить пароль пользователя
    public void resetPassword(String username) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.executeAction("reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)), this.getProjectId());
    }

    //Сбросить пароль владельца
    public void resetDbOwnerPassword(String username) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.executeAction("reset_db_owner_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)), this.getProjectId());
    }

    //Изменить default_transaction_isolation
    public void updateDti(String defaultTransactionIsolation) {
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
        OrderServiceSteps.executeAction("resize_two_layer", this, new JSONObject("{\"flavor\": " + flavor.toString() + ",\"warning\":{}}").put("check_agree", true), this.getProjectId());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter);
        Assertions.assertEquals(flavor.data.memory, memoryAfter);
    }

    @SneakyThrows
    public void checkConnection(String dbName, String password) {
        checkConnectDb(dbName, dbName + "_admin", password, ((String) OrderServiceSteps.getProductsField(this, CONNECTION_URL)));
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

    public void checkUseSsh(String ip, String dbName, String adminPassword) {
        String cmd = "psql \"host=localhost dbname=" + dbName +
                " user=" + dbName + "_admin password=" + adminPassword +
                "\" -c \"\\pset pager off\" -c \"CREATE TABLE test1 (name varchar(30), surname varchar(30));\" -c \"\\z " + dbName + ".test1\"";
        assertContains(executeSsh(cmd), dbName + "_user=arwd/" + dbName + "_admin",
                dbName + "_reader=r/" + dbName + "_admin", dbName + "_admin=arwdDxt/" + dbName + "_admin");
    }
}




