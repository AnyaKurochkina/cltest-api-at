package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.portalBack.AccessGroup;
import models.cloud.subModels.Db;
import models.cloud.subModels.DbUser;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class ScyllaDb extends IProduct {
    private final static String DB_NAME_PATH = "data.find{it.data.config.containsKey('dbs')}.data.config.dbs.any{it.db_name=='%s'}";
    private final static String DB_USERNAME_PATH = "data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}";
    private final static String DB_USERNAME_PERMISSIONS_PATH = "data.find{it.data.config.containsKey('permissions')}.data.config.permissions.any{it.db_name=='%s' && it.user_name=='%s'}";
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String osVersion;
    String version;
    String domain;
    Flavor flavor;
    @Builder.Default
    public List<Db> database = new ArrayList<>();
    @Builder.Default
    public List<DbUser> users = new ArrayList<>();

    @Override
    public Entity init() {
        jsonTemplate = "/orders/scyllaDb.json";
        if(productName == null)
            productName = "ScyllaDB";
        initProduct();
        if(segment == null)
            segment = OrderServiceSteps.getNetSegment(this);
        if (domain == null)
            domain = OrderServiceSteps.getDomainBySegment(this, segment);
        if(flavor == null)
            flavor = getMinFlavor();
        if(osVersion == null)
            osVersion = getRandomOsVersion();
        if(dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {

        createProduct();
    }

    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(getProjectId()).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.scylladb_version", version)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", getProjectId())
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();
    }

    // (?!system|maintenancy|dba|admin)([a-z0-9]+){1,63}
    public void createDb(String dbName) {
        if(database.contains(new Db(dbName)))
            return;
        OrderServiceSteps.executeAction("scylladb_create_db", this, new JSONObject(String.format("{db_name: \"%s\"}", dbName)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)),
                "База данных не создалась c именем " + dbName);
        database.add(new Db(dbName));
        log.info("createDb: " + database);
        save();
    }

    // [a-z0-9]+ 3-16
    public void createDbmsUser(String username, String password, String role) {
        OrderServiceSteps.executeAction("scylladb_create_dbms_user", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\",\"dbms_role\":\"%s\"}", username, password, role)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERNAME_PATH, username)), "Имя пользователя отличается от создаваемого");
        log.info("createDbmsUser = " + username);
        save();
    }

    // admin, user
    public void addPermissionsUser(String dbName, String username){
        OrderServiceSteps.executeAction("scylladb_dbms_permissions", this, new JSONObject(String.format("{\"db_name\":\"%s\",\"user_name\":\"%s\"}", dbName, username)));
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERNAME_PERMISSIONS_PATH, dbName, username)), "Права пользователю не выданы");
        users.add(new DbUser(dbName, username));
        log.info("addPermissionsUser = " + username);
        save();
    }

    public void removePermissionsUser(String dbName, String username){
        OrderServiceSteps.executeAction("scylladb_remove_dbms_permissions", this, new JSONObject(String.format("{\"db_name\":\"%s\",\"user_name\":\"%s\"}", dbName, username)));
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERNAME_PERMISSIONS_PATH, dbName, username)), "Права у пользователя остались");
        users.remove(new DbUser(dbName, username));
        log.info("removePermissionsUser = " + username);
        save();
    }

    //16-63
    public void resetPassword(String username, String password) {
        OrderServiceSteps.executeAction("scylladb_reset_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)), this.getProjectId());
    }

    //Удалить пользователя
    public void removeDbmsUser(String username) {
        OrderServiceSteps.executeAction("scylladb_remove_dbms_user", this, new JSONObject(String.format("{\"user_name\":\"%s\"}", username)), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERNAME_PATH, username)),
                String.format("Пользователь: %s не удалился", username));
        save();
    }

    //Удалить БД
    public void removeDb(String dbName) {
        OrderServiceSteps.executeAction("scylladb_remove_db", this, new JSONObject("{\"db_name\": \"" + dbName + "\"}"), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.removeIf(db -> db.getNameDB().equals(dbName));
        save();
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app/scylla/data", 10);
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
        delete("delete_two_layer");
    }

    public void checkConnectDb(String db, String user, String password) {
        super.checkConnectDb(db, user, password, ((String) OrderServiceSteps.getProductsField(this, CONNECTION_URL)));
    }
}
