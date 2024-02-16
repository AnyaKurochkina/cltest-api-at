package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.JsonTemplate;
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
import steps.orderService.ActionParameters;
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
    String osVersion;
    @ToString.Include
    String elasticsearchVersion;
    String kibanaPassword;
    Flavor flavorData;
    Flavor flavorMaster;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/elasticsearch_opensearch_cluster.json";
        if (productName == null)
            productName = "Elasticsearch Opensearch cluster (Astra)";
        initProduct();
        if (elasticsearchVersion == null)
            elasticsearchVersion = getRandomProductVersionByPathEnum("elasticsearch_version.enum");
        if (kibanaPassword == null)
            kibanaPassword = "RnXLM4Ms3XQi";
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
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        String accessGroup = accessGroup();
        flavorData = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:cluster:elasticsearch:data:" + envType() + ":" + getEnv().toLowerCase()).get(0);
        flavorMaster = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:cluster:elasticsearch:master:" + envType() + ":" + getEnv().toLowerCase()).get(0);
//        flavorKibana = referencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_kibana:DEV").get(0);
        JsonTemplate template = JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain());
        if (envType().contains("prod")) {
            template.put("$.order.attrs", "geo_distribution", true)
                    .put("$.order.attrs", "layout", getIdGeoDistribution("Elasticsearch_Master:3", "elasticsearch_os"));
        }
        return template.set("$.order.attrs.flavor_data", new JSONObject(flavorData.toString()))
                .set("$.order.attrs.flavor_master", new JSONObject(flavorMaster.toString()))
                .set("$.order.attrs.kibana_password", kibanaPassword)
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.elasticsearch_version", elasticsearchVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.system_adm_groups[0]", accessGroup)
                .set("$.order.attrs.user_app_groups[0]", accessGroup)
                .set("$.order.attrs.adm_app_groups[0]", accessGroup)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.label", getLabel())
                .build();
    }

    @Override
    protected void create() {
        createProduct();
    }

    public void addKibana() {
        Flavor flavorKibana = ReferencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:cluster:elasticsearch:kibana:" + envType() + ":" + getEnv().toLowerCase()).get(0);
        JSONObject object = JsonHelper.getJsonTemplate("/orders/elastic_open_search_add_kibana.json")
                .set("$.flavor_kibana", new JSONObject(flavorKibana.toString()))
                .set("$.default_nic.net_segment", getSegment())
                .set("$.data_center", OrderServiceSteps.getDataCentre(this))
                .set("$.kibana_password", kibanaPassword)
                .set("$.ad_logon_grants[0].groups[0]", accessGroup())
                .remove("$.ad_logon_grants", !isDev())
                .set("$.on_support", getSupport())
                .build();
        OrderServiceSteps.runAction(ActionParameters.builder().name("add_dedicated_kibana_node").product(this).data(object).build());
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("check_vm").product(this).build());
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
