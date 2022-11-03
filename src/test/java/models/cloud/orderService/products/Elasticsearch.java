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
import models.cloud.portalBack.AccessGroup;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

@Deprecated
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Elasticsearch extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String osVersion;
    @ToString.Include
    String elasticsearchVersion;
    String domain;
    Flavor flavorData;
    Flavor flavorMaster;
    Flavor flavorKibana;
    String adminPassword;
    String kibanaPassword;
    String fluentdPassword;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/elasticsearch.json";
        productName = "Elasticsearch X-pack cluster";
        initProduct();
        if(adminPassword == null)
            adminPassword = "F5pFBbA23mvugDibV";
        if(kibanaPassword == null)
            kibanaPassword = "RnXLM4Ms3XQi";
        if(fluentdPassword == null)
            fluentdPassword = "jP9W4Yqsz8iSNX532dO";
        if(osVersion == null)
            osVersion = getRandomOsVersion();
        if(elasticsearchVersion == null)
            elasticsearchVersion = getRandomProductVersionByPathEnum("elasticsearch_version.enum");
        if(dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        flavorData = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_data:DEV").get(0);
        flavorMaster = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_master:DEV").get(0);
        flavorKibana = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_kibana:DEV").get(0);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.flavor_data", new JSONObject(flavorData.toString()))
                .set("$.order.attrs.flavor_master", new JSONObject(flavorMaster.toString()))
                .set("$.order.attrs.flavor_kibana", new JSONObject(flavorKibana.toString()))
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.admin_password", adminPassword)
                .set("$.order.attrs.kibana_password", kibanaPassword)
                .set("$.order.attrs.fluentd_password", fluentdPassword)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    @Override
    //Удалить кластер EK Xpack
    protected void delete() {
        delete("delete_elasticsearch_xpack");
    }

    //Удалить хост
    public void deleteHost(String action) {
        OrderServiceSteps.executeAction("delete_vm", this, null, this.getProjectId());
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
}
