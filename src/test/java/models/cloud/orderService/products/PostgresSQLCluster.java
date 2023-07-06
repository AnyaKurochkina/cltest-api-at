package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.JsonTemplate;
import core.helper.StringUtils;
import core.utils.ssh.SshClient;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.util.Objects;

import static core.utils.AssertUtils.assertContains;


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

    transient String leaderIp;

    @Override
    public void updateMaxConnections() {
        String loadProfile = (String) OrderServiceSteps.getProductsField(this, "data.find{it.type=='cluster'}.data.config.load_profile");
        OrderServiceSteps.executeAction("postgresql_cluster_update_max_connections", this, new JSONObject().put("load_profile", loadProfile), this.getProjectId());
    }

    @Override
    public String executeSsh(String cmd) {
        if (Objects.isNull(leaderIp)) {
            String ip = (String) OrderServiceSteps.getProductsField(this, "product_data.find{it.hostname.contains('-pgc')}.ip");
            leaderIp = StringUtils.findByRegex("(([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3}))",
                    executeSsh(new SshClient(ip, envType()), "sudo -i patronictl -c /etc/patroni/patroni.yml list | grep Leader"));
        }
        return executeSsh(new SshClient(leaderIp, envType()), cmd);
    }

    @Override
    public void cmdRestartPostgres() {
        executeSsh("sudo -i patronictl -c /etc/patroni/patroni.yml restart $(sudo -i cat /etc/patroni/patroni.yml | grep scope | awk '{print $2}') -r master --force");
    }

    @Override
    protected void cmdSetMaxConnections(int connections) {
        String cmd = String.format("sudo patronictl -c /etc/patroni/patroni.yml edit-config -p max_connections=\"%s\" --force", connections);
        assertContains(executeSsh(cmd), "Configuration changed");
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

    private boolean isNotDebezium(String type) {
        Boolean isDebezium = OrderServiceSteps.getProductsField(this, String.format("data.find{it.type=='%s'}.data.config.debezium_ready", type), Boolean.class, false);
        return Objects.isNull(isDebezium) || !isDebezium;
    }

    @Step("Настроить кластер для интеграции с Debezium")
    public void configureDebezium() {
        if (isNotDebezium("cluster")) {
            JSONObject data = new JSONObject().put("check_agree", true).put("user_password", "hcvZ5k5oVRhV3WwXzVlrZsHU-Dcb9hWXz");
            OrderServiceSteps.executeAction("postgresql_cluster_configure_debezium", this, data, this.getProjectId());
        }
    }

    @Step("Настроить БД для интеграции с Debezium")
    public void configureDebeziumDb() {
        configureDebezium();
        if (isNotDebezium("db")) {
            JSONObject data = new JSONObject().put("check_agree", true);
            OrderServiceSteps.executeAction("postgresql_db_configure_for_debezium", this, data, this.getProjectId());
        }
    }

    @Step("Создать логический слот")
    public void createLogicalSlot(String slotName) {
        configureDebeziumDb();
        JSONObject data = new JSONObject().put("slot_name", slotName);
        OrderServiceSteps.executeAction("postgresql_db_create_logical_slot", this, data, this.getProjectId());
        Assertions.assertEquals(state(slotName), "on");
    }

    @Step("Удалить логический слот")
    public void removeLogicalSlot(String slotName) {
        JSONObject data = new JSONObject().put("name", slotName);
        OrderServiceSteps.executeAction("postgresql_remove_logical_slot", this, data, this.getProjectId());
        Assertions.assertEquals(state(slotName), "deleted");
    }

    @Step("Создать публикацию")
    public void createPublication(String publication) {
        configureDebeziumDb();
        JSONObject data = new JSONObject().put("publication_name", publication);
        OrderServiceSteps.executeAction("postgresql_db_create_publication", this, data, this.getProjectId());
        Assertions.assertEquals(state(publication), "on");
    }

    @Step("Удалить публикацию")
    public void removePublication(String publication) {
        JSONObject data = new JSONObject().put("name", publication);
        OrderServiceSteps.executeAction("postgresql_remove_publication", this, data, this.getProjectId());
        Assertions.assertEquals(state(publication), "deleted");
    }

    @Override
    public void getConfiguration() {
        OrderServiceSteps.executeAction("postgresql_cluster_get_configuration", this, null, this.getProjectId());
    }

    @Override
    public void updatePostgresql() {
        OrderServiceSteps.executeAction("postgresql_cluster_update_postgresql", this, new JSONObject().put("check_agree", true), this.getProjectId());
    }

    @Override
    public void updateDti(String defaultTransactionIsolation) {
        OrderServiceSteps.executeAction("postgresql_cluster_update_dti", this,
                new JSONObject(String.format("{\"default_transaction_isolation\":\"%s\"}", defaultTransactionIsolation)), this.getProjectId());
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
