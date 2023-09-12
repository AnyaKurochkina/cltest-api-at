package models.cloud.orderService.products;

import core.helper.StringUtils;
import core.utils.AssertUtils;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Db;
import models.cloud.subModels.DbUser;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static core.utils.AssertUtils.assertContains;

@SuperBuilder
@NoArgsConstructor
@Setter
@Log4j2
public abstract class AbstractPostgreSQL extends IProduct {
    public final static String DB_NAME_PATH = "data.any{it.data.config.db_name=='%s' && it.data.state=='on'}";
    public final static String DB_CONN_LIMIT = "data.find{it.data.config.db_name=='%s'}.data.config.conn_limit";
    public final static String EXTENSIONS_LIST = "data.find{it.data.config.db_name=='%s'}.data.config.extensions";
    //    public final static String IS_DB_CONN_LIMIT = "data.find{it.data.config.db_name=='%s'}.data.config.containsKey('conn_limit')";
    public final static String DB_USERNAME_PATH = "data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}";
    public final static String DB_OWNER_NAME_PATH = "data.find{it.data.config.containsKey('db_owners')}.data.config.db_owners.user_name";
    public final static String MAX_CONNECTIONS = "data.find{it.type=='app' || it.type=='cluster'}.data.config.configuration.max_connections";
    @Builder.Default
    public List<Db> database = new ArrayList<>();
    @Builder.Default
    public List<DbUser> users = new ArrayList<>();
    protected Flavor flavor;
    protected String adminPassword;

    public void createDb(String dbName) {
        if (database.contains(new Db(dbName)))
            return;
        OrderServiceSteps.executeAction("postgresql_create_db", this,
                new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"%s\", conn_limit: -1}", dbName, adminPassword)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)),
                "База данных не создалась c именем " + dbName);
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    public void setConnLimit(String dbName, int count) {
        OrderServiceSteps.executeActionWidthFilter("postgresql_db_set_conn_limit", this, new JSONObject().put("conn_limit", count), this.getProjectId(), filterBd(dbName));
        Assertions.assertEquals(count, (Integer) OrderServiceSteps.getProductsField(this, String.format(DB_CONN_LIMIT, dbName)));
        if (isDev() && this instanceof PostgreSQL)
            Assertions.assertEquals(String.valueOf(count), StringUtils.findByRegex("\\s([0-9]*)\\n\\(",
                    executeSsh(String.format("sudo -iu postgres psql -c \"select datconnlimit from pg_database where datname='%s';\"", dbName))));
    }

    public void removeConnLimit(String dbName) {
        OrderServiceSteps.executeActionWidthFilter("postgresql_db_remove_conn_limit", this, new JSONObject().put("conn_limit", -1), this.getProjectId(), filterBd(dbName));
        Assertions.assertEquals(0, OrderServiceSteps.getProductsField(this, String.format(DB_CONN_LIMIT, dbName)));
    }

    void addMountPoint(String action, String mount) {
        OrderServiceSteps.executeAction(action, this, new JSONObject().put("mount", mount), this.getProjectId());
        float sizeAfter = (Float) OrderServiceSteps.getProductsField(this, String.format(CHECK_EXPAND_MOUNT_SIZE, mount, mount, 0));
        Assertions.assertTrue(sizeAfter > 0);
    }

    public void addMountPointPgAudit() {
        addMountPoint("postgresql_add_mount_point_pg_audit", "/pg_audit");
    }

    public void addMountPointPgBackup() {
        addMountPoint("postgresql_add_mount_point_pg_backup", "/pg_backup");
    }

    public void addMountPointPgWalarchive() {
        addMountPoint("postgresql_add_mount_point_pg_walarchive", "/pg_walarchive");
    }

    public void updateMaxConnections() {
        String loadProfile = (String) OrderServiceSteps.getProductsField(this, "data.find{it.type=='app'}.data.config.load_profile");
        OrderServiceSteps.executeAction("postgresql_update_max_connections", this, new JSONObject().put("load_profile", loadProfile), this.getProjectId());
    }

    public String getCurrentMaxConnections() {
        Object obj = OrderServiceSteps.getProductsField(this, MAX_CONNECTIONS);
        if (obj instanceof Integer)
            obj = String.valueOf(obj);
        return (String) obj;
    }

    abstract void cmdRestartPostgres();

    abstract void cmdSetMaxConnections(int connections);

    public void updateMaxConnectionsBySsh(int connections) {
        cmdSetMaxConnections(connections);
        cmdRestartPostgres();
        getConfiguration();
        Assertions.assertEquals(connections, Integer.valueOf(getCurrentMaxConnections()));
    }

    public int maxConnections() {
        return (int) (118 * Math.log(flavor.getCpus() + 1) * Math.log(flavor.getMemory() + 1));
    }

    public void getConfiguration() {
        OrderServiceSteps.executeAction("postgresql_get_configuration", this, null, this.getProjectId());
    }

    public void updatePostgresql() {
        OrderServiceSteps.executeAction("postgresql_update_postgresql", this, new JSONObject().put("check_agree", true), this.getProjectId());
    }

    public void updateOs() {
        OrderServiceSteps.executeAction("postgresql_update_os", this, new JSONObject().put("check_agree", true), this.getProjectId());
    }

    public void updateExtensions(String dbName, List<String> extensions) {
        OrderServiceSteps.executeActionWidthFilter("postgresql_db_update_extensions", this, new JSONObject()
                .put("extensions_updated", extensions)
                .put("extensions", new ArrayList<>()), this.getProjectId(), filterBd(dbName));
        AssertUtils.assertEqualsList(extensions, OrderServiceSteps.getProductsField(this, String.format(EXTENSIONS_LIST, dbName), List.class));
    }

    public void getExtensions(String dbName, String extension) {
        if (isDev()) {
            String extParameters = (this instanceof PostgreSQL) ? "" : "-p 6432";
            String cmd = String.format("sudo -iu postgres psql %s -d %s -c \"create extension %s with schema %s;\"", extParameters, dbName, extension, dbName);
            assertContains(executeSsh(cmd), "CREATE EXTENSION");
        }
        OrderServiceSteps.executeActionWidthFilter("postgresql_db_get_extensions", this, null, this.getProjectId(), filterBd(dbName));
        if (isDev())
            Assertions.assertTrue(OrderServiceSteps.getProductsField(this, String.format(EXTENSIONS_LIST, dbName), List.class).contains(extension));
    }

    private String filterBd(String dbName) {
        return String.format("db_name=='%s'", dbName);
    }

    //Удалить БД
    public void removeDb(String dbName) {
        OrderServiceSteps.executeActionWidthFilter("postgresql_remove_db", this, null, this.getProjectId(), filterBd(dbName));
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.removeIf(db -> db.getNameDB().equals(dbName));
        save();
    }

    public void createDbmsUser(String action, String username, String dbRole, String dbName) {
        OrderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ\"}", dbName, dbRole, username)), this.getProjectId());
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
    public void resetPassword(String action, String username) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)), this.getProjectId());
    }

    //Сбросить пароль владельца
    public void resetDbOwnerPassword(String action, String username) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)), this.getProjectId());
        this.adminPassword = password;
        save();
    }

    //Изменить default_transaction_isolation
    public void updateDti(String defaultTransactionIsolation) {
        OrderServiceSteps.executeAction("postgresql_update_dti", this,
                new JSONObject(String.format("{\"default_transaction_isolation\":\"%s\"}", defaultTransactionIsolation)), this.getProjectId());
    }

    //Удалить пользователя
    public void removeDbmsUser(String action, String username, String dbName) {
        OrderServiceSteps.executeAction(action, this, new JSONObject(String.format("{\"user_name\":\"%s\"}", String.format("%s_%s", dbName, username))), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, String.format("%s_%s", dbName, username))),
                String.format("Пользователь: %s не удалился из базы данных: %s", String.format("%s_%s", dbName, username), dbName));
        users.remove(new DbUser(dbName, username));
        log.info("users = " + users);
        save();
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
}




