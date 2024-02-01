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
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Astra extends IProduct {
    public static String SNAPSHOT_PATH = "data.any{it.type=='snapshot' && it.data.state=='on'}";
    @ToString.Include
    String osVersion;
    Flavor flavor;

    @Override
    public Entity init() {
        if (jsonTemplate == null)
            jsonTemplate = "/orders/astra_general_application.json";
        if (productName == null)
            productName = "Astra Linux";
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
                .build();
    }

    public void updateVmInfo() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("update_vm_info").product(this).build());
    }

    public void stopHard() {
        stopHard("stop_vm_hard");
    }

    public void stopSoft() {
        stopSoft("stop_vm_soft");
    }

    public void start() {
        start("start_vm");
    }

    public void restart() {
        restart("reset_vm");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    public void resize(Flavor flavor) {
        resize("resize_vm", flavor);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_vm");
    }

    public void createSnapshot(int lifetime) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("create_group_snapshot").product(this)
                .data(new JSONObject().put("lifetime", lifetime)).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, SNAPSHOT_PATH), "Снапшот не найден");
    }

    public void deleteSnapshot() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("delete_group_snapshot").product(this).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, SNAPSHOT_PATH), "Снапшот существует");
    }

    public void updateOsVm() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("update_os_vm").product(this).build());
    }
}
