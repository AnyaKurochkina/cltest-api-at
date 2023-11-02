package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import steps.orderService.OrderServiceSteps;

import java.util.List;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Etcd extends IProduct {
    @ToString.Include
    String osVersion;
    Flavor flavor;
    String etcdVersion;
    String etcdUser;
    String etcdPassword;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/etcd.json";
        productName = "Etcd";
        initProduct();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (availabilityZone == null)
            setAvailabilityZone(OrderServiceSteps.getAvailabilityZone(this));
        if (platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        if (etcdVersion == null)
            etcdVersion = getRandomProductVersionByPathEnum("etcd_version.default");
        if (etcdUser == null)
            etcdUser = "user";
        if (etcdPassword == null)
            etcdPassword = "vdcFpCO7UCMMdAVIErDZcxouaOVdR1rKInlpyLToP96GITyrvbo0Zg";
        if (flavor == null)
            flavor = getMinFlavor();
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup())
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.ad_integration", true)
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .set("$.order.attrs.on_support", getSupport())
//                .set("$.order.attrs.tarantool_version", getTarantoolVersion())
//                .set("$.order.attrs.layout", getIdGeoDistribution("rps-2000"))
                .build();
    }

    public void expandMountPoint(String mountPoint) {
        expandMountPoint("expand_mount_point_new", "/app/etcd/data", 10);
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
    }

    @Step("Обновить сертификаты")
    public void updateCerts() {
        OrderServiceSteps.executeAction("tdg_update_certs", this, null, this.getProjectId());
    }

    @Step("Перезапустить сервисы")
    public void restartInstances(List<String> services) {
        JSONObject data = new JSONObject().put("type", "Instance").put("instances", services);
        OrderServiceSteps.executeAction("tdg_restart_instances", this, data, this.getProjectId());
    }

    @Step("Удалить")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void resetPassword(String etcdPassword) {
        JSONObject data = new JSONObject().put("etcd_user", etcdUser).put("etcd_password", etcdPassword);
        OrderServiceSteps.executeAction("etcd_reset_user_pass", this, data, this.getProjectId());
        this.etcdPassword = etcdPassword;
        save();
    }

    public void createCerts() {
        JSONObject data = new JSONObject().put("etcd_user", etcdUser);
        OrderServiceSteps.executeAction("etcd_create_certs", this, data, this.getProjectId());
    }
}
