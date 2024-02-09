package models.cloud.orderService.products;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
import steps.orderService.ActionParameters;
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
    private final static String RABBITMQ_USER = "data.find{it.type=='cluster'}.data.config.users.find{it.name.contains('%s')}.name";
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
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (availabilityZone == null)
            setAvailabilityZone(OrderServiceSteps.getAvailabilityZone(this));
        if (platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        if (flavor == null)
            flavor = getMinFlavor();
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
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.web_manager_groups[0]", accessGroup)
                .set("$.order.attrs.web_administrator_groups[0]", accessGroup, !isDev())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.layout", getIdGeoDistribution("rabbitmq-2", getSegment(), "rabbitmq", org.getName()))
                .set("$.order.label", getLabel())
                .build();
    }

    //Создать пользователя RabbitMQ
    public void rabbitmqCreateUser(String apd, String risCode, String user) {
        JSONObject jsonObject = new JSONObject().append("rabbitmq_users", new JSONObject().put("apd", apd).put("ris_code", risCode).put("name", user))
                .put("env_prefix", getEnv().toLowerCase());
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_create_user_release").product(this)
                .data(jsonObject).build());
        Assertions.assertNotNull(fullUserName(user), "У продукта отсутствует пользователь " + user);
    }

    private String fullUserName(String user) {
        return OrderServiceSteps.getObjectClass(this, String.format(RABBITMQ_USER, user), String.class);
    }

    public void rabbitmqDeleteUser(String user) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_delete_users_release").product(this)
                .data(new JSONObject().put("name", fullUserName(user))).build());
        Assertions.assertNull(fullUserName(user), "У продукта присутствует пользователь " + user);
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_update_certs_release").product(this)
                .data(new JSONObject().put("dumb", "empty").put("accept", true)).build());
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
        Assertions.assertNotEquals(0, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Permission {
        private String userName;
        @Singular("vhostRead")
        private List<String> vhostRead;
        @Singular("vhostWrite")
        private List<String> vhostWrite;
        @Singular("vhostConfigure")
        private List<String> vhostConfigure;
    }

    public void editVhostsAccess(Permission... permissions) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_edit_vhosts_access_release").product(this)
                .data(new JSONObject().put("input_permissions", permissions)).build());
        for (Permission permission : permissions)
            for (String vhost : permission.getVhostConfigure())
                Assertions.assertTrue(OrderServiceSteps.getObjectClass(this, String.format(RABBIT_CLUSTER_VHOST_ACCESS, vhost), Boolean.class),
                        "Отсутствует vhost access " + vhost);
    }

    public void addVhost(List<String> collect) {
        List<Vhost> vhosts = new ArrayList<>();
        for (String name : collect)
            vhosts.add(new Vhost(name));
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_edit_vhosts_access_release").product(this)
                .data(new JSONObject().put("rabbitmq_vhosts", serializeList(vhosts))).build());
        for (String name : collect)
            Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this,
                    String.format(RABBIT_CLUSTER_VHOST, name)), "Отсутствует vhost " + name);
    }

    public void deleteVhost(List<String> collect) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_delete_vhosts_release").product(this)
                .data(new JSONObject().put("rabbitmq_vhosts_to_delete", serializeList(collect))).build());
        for (String name : collect)
            Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this,
                    String.format(RABBIT_CLUSTER_VHOST, name)), "Присутствует vhost " + name);
    }

    public void editVhostAccess(String user, List<String> permissions, String vhost) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_edit_vhost_access_release").product(this)
                .data(new JSONObject("{\"user_name\": \"" + fullUserName(user) + "\", \"vhost_access\": [{\"permissions\": " + JsonHelper.toJson(permissions) + ", \"vhost_name\": \"" + vhost + "\"}]}")).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this,
                String.format(RABBIT_CLUSTER_VHOST_ACCESS, vhost)), "Отсутствует vhost access " + vhost);
    }

    public void deleteVhostAccess(String user, String vhost) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_delete_vhost_access_release").product(this)
                .data(new JSONObject().put("user_name", fullUserName(user)).put("vhost_name", vhost)).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this,
                String.format(RABBIT_CLUSTER_VHOST_ACCESS, vhost)), "Присутствует vhost access " + vhost);
    }

    @Step("Произвести ре-балансировку очередей")
    public void queueRebalancing() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_queue_rebalancing_release").product(this)
                .data(new JSONObject().put("accept", true)).build());
    }

    @Step("Синхронизировать данные кластера")
    public void dataSynchronization() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_data_synchronization_release").product(this)
                .data(new JSONObject().put("accept", true)).build());
    }

    @Step("Редактировать группу доступа")
    public void editAccessGroupsOnTheWeb(String group, String role) {
        JSONObject data = new JSONObject().put("accept", true).append("members", group).put("role", role);
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_edit_access_groups_on_the_web_release")
                .product(this).data(data).build());
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("check_vm").product(this).build());
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

    public void updateOs() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_update_os_cluster_release").product(this)
                .data(new JSONObject().put("check_agree", true)).build());
    }

    public void verticalScaling() {
        final Flavor maxFlavor = getMaxFlavor();
        JSONObject data = JsonHelper.getJsonTemplate("/orders/rabbitmq_vertical_scaling.json")
                .set("$.flavor", new JSONObject(maxFlavor.toString())).build();
        OrderServiceSteps.runAction(ActionParameters.builder().name("rabbitmq_vertical_scaling_release").product(this).data(data).build());
        flavor = maxFlavor;
        save();
    }
}
