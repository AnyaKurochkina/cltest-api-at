package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import models.cloud.subModels.Vhost;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class RabbitMQClusterAstra extends IProduct {
    private final static String RABBITMQ_USER = "data.find{it.type=='cluster'}.data.config.users.any{it.name=='%s'}";
    private final static String RABBIT_CLUSTER_VHOST = "data.find{it.data.config.containsKey('vhosts')}.data.config.vhosts.any{it.name=='%s'}";
    private final static String RABBIT_CLUSTER_VHOST_ACCESS = "data.find{it.data.config.containsKey('vhost_access')}.data.config.vhost_access.any{it.vhost_name=='%s'}";
    String role;
    Flavor flavor;
    String osVersion;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/rabbitmq_cluster.json";
        productName = "RabbitMQ Cluster Astra";
        role = "manager";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
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

    @Override
    public JSONObject toJson() {
        Organization org = Organization.builder().type("default").build().createObject();
        Project project = Project.builder().id(projectId).build().createObject();
        String accessGroup = accessGroup();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.cluster_name", "at-" + new Random().nextInt())
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.web_console_grants[0].role", role)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.layout", getIdGeoDistribution("rabbitmq-2", getSegment(), "rabbitmq", org.getName()))
                .set("$.order.label", getLabel())
                .build();
    }

    //Создать пользователя RabbitMQ
    public void rabbitmqCreateUser(String user) {
        OrderServiceSteps.executeAction("rabbitmq_create_user_release", this, new JSONObject(String.format("{rabbitmq_users: [{name: \"%s\"}]}", user)), this.getProjectId());
        Assertions.assertTrue(((Boolean) OrderServiceSteps.getProductsField(this, String.format(RABBITMQ_USER, user))), "У продукта отсутствует пользователь " + user);
    }

    public void rabbitmqDeleteUser(String user) {
        OrderServiceSteps.executeAction("rabbitmq_delete_users_release", this, new JSONObject().put("name", user), this.getProjectId());
        Assertions.assertFalse(((Boolean) OrderServiceSteps.getProductsField(this, String.format(RABBITMQ_USER, user))), "У продукта присутствует пользователь " + user);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    @SneakyThrows
    public void updateCerts() {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
        super.updateCerts("rabbitmq_update_certs_release");
//        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "attrs.preview_items.data.find{it.config.containsKey('certificate_expiration')}.config.certificate_expiration"));
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
//        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата больше либо равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
        Assertions.assertNotEquals(0, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
    }

    public void addVhost(List<String> collect) {
        List<Vhost> vhosts = new ArrayList<>();
        for (String name : collect)
            vhosts.add(new Vhost(name));
        OrderServiceSteps.executeAction("rabbitmq_create_vhosts_release", this, new JSONObject("{\"rabbitmq_vhosts\": " + JsonHelper.toJson(vhosts) + "}"), projectId);
        for (String name : collect)
            Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this,
                    String.format(RABBIT_CLUSTER_VHOST, name)), "Отсутствует vhost " + name);
    }

    public void deleteVhost(List<String> collect) {
        OrderServiceSteps.executeAction("rabbitmq_delete_vhosts_release", this, new JSONObject("{\"vhosts\": " + JsonHelper.toJson(collect) + "}"), projectId);
        for (String name : collect)
            Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this,
                    String.format(RABBIT_CLUSTER_VHOST, name)), "Присутствует vhost " + name);
    }

    public void editVhostAccess(String user, List<String> permissions, String vhost) {
        OrderServiceSteps.executeAction("rabbitmq_edit_vhost_access_release", this,
                new JSONObject("{\"user_name\": \"" + user + "\", \"vhost_permissions\": [{\"permissions\": " + JsonHelper.toJson(permissions) + ", \"vhost_name\": \"" + vhost + "\"}]}"), projectId);
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this,
                String.format(RABBIT_CLUSTER_VHOST_ACCESS, vhost)), "Отсутствует vhost access " + vhost);
    }

    public void deleteVhostAccess(String user, String vhost) {
        OrderServiceSteps.executeAction("rabbitmq_delete_vhost_access_release", this,
                new JSONObject("{\"user_name\": \"" + user + "\", \"vhost_name\": \"" + vhost + "\"}"), projectId);
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this,
                String.format(RABBIT_CLUSTER_VHOST_ACCESS, vhost)), "Присутствует vhost access " + vhost);
    }

    @Step("Произвести ре-балансировку очередей")
    public void queueRebalancing() {
        OrderServiceSteps.executeAction("rabbitmq_queue_rebalancing_release", this, new JSONObject().put("accept", true), projectId);
    }

    @Step("Синхронизировать данные кластера")
    public void dataSynchronization() {
        OrderServiceSteps.executeAction("rabbitmq_data_synchronization_release", this, new JSONObject().put("accept", true), projectId);
    }

    @Step("Редактировать группу доступа")
    public void editAccessGroupsOnTheWeb(String group, String role) {
        JSONObject data = new JSONObject().put("accept", true).append("members", group).put("role", role);
        OrderServiceSteps.executeAction("rabbitmq_edit_access_groups_on_the_web_release", this, data, projectId);
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
    }

    public void resize() {
        resize("resize_vm");
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

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }
}
