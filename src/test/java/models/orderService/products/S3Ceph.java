package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import org.json.JSONObject;
import steps.orderService.OrderServiceSteps;
import steps.resourceManager.ResourceManagerSteps;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class S3Ceph extends IProduct {
    private String segment;
    //    private String platform;
    private String dataCentre;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/s3.json";
        productName = "S3 CEPH Tenant";
        initProduct();
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.net_segment", segment)
                .set("$.order.product_id", productId)
//                .set("$.order.attrs.platform", platform)
                .set("$.order.label", getLabel())
                .build();
    }

    @Override
    protected void create() {
        createProduct();
    }

    public String getBucketName(String name){
        JsonPath path = ResourceManagerSteps.getProjectPath(projectId);
        return path.getString("data.environment_prefix.name") + "-" +
                path.getString("data.information_system.code")  + "-";
    }

    @Step("Удалить бакет")
    public void deleteBucket(String name) {
        OrderServiceSteps.executeAction("s3_ceph_bucket_delete", this, new JSONObject(String.format("{name: \"%s\"}", name)), this.getProjectId());
    }

    @Step("Добавить бакет")
    public void addBucket(BucketAttrs attrs) {
        OrderServiceSteps.executeAction("s3_ceph_bucket_add", this, new JSONObject(JsonHelper.toJson(attrs)), this.getProjectId());
    }

    @Step("Изменить бакет")
    public void updateBucket(BucketAttrs attrs) {
        OrderServiceSteps.executeAction("s3_ceph_bucket_update", this, new JSONObject(JsonHelper.toJson(attrs)), this.getProjectId());
    }

    @Step("Добавить пользователя")
    public void addUser(String userName, String accessKey, String secretKey) {
        OrderServiceSteps.executeAction("s3_ceph_user_add", this,
                new JSONObject().put("user_name", userName).put("access_key", accessKey).put("secret_key", secretKey), this.getProjectId());
    }

    @Step("Удалить пользователя")
    public void deleteUser(String userName) {
        OrderServiceSteps.executeAction("s3_ceph_user_delete", this, new JSONObject().put("user_name", userName), this.getProjectId());
    }

    @Step("Добавить политику")
    public void addPolicy(PolicyAttrs attrs) {
        OrderServiceSteps.executeAction("s3_ceph_policy_add", this, new JSONObject(JsonHelper.toJson(attrs)), this.getProjectId());
    }

    @Step("Изменить политику")
    public void updatePolicy(PolicyAttrs attrs) {
        OrderServiceSteps.executeAction("s3_ceph_policy_update", this, new JSONObject(JsonHelper.toJson(attrs)), this.getProjectId());
    }

    @Step("Удалить политику")
    public void deletePolicy(PolicyAttrs attrs) {
        OrderServiceSteps.executeAction("s3_ceph_policy_delete", this, new JSONObject(JsonHelper.toJson(attrs)), this.getProjectId());
    }

    @Step("Удалить тенант")
    @Override
    protected void delete() {
        delete("s3_ceph_tenant_delete");
    }

    @Data
    @Builder
    public static class BucketAttrs {
        BucketAttrs.Versioning versioning;
        int maxSizeGb;
        String name;

        @Data
        @Builder
        public static class Versioning {
            boolean prune;
            boolean enabled;
            @Builder.Default
            int pruneDays = 1;
        }
    }

    @Data
    @Builder
    public static class PolicyAttrs {
        PolicyAttrs.Policy policy;
        String userId;
        String bucketName;

        @Builder
        public static class Policy {
            PolicyId id;
        }

        public enum PolicyId{
            READ_WRITE,
            READ
        }
    }
}
