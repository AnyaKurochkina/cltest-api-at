package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
import models.subModels.Flavor;
import models.subModels.Vhost;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class RabbitMQCluster extends IProduct {
    private final static String RABBITMQ_USER = "data.find{it.type=='cluster'}.data.config.users.any{it.name=='%s'}";
    private final static String RABBIT_CLUSTER_VHOST = "data.find{it.data.config.containsKey('vhosts')}.data.config.vhosts.any{it.name=='%s'}";
    private final static String RABBIT_CLUSTER_VHOST_ACCESS = "data.find{it.data.config.containsKey('vhost_access')}.data.config.vhost_access.any{it.vhost_name=='%s'}";
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String domain;
    String role;
    Flavor flavor;
    String osVersion;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/rabbitmq_cluster.json";
        productName = "RabbitMQ Cluster";
        role = "administrator";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        if (isTest())
            role = "manager";
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.web_console_grants[0].role", role)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();
    }

    //Создать пользователя RabbitMQ
    public void rabbitmqCreateUser(String user) {
        OrderServiceSteps.executeAction("rabbitmq_create_user", this, new JSONObject(String.format("{rabbitmq_users: [{user: \"%s\", password: \"%s\", rabbitmq_user_password: true}]}", user, user)), this.getProjectId());
        Assertions.assertTrue(((Boolean) OrderServiceSteps.getProductsField(this, String.format(RABBITMQ_USER, user))), "У продукта отсутствует пользователь " + user);
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
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('certificate_expiration')}.data.config.certificate_expiration"));
        super.updateCerts("rabbitmq_update_certs");
//        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "attrs.preview_items.data.find{it.config.containsKey('certificate_expiration')}.config.certificate_expiration"));
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('certificate_expiration')}.data.config.certificate_expiration"));
//        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата больше либо равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
        Assertions.assertNotEquals(0, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
    }

    public void addVhost(List<String> collect) {
        List<Vhost> vhosts = new ArrayList<>();
        for (String name : collect)
            vhosts.add(new Vhost(name));
        OrderServiceSteps.executeAction("rabbitmq_create_vhosts", this, new JSONObject("{\"rabbitmq_vhosts\": " + JsonHelper.toJson(vhosts) + "}"), projectId);
        for (String name : collect)
            Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this,
                    String.format(RABBIT_CLUSTER_VHOST, name)), "Отсутствует vhost " + name);
    }

    public void deleteVhost(List<String> collect) {
        OrderServiceSteps.executeAction("rabbitmq_delete_vhosts", this, new JSONObject("{\"vhosts\": " + JsonHelper.toJson(collect) + "}"), projectId);
        for (String name : collect)
            Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this,
                    String.format(RABBIT_CLUSTER_VHOST, name)), "Присутствует vhost " + name);
    }

    public void addVhostAccess(String user, List<String> permissions, String vhost) {
        OrderServiceSteps.executeAction("rabbitmq_add_vhost_access", this,
                new JSONObject("{\"user_name\": \"" + user + "\", \"vhost_permissions\": [{\"permissions\": " + JsonHelper.toJson(permissions) + ", \"vhost_name\": \"" + vhost + "\"}]}"), projectId);
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this,
                String.format(RABBIT_CLUSTER_VHOST_ACCESS, vhost)), "Отсутствует vhost access " + vhost);
    }

    public void deleteVhostAccess(String user, String vhost) {
        OrderServiceSteps.executeAction("rabbitmq_delete_vhost_access", this,
                new JSONObject("{\"user_name\": \"" + user + "\", \"vhost_name\": \"" + vhost + "\"}"), projectId);
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this,
                String.format(RABBIT_CLUSTER_VHOST_ACCESS, vhost)), "Присутствует vhost access " + vhost);
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
        expandMountPoint("expand_mount_point", "/app", 10);
    }
}
