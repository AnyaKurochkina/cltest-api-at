package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import org.json.JSONObject;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;
import steps.resourceManager.ResourceManagerSteps;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class S3Ceph extends IProduct {
    String dataCentre;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/s3.json";
        productName = "S3 CEPH Tenant";
        initProduct();
        if(segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if(dataCentre == null)
            setDataCentre(OrderServiceSteps.getDataCentre(this));
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.net_segment", getSegment())
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
        JsonPath path = ResourceManagerSteps.getProjectJsonPath(projectId);
        return path.getString("data.environment_prefix.name") + "-" +
                path.getString("data.information_system.code")  + "-" + name;
    }

    @Step("Удалить бакет")
    public void deleteBucket(String name) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_bucket_delete").product(this).data(new JSONObject().put("name", name)).build());
    }

    @Step("Добавить бакет")
    public void addBucket(BucketAttrs attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_bucket_add").product(this).data(new JSONObject(JsonHelper.toJson(attrs))).build());
    }

    @Step("Изменить бакет")
    public void updateBucket(BucketAttrs attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_bucket_update").product(this).data(new JSONObject(JsonHelper.toJson(attrs))).build());
    }

    @Step("Добавить пользователя")
    public void addUser(String userName, String accessKey, String secretKey) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_user_add").product(this)
                .data(new JSONObject().put("user_name", userName).put("access_key", accessKey).put("secret_key", secretKey)).build());
    }

    @Step("Удалить пользователя")
    public void deleteUser(String userName) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_user_delete").product(this).data(new JSONObject().put("user_name", userName)).build());
    }

    @Step("Добавить политику")
    public void addPolicy(PolicyAttrs attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_policy_add").product(this).data(new JSONObject(JsonHelper.toJson(attrs))).build());
    }

    @Step("Изменить политику")
    public void updatePolicy(PolicyAttrs attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_policy_update").product(this).data(new JSONObject(JsonHelper.toJson(attrs))).build());
    }

    @Step("Удалить политику")
    public void deletePolicy(PolicyAttrs attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_policy_delete").product(this).data(new JSONObject(JsonHelper.toJson(attrs))).build());
    }

    @Step("Изменить правило жизненного цикла")
    public void updateRule(RoleAttrs attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_bucket_lr_update").product(this).data(new JSONObject(JsonHelper.toJson(attrs))).build());
    }

    @Step("Добавить правило жизненного цикла")
    public void addRule(RoleAttrs attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_bucket_lr_create").product(this).data(new JSONObject(JsonHelper.toJson(attrs))).build());
    }

    @Step("далить правило жизненного цикла")
    public void deleteRule(RoleAttrs attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("s3_ceph_bucket_lr_delete").product(this).data(new JSONObject().put("name", attrs.getName())).build());
    }

    @Step("Удалить тенант")
    @Override
    protected void delete() {
        delete("s3_ceph_tenant_delete");
    }

    @Data
    @Builder
    public static class BucketAttrs {
//        BucketAttrs.Versioning versioning;
        int maxSizeGb;
        String name;
        boolean versioning;

//        @Data
//        @Builder
//        public static class Versioning {
//            boolean prune;
//            boolean enabled;
//            @Builder.Default
//            int pruneDays = 1;
//        }
    }

    @Data
    @Builder
    public static class PolicyAttrs {
        PolicyAttrs.Policy policy;
        String userId;
        @Builder.Default
        String selectedRights = "Настраиваемые";
        @Builder.Default
        String prefix = "*";
        String bucketName;

        @Builder
        public static class Policy {
            boolean write;
            boolean read;
            boolean putBucketCors;
            boolean delete;
            boolean abortMultipartUpload;
        }
    }

    @Data
    @Builder
    public static class RoleAttrs {
        RoleAttrs.Filter filter;
        String condition;
        int days;
        String name;
        String type;
        boolean versioning;

        @Builder
        public static class Filter {
            @Builder.Default
            String type = "Prefix";
            String value;
        }
    }
}
