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
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.net.ConnectException;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class ClickHouseCluster extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    String osVersion;
    String domain;
    String chVersion;
    String clickhouseBb;

    String chCustomerAdmin;
    String chCustomerPassword;
    String chCustomerAdminPassword;

    public static final String DB_USERS_AD = "data.find{it.type=='cluster'}.data.config.db_users_ad.any{it.user_name=='%s'}";
    public static final String DB_USERS = "data.find{it.type=='cluster'}.data.config.db_users.any{it.user_name=='%s'}";
    public static final String DB_USER_GROUP = "data.find{it.type=='cluster'}.data.config.db_user_group.any{it.dbms_role=='user' && it.user_name.contains('%s')}";
    public static final String DB_ADMIN_GROUP = "data.find{it.type=='cluster'}.data.config.db_app_admin_group.any{it.dbms_role=='admin' && it.user_name.contains('%s')}";

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/clickhouse_cluster.json";
        productName = "ClickHouse Cluster";
        initProduct();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        if (chCustomerAdmin == null)
            chCustomerAdmin = "portal_admin";
        if (chCustomerAdminPassword == null)
            chCustomerAdminPassword = "zWYVWBnqpYZ2X8Fj7rLaQM";
        if (chCustomerPassword == null)
            chCustomerPassword = "l8yPSaKJPgZ5liNtUyGFi1q8j8i9ZDc7FsCwlFvYvYB";
        if (clickhouseBb == null)
            clickhouseBb = "dbname";
        if (chVersion == null)
            chVersion = getRandomProductVersionByPathEnum("ch_version.default.split()");
        return this;
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app/zookeeper", 10);
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

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        Flavor flavorCh = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:cluster:clickhouse:dev:dev").get(0);
        Flavor flavorZk = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:cluster:zookeeper:dev:dev").get(0);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.ch_customer_password", chCustomerPassword)
                .set("$.order.attrs.ch_version", chVersion)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.ch_db_name", clickhouseBb)
                .set("$.order.attrs.flavor_ch", new JSONObject(flavorCh.toString()))
                .set("$.order.attrs.flavor_zk", new JSONObject(flavorZk.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.clickhouse_user_ad_groups[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.system_adm_groups[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.clickhouse_app_admin_ad_groups[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.ch_customer_admin", chCustomerAdmin)
                .set("$.order.attrs.ch_customer_admin_password", chCustomerAdminPassword)
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();

    }

    public void resetPasswordCustomer() {
        String password = "RXlpeN0MztCYS3XDP6i75";
        OrderServiceSteps.executeAction("clickhouse_cluster_reset_db_user_password", this, new JSONObject().put("user_name", "ch_customer").put("user_password", password), getProjectId());
        chCustomerPassword = password;
        save();
    }

    public void checkConnectDb(int node) {
        Assertions.assertThrows(ConnectException.class, () ->
        checkConnectDb(clickhouseBb + "?ssl=1&sslmode=none", chCustomerAdmin, chCustomerAdminPassword,
                ((String) OrderServiceSteps.getProductsField(this, CONNECTION_URL + "[" + node + "]"))
                        .replaceFirst("/play", "")
                        .replaceFirst("https:", "clickhouse:")), "UNKNOWN_DATABASE");
    }

    public void createUserAccount(String user, String password) {
        OrderServiceSteps.executeAction("clickhouse_cluster_create_local_tuz", this, new JSONObject().put("user_name", user).put("user_password", password), getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERS, user)), String.format("Пользователь %s не найден", user));
    }

    public void addUserAd(String user) {
        OrderServiceSteps.executeAction("clickhouse_cluster_create_new_tuz_ad", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERS_AD, user)), String.format("Пользователь %s не найден", user));
    }

    public void deleteUserAccount(String user) {
        OrderServiceSteps.executeAction("clickhouse_cluster_remove_local_tuz", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERS, user)), String.format("Пользователь %s найден", user));
    }

    public void deleteUserAd(String user) {
        OrderServiceSteps.executeAction("clickhouse_cluster_remove_new_tuz_ad", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USERS_AD, user)), String.format("Пользователь %s найден", user));
    }

    public void deleteGroupAdmin(String user) {
        OrderServiceSteps.executeAction("clickhouse_cluster_remove_new_app_admin_group_ad", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_ADMIN_GROUP, user)), String.format("Группа %s найдена", user));
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
        OrderServiceSteps.executeAction("clickhouse_cluster_create_new_app_admin_group_ad", this, object, getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_ADMIN_GROUP, user)), String.format("Группа %s не найдена", user));
    }

    public void deleteGroupAd(String user) {
        OrderServiceSteps.executeAction("clickhouse_cluster_remove_new_app_user_group_ad", this, new JSONObject().put("user_name", user), getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USER_GROUP, user)), String.format("Группа %s найдена", user));
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
        OrderServiceSteps.executeAction("clickhouse_cluster_create_new_app_user_group_ad", this, object, getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(DB_USER_GROUP, user)), String.format("Группа %s не найдена", user));
    }
}
