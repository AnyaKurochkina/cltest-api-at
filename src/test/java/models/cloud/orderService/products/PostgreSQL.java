package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.utils.AssertUtils;
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
    private final static String DB_NAME_PATH = "data.any{it.data.config.db_name=='%s'}";
    public final static String DB_CONN_LIMIT = "data.find{it.data.config.db_name=='%s'}.data.config.conn_limit";
    public final static String EXTENSIONS_LIST = "data.find{it.data.config.db_name=='%s'}.data.config.extensions";
    public final static String IS_DB_CONN_LIMIT = "data.find{it.data.config.db_name=='%s'}.data.config.containsKey('conn_limit')";
    private final static String DB_USERNAME_PATH = "data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}";
    private final static String DB_OWNER_NAME_PATH = "data.find{it.data.config.containsKey('db_owners')}.data.config.db_owners.user_name";
    private final static String MAX_CONNECTIONS = "data.find{it.type=='app'}.data.config.configuration.max_connections";
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

    public void createNonProd(String dbName, String dbAdminPass) {
        if (database.contains(new Db(dbName)))
            return;

        OrderServiceSteps.executeAction("postgresql_create_db", this,
                new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"%s\", conn_limit: -1}", dbName, dbAdminPass)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)),
                "База данных не создалась c именем " + dbName);
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    public void setConnLimit(String dbName, int count) {
        OrderServiceSteps.executeActionWidthFilter("postgresql_db_set_conn_limit", this, new JSONObject().put("conn_limit", count), this.getProjectId(), filterBd(dbName));
        Assertions.assertEquals(count, (Integer) OrderServiceSteps.getProductsField(this, String.format(DB_CONN_LIMIT, dbName)));
        if (isDev())
            Assertions.assertEquals(String.valueOf(count), StringUtils.findByRegex("\\s(.*)\\n\\(",
                    executeSsh("psql -c \"select datconnlimit from pg_database where datname='dbname';\"")));
    }

    public void removeConnLimit(String dbName) {
        OrderServiceSteps.executeActionWidthFilter("postgresql_db_remove_conn_limit", this, new JSONObject().put("conn_limit", -1), this.getProjectId(), filterBd(dbName));
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(IS_DB_CONN_LIMIT, dbName)));
    }

    private void addMountPoint(String action, String mount) {
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

    public String getCurrentMaxConnections(){
        return (String) OrderServiceSteps.getProductsField(this, MAX_CONNECTIONS);
    }

    public void updateMaxConnectionsBySsh(int connections){
        String cmd = String.format("sudo -iu postgres psql -c \"Alter system set max_connections to '%s';\"", connections);
        assertContains(executeSsh(cmd), "ALTER SYSTEM");
        executeSsh("sudo -i systemctl restart postgresql-14");
        getConfiguration();
        Assertions.assertEquals(connections, Integer.valueOf(getCurrentMaxConnections()));
    }

    public int maxConnections() {
        return (int) (118 * Math.log(flavor.getCpus() + 1) * Math.log(flavor.getMemory() + 1));
    }

    public void getConfiguration(){
        OrderServiceSteps.executeAction("postgresql_get_configuration", this, null, this.getProjectId());
    }

    public void updatePostgresql(){
        OrderServiceSteps.executeAction("postgresql_update_postgresql", this, new JSONObject().put("check_agree", true), this.getProjectId());
    }

    public void updateOs(){
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
            String cmd = String.format("sudo -iu postgres psql -d %s -c \"create extension %s with schema %s;\"", dbName, extension, dbName);
            assertContains(executeSsh(cmd), "CREATE EXTENSION");
        }
        OrderServiceSteps.executeActionWidthFilter("postgresql_db_get_extensions", this, null, this.getProjectId(), filterBd(dbName));
        if (isDev())
            Assertions.assertTrue(OrderServiceSteps.getProductsField(this, String.format(EXTENSIONS_LIST, dbName), List.class).contains(extension));
    }

    private String filterBd(String dbName){
        return String.format("db_name=='%s'", dbName);
    }

    //Удалить БД
    public void removeDb(String dbName) {
        OrderServiceSteps.executeActionWidthFilter("postgresql_remove_db", this, null, this.getProjectId(), filterBd(dbName));
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

    public void resize(Flavor newFlavor) {
        OrderServiceSteps.executeAction("resize_two_layer", this, new JSONObject("{\"flavor\": " + newFlavor.toString() + ",\"warning\":{}}").put("check_agree", true), this.getProjectId());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(newFlavor.data.cpus, cpusAfter);
        Assertions.assertEquals(newFlavor.data.memory, memoryAfter);
        flavor = newFlavor;
        save();
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

    public void checkUseSsh(String ip, String dbName, String adminPassword) {
        String cmd = "psql \"host=localhost dbname=" + dbName +
                " user=" + dbName + "_admin password=" + adminPassword +
                "\" -c \"\\pset pager off\" -c \"CREATE TABLE test1 (name varchar(30), surname varchar(30));\" -c \"\\z " + dbName + ".test1\"";
        assertContains(executeSsh(cmd), dbName + "_user=arwd/" + dbName + "_admin",
                dbName + "_reader=r/" + dbName + "_admin", dbName + "_admin=arwdDxt/" + dbName + "_admin");
    }
}




