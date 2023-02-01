package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.utils.ssh.SshClient;
import io.qameta.allure.Step;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.portalBack.AccessGroup;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

@Deprecated
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Rhel extends IProduct {
    @ToString.Include
    String osVersion;
    Flavor flavor;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/rhel.json";
        Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            setProjectId(project.getId());
        }
        if(productName == null) {
            if(isTest())
                productName = "RHEL General Application";
            else
                productName = "Rhel";
        }
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
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    public JSONObject toJson() {
        String accessGroup = PortalBackSteps.getRandomAccessGroup(getProjectId(), getDomain());
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.project_name", getProjectId())
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();
    }


    //Перезагрузить по питанию
    public void restart() {
        restart("reset_vm");
    }

    //Выключить принудительно
    public void stopHard() {
        stopHard("stop_vm_hard");
    }

    //Выключить
    public void stopSoft() {
        stopSoft("stop_vm_soft");
    }

    //Включить
    public void start() {
        start("start_vm");
    }

    public void resize(Flavor flavor) {
        resize("resize_vm", flavor);
    }
    public void expandMountPoint(){
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_vm");
    }

    public void checkCreateUseSsh(String userName) {
        GlobalUser globalUser = GlobalUser.builder().username(userName).build().createObject();
        String host = (String) OrderServiceSteps.getProductsField(this, "product_data.find{it.type=='vm'}.hostname");
        SshClient ssh = new SshClient(host, globalUser.getUsername(), globalUser.getPassword());
        ssh.connectAndExecuteListCommand("ls");
    }
}
