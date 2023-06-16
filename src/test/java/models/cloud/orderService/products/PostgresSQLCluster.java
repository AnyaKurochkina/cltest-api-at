package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.JsonTemplate;
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

import java.util.ArrayList;
import java.util.List;

import static core.utils.AssertUtils.assertContains;
import static models.cloud.orderService.products.PostgreSQL.DB_CONN_LIMIT;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class PostgresSQLCluster extends AbstractPostgreSQL {
    String osVersion;
    @ToString.Include
    String postgresqlVersion;
    private String adminPassword;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/postgressql_cluster.json";
        if (productName == null)
            productName = "PostgreSQL Cluster Astra Linux";
        if (adminPassword == null)
            adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";
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
        JsonTemplate template = JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain());
        if (envType().contains("prod")) {
            template.put("$.order.attrs", "geo_distribution", true)
                    .put("$.order.attrs", "layout", getIdGeoDistribution("postgresql_etcd-1:postgresql-2", "postgresql"));
        }
        return template.set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.postgresql_version", postgresqlVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", getAccessGroup())
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.on_backup", envType().contains("prod"))
                .set("$.order.attrs.replication", envType().contains("prod"))
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

    @SneakyThrows
    public void checkConnection(String dbName) {
        checkConnectDb(dbName, dbName + "_admin", adminPassword, ((String) OrderServiceSteps.getProductsField(this, CONNECTION_URL)).split(",")[0]);
    }

    public void removeDbmsUser(String username, String dbName) {
        removeDbmsUser("postgresql_cluster_remove_dbms_user", username, dbName);
    }

    @Override
    public void getConfiguration() {
        OrderServiceSteps.executeAction("postgresql_cluster_get_configuration", this, null, this.getProjectId());
    }

    public void resetDbOwnerPassword(String username) {
        resetDbOwnerPassword("postgresql_cluster_reset_db_owner_password", username);
    }

    public void resetPassword(String username) {
        resetPassword("postgresql_cluster_reset_db_user_password", username);
    }

    public void createDbmsUser(String username, String dbRole, String dbName) {
        createDbmsUser("postgresql_cluster_create_dbms_user", username, dbRole, dbName);
    }

    public void resize(Flavor flavor) {
        resize("resize_postgresql_cluster", flavor);
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

    public void checkUseSsh(String ip, String dbName) {
        String cmd = "psql \"host=localhost dbname=" + dbName +
                " user=" + dbName + "_admin password=" + adminPassword +
                "\" -c \"\\pset pager off\" -c \"CREATE TABLE test1 (name varchar(30), surname varchar(30));\" -c \"\\z " + dbName + ".test1\"";
        assertContains(executeSsh(new SshClient(ip, envType()), cmd), dbName + "_user=arwd/" + dbName + "_admin",
                dbName + "_reader=r/" + dbName + "_admin", dbName + "_admin=arwdDxt/" + dbName + "_admin");
    }

}
