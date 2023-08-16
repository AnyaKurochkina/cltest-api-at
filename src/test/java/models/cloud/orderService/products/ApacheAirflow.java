package models.cloud.orderService.products;

import com.mifmif.common.regex.Generex;
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
import models.cloud.subModels.DbUser;
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
    String role;
    Flavor flavor;
    String osVersion;
    String airflowVersion;
    String dbServer;
    DbUser dbUser;
    String pgAdminPassword;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/apache_airflow.json";
        productName = "Apache Airflow Astra";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (airflowVersion == null)
            airflowVersion = getRandomProductVersionByPathEnum("airflow_version.enum");
        if(segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if(dataCentre == null)
            setDataCentre(OrderServiceSteps.getDataCentre(this));
        if(platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if(domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        String accessGroup = getAccessGroup();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.cluster_name", new Generex("at-[a-z]{6}").random())
                .set("$.order.attrs.airflow_version", airflowVersion)
                .set("$.order.attrs.postgresql_config.db_host", dbServer)
                .set("$.order.attrs.postgresql_config.db_user", dbUser.getUsername())
                .set("$.order.attrs.postgresql_config.db_database", dbUser.getNameDB())
                .set("$.order.attrs.postgresql_config.db_password", pgAdminPassword)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup)
                //TODO: нужна тех группа!
                .set("$.order.attrs.deploy_grants[0].groups[0]", accessGroup)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", getSupport())
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
