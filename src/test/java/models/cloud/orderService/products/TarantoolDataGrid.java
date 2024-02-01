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
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
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
    String tarantoolVersion;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/tarantool.json";
        productName = "Tarantool Data Grid Astra";
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
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.access_group[0]", accessGroup())
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup())
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.ad_integration", true)
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.tarantool_version", getTarantoolVersion())
                .set("$.order.attrs.layout", getIdGeoDistribution("rps-2000"))
                .build();
    }

    @Step("Создать резервную копию")
    public void backup() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("tdg_backup").product(this).data(new JSONObject().put("dumb", "empty")).build());
        Assertions.assertEquals(1, (Integer) OrderServiceSteps.getProductsField(this, BACKUP_PATH), "Отсутствует backup");
    }

    @Step("Обновить сертификаты")
    public void updateCerts() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("tdg_update_certs").product(this).build());
    }

    @Step("Остановить сервисы")
    public void stopInstances(List<String> services) {
        JSONObject data = new JSONObject().put("type", "Instance").put("instances", services);
        OrderServiceSteps.runAction(ActionParameters.builder().name("tdg_stop_instances").product(this).data(data).build());
        for (String service : services)
            Assertions.assertEquals("off", OrderServiceSteps.getProductsField(this,
                    String.format(SERVICE_PATH, service)), "Статус сервиса " + service);
    }

    @Step("Запустить сервисы")
    public void startInstances(List<String> services) {
        JSONObject data = new JSONObject().put("type", "Instance").put("instances", services);
        OrderServiceSteps.runAction(ActionParameters.builder().name("tdg_start_instances").product(this).data(data).build());
        for (String service : services)
            Assertions.assertEquals("on", OrderServiceSteps.getProductsField(this,
                    String.format(SERVICE_PATH, service)), "Статус сервиса " + service);
    }

    @Step("Перезапустить сервисы")
    public void restartInstances(List<String> services) {
        JSONObject data = new JSONObject().put("type", "Instance").put("instances", services);
        OrderServiceSteps.runAction(ActionParameters.builder().name("tdg_restart_instances").product(this).data(data).build());
    }

    @Step("Удалить")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

}
