package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.portalBack.AccessGroup;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import steps.orderService.OrderServiceSteps;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class ApacheAirflow extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    String domain;
    String role;
    Flavor flavor;
    String osVersion;
    String airflowVersion;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/apache_airflow.json";
        productName = "Apache Airflow";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        if (airflowVersion == null)
            airflowVersion = getRandomProductVersionByPathEnum("airflow_version.enum");
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
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();
    }


    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
    }

    public void resize() {
        resize("resize_vm");
    }

    public void restart() {
        restart("reset_vm");
    }

    public void stopSoft() {
        stopSoft("stop_vm_soft");
    }

    public void start() {
        start("start_vm");
    }

    public void stopHard() {
        stopHard("stop_vm_hard");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }
}
