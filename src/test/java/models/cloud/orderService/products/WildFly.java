package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.util.Date;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class WildFly extends IProduct {
    @ToString.Include
    String osVersion;
    @ToString.Include
    String wildFlyVersion;
    private static String otherJavaVersion = "11";
    String javaVersion;
    Flavor flavor;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/wildfly.json";
        if (productName == null) {
            productName = "WildFly Astra";
        }
        initProduct();
        if (wildFlyVersion == null)
//            wildFlyVersion = getRandomProductVersionByPathEnum("wildfly_version.enum");
            wildFlyVersion = "23.0.2.Final";
        if (javaVersion == null)
            javaVersion = "8";
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (availabilityZone == null)
            setAvailabilityZone(OrderServiceSteps.getAvailabilityZone(this));
        if(platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        if (flavor == null)
            flavor = getMinFlavor();
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        String accessGroup = accessGroup();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.wildfly_version", getWildFlyVersion())
                .set("$.order.attrs.java_version", getJavaVersion())
                .set("$.order.attrs.access_group[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", /*isTest()*/getSupport())
                .set("$.order.label", getLabel())
                .build();
    }

    //Обновить сертификаты
    @SneakyThrows
    public void updateCerts(JSONObject data) {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('certificate')}.data.config.certificate.end_date"));
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_update_certs").product(this).data(data).build());
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('certificate')}.data.config.certificate.end_date"));
        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate), "Предыдущая дата обновления сертификата больше либо равна новой дате обновления сертификата ");

    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("check_vm").product(this).build());
    }

    public void syncDev() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_sync").product(this).build());
    }

    public void updateOs() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_update_os").product(this).data(new JSONObject().put("accept", true)).build());
    }

    public void stopService() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_stop_wf").product(this).data(new JSONObject().put("accept", true)).build());
    }

    public void startService() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_start_wf").product(this).data(new JSONObject().put("accept", true)).build());
    }

    public void restartService() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_restart_wf").product(this).data(new JSONObject().put("accept", true)).build());
    }

    public void wildflyChangeJava() {
        JSONObject data = new JSONObject().put("accept", true).put("java_version", otherJavaVersion).put("wildfly_version", wildFlyVersion);
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_change_java").product(this).data(data).build());
    }

    //Добавление пользователя WildFly
    public void addUser(String username, String role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_add_user").product(this)
                .data(new JSONObject().put("new_wildfly_user", new JSONObject().put("user_name", username).put("user_role", role))).build());
    }

    //Удаление пользователя WildFly
    public void deleteUser(String username, String role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_del_user").product(this)
                .data(new JSONObject().put("wildfly_deployer", username).put("user_role", role)).build());
    }

    //Добавление группы WildFly
    public void addGroup(String name, String role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_add_group").product(this)
                .data(new JSONObject().put("new_wildfly_user", new JSONObject().append("group_name", name).put("user_role", role))).build());
    }

    //Удаление группы WildFly
    public void deleteGroup(String name, String role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("wildfly_release_del_group").product(this)
                .data(new JSONObject().put("wildfly_deployer", name).put("user_role", role)).build());
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app/app", 10);
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

    public void resize(Flavor flavor) {
        resize("resize_vm", flavor);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }
}
