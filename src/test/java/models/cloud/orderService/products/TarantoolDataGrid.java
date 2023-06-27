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

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class TarantoolDataGrid extends IProduct {
    @ToString.Include
    String osVersion;
    Flavor flavor;
    String tarantoolVersion;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/tarantool.json";
        productName = "Tarantool Data Grid Astra";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (dataCentre == null)
            setDataCentre(OrderServiceSteps.getDataCentre(this));
        if (platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        if (tarantoolVersion == null)
            tarantoolVersion = getRandomProductVersionByPathEnum("tarantool_version.enum");
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
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.access_group", getAccessGroup())
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", getAccessGroup())
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.ad_integration", true)
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.tarantool_version", getTarantoolVersion())
                .set("$.order.attrs.layout", getIdGeoDistribution("rps-2000", envType().toUpperCase(), "tdg", "cluster"))
                .build();
    }

    @Step("Расширить")
    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

//    @Step("Обновить версию приложения")
//    public void updateApp() {
//        JSONObject jsonData = new JSONObject().put("user_name", getUsers()).put("users_password", password);
//        OrderServiceSteps.executeAction("tdg_update_version", this, jsonData, this.getProjectId());
//        save();
//    }

    @Step("Удалить")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

}
