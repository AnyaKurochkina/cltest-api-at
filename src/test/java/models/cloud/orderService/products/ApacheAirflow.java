package models.cloud.orderService.products;

import com.mifmif.common.regex.Generex;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.DbUser;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


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
    String deployRoleGroup;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/apache_airflow.json";
        productName = "Apache Airflow Astra";
        deployRoleGroup = "airflow_deploy";
        initProduct();
        if (airflowVersion == null)
            airflowVersion = getRandomProductVersionByPathEnum("airflow_version.enum");
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
        Project project = Project.builder().id(projectId).build().createObject();
        String accessGroup = accessGroup();
        String accessGroupTech = accessGroup("service-accounts", "AT-ORDER");
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.cluster_name", new Generex("at-[a-z]{6}").random())
                .set("$.order.attrs.airflow_version", airflowVersion)
                .set("$.order.attrs.postgresql_config.db_host", dbServer)
                .set("$.order.attrs.postgresql_config.db_user", dbUser.getUsername())
                .set("$.order.attrs.postgresql_config.db_database", dbUser.getNameDB())
                .set("$.order.attrs.postgresql_config.db_password", pgAdminPassword)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.web_console_grants[0].role", "Operator")
                .set("$.order.attrs.deploy_grants[0].groups[0]", accessGroupTech)
                .set("$.order.attrs.deploy_grants[0].role", deployRoleGroup)
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("check_vm").product(this).build());
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

    public static String CERT_END_DATE = "data.find{it.data.config.containsKey('certificate_expiration')}.data.config.certificate_expiration";

    @SneakyThrows
    public void updateCerts() {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, CERT_END_DATE));
        updateCerts("airflow_update_certs");
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, CERT_END_DATE));
        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate), "Предыдущая дата обновления сертификата больше либо равна новой дате обновления сертификата ");
    }

    public void updateGroupAddDagFiles(String accessGroupTechNew) {
        JSONObject data = new JSONObject().put("role", deployRoleGroup).append("groups", accessGroupTechNew);
        OrderServiceSteps.runAction(ActionParameters.builder().name("airflow_change_deploy_group").product(this).data(data).build());
    }

    public void airflowChangeWebAccess(List<String> accessGroups) {
        JSONObject data = new JSONObject().put("groups", accessGroups);
        OrderServiceSteps.runAction(ActionParameters.builder().name("airflow_change_web_access").product(this).data(data).build());
    }

    public void airflowInstallExtras() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("airflow_install_extras").product(this).build());
    }

    public void updateOs() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("airflow_update_os").product(this).build());
    }
}
