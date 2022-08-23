package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import models.Entity;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
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
    String segment;
    String dataCentre;
    @ToString.Include
    String osVersion;
    @ToString.Include
    String wildFlyVersion;
    String domain;
    Flavor flavor;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/wildfly.json";
        if (productName == null) {
            productName = "WildFly RHEL";
        }
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (wildFlyVersion == null)
            wildFlyVersion = getRandomProductVersionByPathEnum("wildfly_version.enum");
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.wildfly_version", getWildFlyVersion())
                .set("$.order.attrs.access_group[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();
    }

    //Обновить сертификаты
    @SneakyThrows
    public void updateCerts() {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('certificate')}.data.config.certificate.end_date"));
        super.updateCerts("wildfly_update_certs");
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('certificate')}.data.config.certificate.end_date"));
        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate), "Предыдущая дата обновления сертификата больше либо равна новой дате обновления сертификата ");

    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
    }

    public void syncDev() {
        OrderServiceSteps.executeAction("wildfly_sync", this, null, this.getProjectId());
    }

    //Добавление пользователя WildFly
    public void addUser(String username, String role) {
        OrderServiceSteps.executeAction("wildfly_add_user", this,
                new JSONObject().put("new_wildfly_user", new JSONObject().put("user_name", username).put("user_role", role))
                , this.getProjectId());
    }

    //Удаление пользователя WildFly
    public void deleteUser(String username, String role) {
        OrderServiceSteps.executeAction("wildfly_del_user", this,
                new JSONObject().put("wildfly_deployer", username).put("user_role", role), this.getProjectId());
    }

    //Добавление группы WildFly
    public void addGroup(String name, String role) {
        OrderServiceSteps.executeAction("wildfly_add_group", this,
                new JSONObject().put("new_wildfly_user", new JSONObject().put("group_name", name).put("user_role", role)), this.getProjectId());
    }

    //Удаление группы WildFly
    public void deleteGroup(String name, String role) {
        OrderServiceSteps.executeAction("wildfly_del_group", this,
                new JSONObject().put("wildfly_deployer", name).put("user_role", role), this.getProjectId());
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
