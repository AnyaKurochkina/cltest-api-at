package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.portalBack.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class HcpBucket extends IProduct {

    private String bucketName;
    private String segment;
    private String platform;
    private Double hardQuota;
    private String dataCentre;
    private String servicePlan;
    private Boolean replication;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/hcp_bucket.json";
        productName = "HCP bucket";
        initProduct();
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.net_segment", segment)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.service_plan", servicePlan)
                .set("$.order.label", getLabel())
                .build();
    }

    @Override
    protected void create() {
        createProduct();
    }

    @Step("Измененить параметры версионирования")
    public void changeBucketVersioning(){
        orderServiceSteps.executeAction("change_bucket_versioning", this, new JSONObject("{\"bucket\":{\"versioning\":{\"prune\":true,\"enabled\":true,\"pruneDays\":10}}}"), this.getProjectId());
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, "data[0].config.bucket.versioning.prune"), "Очистка не активирована");
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, "data[0].config.bucket.versioning.enabled"), "Версионирование не активировалось");
    }

    @Step("Измененить конфигурацию бакета")
    public void changeBucketConfig(){
        orderServiceSteps.executeAction("change_bucket_config", this, new JSONObject("{\"bucket\":{\"hard_quota\":20.48,\"service_plan\":\"Sata_Tier\",\"replication_enabled\":false}}"), this.getProjectId());
        Float hardQuota = (Float) orderServiceSteps.getProductsField(this, "data[0].config.bucket.hard_quota");
        Assertions.assertEquals(20.48F, hardQuota, "Макс. объем не изменился! Макс. объем = " + hardQuota);
    }

    @Step("Настроить ACL")
    public void createOrChangeBucketAcls(String serviceAccId, String serviceAccTitle){
        orderServiceSteps.executeAction("create_or_change_bucket_acls", this,
                new JSONObject(String.format("{\"user_name\":{\"name\":\"%s\",\"title\":\"%s\"},\"permissions\":[\"READ\",\"READ_ACL\",\"WRITE\",\"WRITE_ACL\"]}", serviceAccId, serviceAccTitle)), this.getProjectId());
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("remove_bucket_product");
    }
}
