package models.cloud.orderService.products;

import core.helper.StringUtils;
import core.utils.AssertUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Db;
import models.cloud.subModels.DbUser;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static core.utils.AssertUtils.assertContains;

@SuperBuilder
@NoArgsConstructor
@Setter
@Getter
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

    public abstract String pgcHost();

    public void createDb(String dbName) {
        if (database.contains(new Db(dbName)))
            return;
        if (getEnv().equalsIgnoreCase("LT") || isProd()) {
            JSONObject data = new JSONObject().put("db_name", dbName).put("db_admin_pass", adminPassword).put("conn_limit", 11);
            OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_create_db_lt_prod").product(this).data(data).build());
        } else {
            JSONObject data = new JSONObject().put("db_name", dbName).put("db_admin_pass", adminPassword).put("conn_limit", -1);
            OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_create_db").product(this).data(data).build());
        }
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)),
                "База данных не создалась c именем " + dbName);
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    public void setConnLimit(String dbName, int count) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_db_set_conn_limit").product(this)
                .data(new JSONObject().put("conn_limit", count)).filter(filterBd(dbName)).build());
        Assertions.assertEquals(count, (Integer) OrderServiceSteps.getProductsField(this, String.format(DB_CONN_LIMIT, dbName)));
        if (isDev() && this instanceof PostgreSQL)
            Assertions.assertEquals(String.valueOf(count), StringUtils.findByRegex("\\s([0-9]*)\\n\\(",
                    executeSsh(String.format("sudo -iu postgres psql -c \"select datconnlimit from pg_database where datname='%s';\"", dbName))));
    }

    public void removeConnLimit(String dbName) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_db_remove_conn_limit").product(this)
                .data(new JSONObject().put("conn_limit", -1)).filter(filterBd(dbName)).build());
        Assertions.assertEquals(0, OrderServiceSteps.getProductsField(this, String.format(DB_CONN_LIMIT, dbName)));
    }

    void addMountPoint(String action, String mount) {
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).data(new JSONObject().put("mount", mount).put("check_agree", true)).build());
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_update_max_connections").product(this)
                .data(new JSONObject().put("load_profile", loadProfile)).build());
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_get_configuration").product(this).build());
    }

    public void updatePostgresql() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_update_postgresql").product(this).data(new JSONObject().put("check_agree", true)).build());
    }

    public void updateOs() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_update_os").product(this).data(new JSONObject().put("check_agree", true)).build());
    }

    public void updateExtensions(String dbName, List<String> extensions) {
        JSONObject data = new JSONObject().put("extensions_updated", extensions).put("extensions", new ArrayList<>());
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_db_update_extensions").product(this)
                .data(data).filter(filterBd(dbName)).build());
        AssertUtils.assertEqualsList(extensions, OrderServiceSteps.getProductsField(this, String.format(EXTENSIONS_LIST, dbName), List.class));
    }

    public void getExtensions(String dbName, String extension) {
        if (isDev()) {
            String extParameters = (this instanceof PostgreSQL) ? "" : "-p 6432";
            String cmd = String.format("sudo -iu postgres psql %s -d %s -c \"create extension %s with schema %s;\"", extParameters, dbName, extension, dbName);
            assertContains(executeSsh(cmd), "CREATE EXTENSION");
        }
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_db_get_extensions").product(this).filter(filterBd(dbName)).build());
        if (isDev())
            Assertions.assertTrue(OrderServiceSteps.getProductsField(this, String.format(EXTENSIONS_LIST, dbName), List.class).contains(extension));
    }

    private String filterBd(String dbName) {
        return String.format("db_name=='%s'", dbName);
    }

    //Удалить БД
    public void removeDb(String dbName) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_remove_db").product(this).filter(filterBd(dbName)).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.removeIf(db -> db.getNameDB().equals(dbName));
        save();
    }

    public void createDbmsUser(String action, String username, String dbRole, String dbName) {
        JSONObject data = new JSONObject().put("comment", "testapi").put("db_name", dbName).put("dbms_role", dbRole)
                .put("user_name", username).put("user_password", "pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ");
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).data(data).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, String.format("%s_%s", dbName, username))),
                "Имя пользователя отличается от создаваемого");
        users.add(new DbUser(dbName, username));
        log.info("users = " + users);
        save();
    }

    //Сбросить пароль пользователя
    public void resetPassword(String action, String username) {
        String password = "Wx1QA9SI4AzW6AWx1QAWI4AzW6AvJZ9SI4AvJZvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this)
                .data(new JSONObject().put("user_name", username).put("user_password", password)).build());
    }

    //Сбросить пароль владельца
    public void resetDbOwnerPassword(String action, String username) {
        String password = "Wx1QA9SI4AzW6AWx1QAWI4AzW6AvJZ9SI4AvJZvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this)
                .data(new JSONObject().put("user_name", username).put("user_password", password)).build());
        this.adminPassword = password;
        save();
    }

    //Изменить default_transaction_isolation
    public void updateDti(String defaultTransactionIsolation) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("postgresql_update_dti").product(this)
                .data(new JSONObject().put("default_transaction_isolation", defaultTransactionIsolation)).build());
    }

    //Удалить пользователя
    public void removeDbmsUser(String action, String username, String dbName) {
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this)
                .data(new JSONObject().put("user_name", String.format("%s_%s", dbName, username))).build());
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




