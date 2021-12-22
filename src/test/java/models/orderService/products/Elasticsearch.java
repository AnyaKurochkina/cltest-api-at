package models.orderService.products;

import core.helper.Http;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Flavor;
import org.json.JSONObject;

import static core.helper.Configure.OrderServiceURL;

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
    String platform;
    @ToString.Include
    String osVersion;
    @ToString.Include
    String elasticsearchVersion;
    String domain;
    Flavor flavorData;
    Flavor flavorMaster;
    Flavor flavorKibana;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/elasticsearch.json";
        productName = "Elasticsearch X-pack cluster";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            projectId = project.getId();
        }
        if (productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = new Http(OrderServiceURL)
                .setProjectId(projectId)
                .body(toJson())
                .post("projects/" + projectId + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = array.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        compareCostOrderAndPrice();
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        flavorData = referencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_data:DEV").get(0);
        flavorMaster = referencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_master:DEV").get(0);
        flavorKibana = referencesStep.getFlavorsByPageFilterLinkedList(this, "flavor:elasticsearch_kibana:DEV").get(0);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.flavor_data", new JSONObject(flavorData.toString()))
                .set("$.order.attrs.flavor_master", new JSONObject(flavorMaster.toString()))
                .set("$.order.attrs.flavor_kibana", new JSONObject(flavorKibana.toString()))
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .set("$.order.label", getLabel())
                .build();
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point", "/app", 10);
    }

    @Override
    //Удалить кластер EK Xpack
    protected void delete() {
        delete("delete_elasticsearch_xpack");
    }

    //Удалить хост
    public void deleteHost(String action) {
        orderServiceSteps.executeAction("delete_vm", this, null);
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

    public void resize() {
        resize("resize_vm");
    }
}
