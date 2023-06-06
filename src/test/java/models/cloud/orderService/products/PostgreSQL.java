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
public class PostgreSQL extends AbstractPostgreSQL {
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
        delete("delete_postgresql");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/pg_data", 10);
    }

    public String getIp() {
        return ((String) OrderServiceSteps.getProductsField(this, "data.find{it.type=='vm'}.data.config.default_v4_address"));
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

    public void removeDbmsUser(String username, String dbName) {
        removeDbmsUser("remove_dbms_user", username, dbName);
    }

    public void resetDbOwnerPassword(String username) {
        resetDbOwnerPassword("reset_db_owner_password", username);
    }

    public void resetPassword(String username) {
        resetPassword("reset_db_user_password", username);
    }

    public void createDbmsUser(String username, String dbRole, String dbName) {
        createDbmsUser("create_dbms_user", username, dbRole, dbName);
    }

    public void checkUseSsh(String ip, String dbName, String adminPassword) {
        String cmd = "psql \"host=localhost dbname=" + dbName +
                " user=" + dbName + "_admin password=" + adminPassword +
                "\" -c \"\\pset pager off\" -c \"CREATE TABLE test1 (name varchar(30), surname varchar(30));\" -c \"\\z " + dbName + ".test1\"";
        assertContains(executeSsh(cmd), dbName + "_user=arwd/" + dbName + "_admin",
                dbName + "_reader=r/" + dbName + "_admin", dbName + "_admin=arwdDxt/" + dbName + "_admin");
    }
}




