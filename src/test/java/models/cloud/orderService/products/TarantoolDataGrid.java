package models.cloud.orderService.products;

import core.helper.Configure;
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
import steps.orderService.OrderServiceSteps;

import java.util.List;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class TarantoolDataGrid extends IProduct {
    public static final String BACKUP_PATH = "data.find{it.type=='cluster'}.data.config.backup.size()";
    public static final String SERVICE_PATH = "data.find{it.type=='cluster'}.data.config.cluster.find{it.instance=='%s'}.state";
    @ToString.Include
    String osVersion;
    Flavor flavor;
    String tarantoolVersion;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/tarantool.json";
        productName = "Tarantool Data Grid Astra";
        if(Configure.ENV.equalsIgnoreCase("ift"))
            productName = "Tarantool Data Grid - Astra Linux";
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
                .set("$.order.attrs.access_group[0]", getAccessGroup())
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", getAccessGroup())
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.ad_integration", true)
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.tarantool_version", getTarantoolVersion())
                .set("$.order.attrs.layout", getIdGeoDistribution("rps-2000", String.format("%s:%s:%s","cluster", "tdg", envType().toUpperCase())))
                .build();
    }

    @Step("Создать резервную копию")
    public void backup() {
        OrderServiceSteps.executeAction("tdg_backup", this, new JSONObject().put("dumb", "empty"), this.getProjectId());
        Assertions.assertEquals(1, (Integer) OrderServiceSteps.getProductsField(this, BACKUP_PATH), "Отсутствует backup");
    }

    @Step("Обновить сертификаты")
    public void updateCerts() {
        OrderServiceSteps.executeAction("tdg_update_certs", this, null, this.getProjectId());
    }

    @Step("Остановить сервисы")
    public void stopInstances(List<String> services) {
        JSONObject data = new JSONObject().put("type", "Instance").put("instances", services);
        OrderServiceSteps.executeAction("tdg_stop_instances", this, data, this.getProjectId());
        for(String service : services)
            Assertions.assertEquals("off", OrderServiceSteps.getProductsField(this,
                String.format(SERVICE_PATH, service)), "Статус сервиса " + service);
    }

    @Step("Запустить сервисы")
    public void startInstances(List<String> services) {
        JSONObject data = new JSONObject().put("type", "Instance").put("instances", services);
        OrderServiceSteps.executeAction("tdg_start_instances", this, data, this.getProjectId());
        for(String service : services)
            Assertions.assertEquals("on", OrderServiceSteps.getProductsField(this,
                    String.format(SERVICE_PATH, service)), "Статус сервиса " + service);
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

}
