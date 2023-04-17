package models.cloud.orderService.products;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.utils.ssh.SshClient;
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
import steps.portalBack.PortalBackSteps;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static core.utils.AssertUtils.assertContains;
import static models.cloud.orderService.products.ClickHouseCluster.*;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class ClickHouse extends IProduct {
    Flavor flavor;
    String osVersion;
    @Builder.Default
    public List<Db> database = new ArrayList<>();
    @Builder.Default
    public List<DbUser> users = new ArrayList<>();
    String clickhousePassword;
    String clickhouseUser;
    String clickhouseBb;
    String chCustomerPassword;
    String chVersion;

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

    private final static String DB_NAME_PATH = "data.find{it.data.config.containsKey('dbs')}.data.config.dbs.any{it.db_name=='%s'}";
    private final static String DB_USERNAME_PATH = "data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}";

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/clickhouse.json";
        productName = "ClickHouse";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (clickhouseUser == null)
            clickhouseUser = "clickhouse_user";
        if (clickhousePassword == null)
            clickhousePassword = "vrItfk0k8sf8ICbwsMs7nB3";
        if (chCustomerPassword == null)
            chCustomerPassword = "XcMYBatz2KNlctnmRYitgcSNQxQejZKV4I71lJGu8t";
        if (clickhouseBb == null)
            clickhouseBb = "dbname";
        if (chVersion == null)
            chVersion = getRandomProductVersionByPathEnum("ch_version.default.split()");
        if(segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if(dataCentre == null)
            setDataCentre(OrderServiceSteps.getDataCentre(this));
        if(platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if(domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        return this;
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void refreshVmConfig() {
        OrderServiceSteps.executeAction(REFRESH_VM_CONFIG, this, null, this.getProjectId());
    }

    public void removeDb(String dbName) {
        OrderServiceSteps.executeAction(CLICKHOUSE_DELETE_DB, this, new JSONObject("{\"db_name\": \"" + dbName + "\"}"), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.removeIf(db -> db.getNameDB().equals(dbName));
        save();
    }

    public void resetPasswordOwner() {
        String password = "uAhHmuyQnT2kCvTpOPgw9JIab0OwNvyj";
        OrderServiceSteps.executeAction("clickhouse_reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", clickhouseUser, password)), this.getProjectId());
        clickhousePassword = password;
        save();
    }

    public void resetPasswordCustomer() {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        OrderServiceSteps.executeAction("clickhouse_reset_ch_customer_password", this, new JSONObject(String.format("{\"user_name\":\"ch_customer\",\"user_password\":\"%s\"}", password)), this.getProjectId());
        chCustomerPassword = password;
        save();
    }

    public void removeDbmsUser(String username, String dbName) {
        OrderServiceSteps.executeAction(CLICKHOUSE_DELETE_DBMS_USER, this, new JSONObject(String.format("{\"user_name\":\"%s\"}", username)), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, username)),
                String.format("Пользователь: %s не удалился из базы данных: %s", username, dbName));
        log.info("users = " + users);
        save();
    }

    public void createDb(String dbName) {
        if (database.contains(new Db(dbName)))
            return;
        OrderServiceSteps.executeAction(CLICKHOUSE_CREATE_DB, this, new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq\"}", dbName)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)), "База данных не создалась c именем " + dbName);
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    public void createDbmsUser(String username, String dbRole, String dbName) {
        OrderServiceSteps.executeAction(CLICKHOUSE_CREATE_DBMS_USER,
                this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"txLhQ3UoykznQ2i2qD_LEMUQ_-U\"}",
                        dbName, dbRole, username)), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, username)),
                "Имя пользователя отличается от создаваемого");
        users.add(new DbUser(dbName, username));
        log.info("users = " + users);
        save();
    }

    public void createUserAccount(String user, String password) {
        OrderServiceSteps.executeAction("clickhouse_create_local_tuz", this, new JSONObject().put("user_name", user).put("user_password", password), getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERS, user)), String.format("Пользователь %s не найден", user));
    }

    public void deleteUserAccount(String user) {
        OrderServiceSteps.executeAction("clickhouse_remove_local_tuz", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERS, user)), String.format("Пользователь %s найден", user));
    }

    public void addUserAd(String user) {
        OrderServiceSteps.executeAction("clickhouse_create_new_tuz_ad", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERS_AD, user)), String.format("Пользователь %s не найден", user));
    }

    public void deleteUserAd(String user) {
        OrderServiceSteps.executeAction("clickhouse_remove_new_tuz_ad", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERS_AD, user)), String.format("Пользователь %s найден", user));
    }

    public void addGroupAd(String user) {
        JSONObject object = new JSONObject("{\n" +
                "  \"ad_integration\": true,\n" +
                "  \"clickhouse_user_ad_groups\": [\n" +
                "    {\n" +
                "      \"groups\": [\n" +
                "        \"" + user + "\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        OrderServiceSteps.executeAction("clickhouse_create_new_app_user_group_ad", this, object, getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USER_GROUP, user)), String.format("Группа %s не найдена", user));
    }

    public void deleteGroupAd(String user) {
        OrderServiceSteps.executeAction("clickhouse_remove_new_app_user_group_ad", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USER_GROUP, user)), String.format("Группа %s найдена", user));
    }

    public void addGroupAdmin(String user) {
        JSONObject object = new JSONObject("{\n" +
                "  \"ad_integration\": true,\n" +
                "  \"clickhouse_app_admin_ad_groups\": [\n" +
                "    {\n" +
                "      \"groups\": [\n" +
                "        \"" + user + "\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        OrderServiceSteps.executeAction("clickhouse_create_new_app_admin_group_ad", this, object, getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_ADMIN_GROUP, user)), String.format("Группа %s не найдена", user));
    }

    public void deleteGroupAdmin(String user) {
        OrderServiceSteps.executeAction("clickhouse_remove_new_app_admin_group_ad", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_ADMIN_GROUP, user)), String.format("Группа %s найдена", user));
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app/clickhouse", 10);
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
        String accessGroup = getAccessGroup();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.clickhouse_db", clickhouseBb)
                .set("$.order.attrs.ch_customer_password", chCustomerPassword)
                .set("$.order.attrs.ch_version", chVersion)
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.clickhouse_user_ad_groups[0].groups[0]", accessGroup)
                .set("$.order.attrs.clickhouse_app_admin_ad_groups[0].groups[0]", accessGroup)
                .set("$.order.attrs.system_adm_groups[0].groups[0]", accessGroup)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.clickhouse_users", clickhouseUser)
                .set("$.order.attrs.clickhouse_password", clickhousePassword)
                .set("$.order.attrs.on_support", !isDev())
                .set("$.order.label", getLabel())
                .build();

    }

    @SneakyThrows
    public void checkConnectDb() {
        try {
            checkConnectDb(clickhouseBb + "?ssl=1&sslmode=none", clickhouseUser, clickhousePassword,
                    ((String) OrderServiceSteps.getProductsField(this, CONNECTION_URL))
                            .replaceFirst("/play", "")
                            .replaceFirst("https:", "clickhouse:"));
        } catch (ConnectException e){
            if(!e.getMessage().contains("(UNKNOWN_DATABASE)"))
                throw e;
        }
    }

}
