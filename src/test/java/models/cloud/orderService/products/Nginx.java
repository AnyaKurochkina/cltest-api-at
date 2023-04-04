package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.utils.ssh.SshClient;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

import java.text.SimpleDateFormat;
import java.util.Date;

import static core.utils.AssertUtils.assertContains;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Nginx extends IProduct {
    @ToString.Include
    String osVersion;
    Flavor flavor;
    String role;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/nginx.json";
        if(productName == null)
            productName = "Nginx Astra";
        role = isDev() ? "superuser" : "user";
        initProduct();
        if(flavor == null)
            flavor = getMinFlavor();
        if(osVersion == null)
            osVersion = getRandomOsVersion();
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
        String accessGroup = PortalBackSteps.getRandomAccessGroup(getProjectId(), getDomain(), "compute");
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].role", role)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.on_support", /*isTest()*/getSupport())
                .set("$.order.label", getLabel())
                .build();
    }

    public void resize(Flavor flavor) {
        resize("resize_vm", flavor);
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

    public void expandMountPoint(){
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    @SneakyThrows
    public void updateCerts() {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
        super.updateCerts("nginx_update_certs");
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата больше либо равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
    }

    @Override
    public void checkUseSsh() {
        String ip = (String) OrderServiceSteps.getProductsField(this, VM_IP_PATH);
        SshClient ssh = new SshClient(ip, envType());
        assertContains(ssh.execute("sudo systemctl status nginx | grep active"), "Active: active (running)");
    }
}
