package models.orderService.products;

import core.helper.JsonHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
import models.subModels.Flavor;
import org.json.JSONObject;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class ElasticsearchOpensearchCluster extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    @ToString.Include
    String osVersion;
    @ToString.Include
    String elasticsearchVersion;
    String domain;
    Flavor flavorData;
    Flavor flavorMaster;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/elasticsearch_opensearch_cluster.json";
        productName = "Elasticsearch Opensearch cluster";
        initProduct();
        if(osVersion == null)
            osVersion = getRandomOsVersion();
        if(elasticsearchVersion == null)
            elasticsearchVersion = getRandomProductVersionByPathEnum("elasticsearch_version.enum");
        if(dataCentre == null)
            dataCentre = orderServiceSteps.getDomainBySegment(this, segment);
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        flavorData = referencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_data:DEV").get(0);
        flavorMaster = referencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_master:DEV").get(0);
//        flavorKibana = referencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_kibana:DEV").get(0);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.flavor_data", new JSONObject(flavorData.toString()))
                .set("$.order.attrs.flavor_master", new JSONObject(flavorMaster.toString()))
//                .set("$.order.attrs.flavor_kibana", new JSONObject(flavorKibana.toString()))
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.system_adm_groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.user_app_groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.adm_app_groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .set("$.order.label", getLabel())
                .build();
    }

    @Override
    protected void create() {
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        orderServiceSteps.executeAction("check_vm", this, null);
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
}
