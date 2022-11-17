package models.cloud.orderService.products;

import core.helper.JsonHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.portalBack.AccessGroup;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

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
    String osVersion;
    @ToString.Include
    String elasticsearchVersion;
    String domain;
    String kibanaPassword;
    Flavor flavorData;
    Flavor flavorMaster;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/elasticsearch_opensearch_cluster.json";
        if(productName == null)
            productName = "Elasticsearch Opensearch cluster (Astra)";
        initProduct();
        if(osVersion == null)
            osVersion = getRandomOsVersion();
        if(elasticsearchVersion == null)
            elasticsearchVersion = getRandomProductVersionByPathEnum("elasticsearch_version.enum");
        if(kibanaPassword == null)
            kibanaPassword = "RnXLM4Ms3XQi";
        if(segment == null)
            segment = OrderServiceSteps.getNetSegment(this);
        if(dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        flavorData = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_data:DEV").get(0);
        flavorMaster = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_master:DEV").get(0);
//        flavorKibana = referencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_kibana:DEV").get(0);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.flavor_data", new JSONObject(flavorData.toString()))
                .set("$.order.attrs.flavor_master", new JSONObject(flavorMaster.toString()))
                .set("$.order.attrs.kibana_password", kibanaPassword)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.system_adm_groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.user_app_groups[0]", accessGroup.getPrefixName())
                .set("$.order.attrs.adm_app_groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.label", getLabel())
                .build();
    }

    @Override
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    public void addKibana(){
        Flavor flavorKibana = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_kibana:DEV").get(0);
        AccessGroup accessGroup = AccessGroup.builder().projectName(projectId).build().createObject();
        JSONObject object = JsonHelper.getJsonTemplate("/orders/elastic_open_search_add_kibana.json")
                .set("$.default_nic.net_segment", segment)
                .set("$.flavor_kibana", new JSONObject(flavorKibana.toString()))
                .set("$.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.data_center", dataCentre)
                .set("$.kibana_password", kibanaPassword)
                .build();
        OrderServiceSteps.executeAction("add_dedicated_kibana_node", this, object, this.getProjectId());
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
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

    @Override
    protected void delete() {
        delete("delete_elasticsearch_opensearch");
    }
}
