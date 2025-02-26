package api.cloud.orderService;

import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.S3Ceph;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;

@Epic("Продукты")
@Feature("S3 CEPH Tenant")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("s3_ceph"), @Tag("prod")})
public class S3CephTest extends Tests {
    final static String regexAccessKey = "[A-Z0-9]{20,30}";
    final static String regexSecretKey = "[a-zA-Z0-9]{40,50}";

    @TmsLink("974377")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Заказ {0}")
    void create(S3Ceph product, Integer num) {
//        noinspection EmptyTryBlock
        try (S3Ceph ignored = product.createObjectExclusiveAccess()) {}
    }

    @TmsLinks({@TmsLink("974378"), @TmsLink("974384")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить/Удалить бакет {0}")
    void addBucket(S3Ceph product, Integer num) {
        try (S3Ceph s3Ceph = product.createObjectExclusiveAccess()) {
            S3Ceph.BucketAttrs attrs = S3Ceph.BucketAttrs.builder()
                    .versioning(true)
                    .maxSizeGb(10)
                    .name(s3Ceph.getBucketName(new Generex("[a-z]{1}[a-z0-9-]{3,60}[a-z]{1}").random()))
                    .build();
            s3Ceph.addBucket(attrs);
            s3Ceph.deleteBucket(attrs.getName());
        }
    }

    @TmsLink("974462")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить бакет {0}")
    void updateBucket(S3Ceph product, Integer num) {
        try (S3Ceph s3Ceph = product.createObjectExclusiveAccess()) {
            S3Ceph.BucketAttrs attrs = S3Ceph.BucketAttrs.builder()
                    .versioning(true)
                    .maxSizeGb(10)
                    .name(s3Ceph.getBucketName(new Generex("[a-z]{1}[a-z0-9-]{3,60}[a-z]{1}").random()))
                    .build();
            s3Ceph.addBucket(attrs);
            try {
                attrs.setMaxSizeGb(11);
                attrs.setVersioning(false);
                s3Ceph.updateBucket(attrs);
            } finally {
                s3Ceph.deleteBucket(attrs.getName());
            }
        }
    }

    @TmsLinks({@TmsLink("974387"), @TmsLink("974386")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить/Удалить пользователя {0}")
    void addUser(S3Ceph product, Integer num) {
        try (S3Ceph s3Ceph = product.createObjectExclusiveAccess()) {
            String userName = new Generex("[a-z]{1}[a-z0-9-]{1,18}[a-z]{1}").random();
            s3Ceph.addUser(userName,
                    new Generex(regexAccessKey).random(),
                    new Generex(regexSecretKey).random());
            s3Ceph.deleteUser(userName);
        }
    }

    @TmsLinks({@TmsLink("974389"), @TmsLink("974390")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить/Удалить политику {0}")
    void addPolicy(S3Ceph product, Integer num) {
        try (S3Ceph s3Ceph = product.createObjectExclusiveAccess()) {
            S3Ceph.BucketAttrs bucketAttrs = S3Ceph.BucketAttrs.builder()
                    .versioning(true)
                    .maxSizeGb(10)
                    .name(s3Ceph.getBucketName(new Generex("[a-z]{1}[a-z0-9-]{3,60}[a-z]{1}").random()))
                    .build();
            s3Ceph.addBucket(bucketAttrs);
            try {
                String userName = new Generex("[a-z]{1}[a-z0-9-]{1,18}[a-z]{1}").random();
                s3Ceph.addUser(userName,
                        new Generex(regexAccessKey).random(),
                        new Generex(regexSecretKey).random());

                S3Ceph.PolicyAttrs policyAttrs = S3Ceph.PolicyAttrs.builder()
                        .policy(S3Ceph.PolicyAttrs.Policy.builder()
                                .read(true)
                                .write(true)
                                .build())
                        .bucketName(bucketAttrs.getName())
                        .userId(userName)
                        .build();
                s3Ceph.addPolicy(policyAttrs);
                s3Ceph.deletePolicy(policyAttrs);

                s3Ceph.deleteUser(userName);
            } finally {
                s3Ceph.deleteBucket(bucketAttrs.getName());
            }
        }
    }

    @TmsLinks({@TmsLink("1248910"), @TmsLink("1248914"), @TmsLink("1248915")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить/Изменить/Удалить правило жизненного цикла {0}")
    void addRule(S3Ceph product, Integer num) {
        try (S3Ceph s3Ceph = product.createObjectExclusiveAccess()) {
            S3Ceph.BucketAttrs bucketAttrs = S3Ceph.BucketAttrs.builder()
                    .versioning(true)
                    .maxSizeGb(10)
                    .name(s3Ceph.getBucketName(new Generex("[a-z]{1}[a-z0-9-]{3,60}[a-z]{1}").random()))
                    .build();
            s3Ceph.addBucket(bucketAttrs);
            try {
            S3Ceph.RoleAttrs roleAttrs = S3Ceph.RoleAttrs.builder()
                    .condition("days")
                    .type("Expiration")
                    .filter(S3Ceph.RoleAttrs.Filter.builder().build())
                    .name("rule")
                    .days(10)
                    .build();
                s3Ceph.addRule(roleAttrs);
                roleAttrs.setDays(11);
                roleAttrs.setFilter(S3Ceph.RoleAttrs.Filter.builder().value("prefix").build());
                s3Ceph.updateRule(roleAttrs);
                s3Ceph.deleteRule(roleAttrs);
            } finally {
                s3Ceph.deleteBucket(bucketAttrs.getName());
            }
        }
    }

    @TmsLink("974392")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить политику {0}")
    void updatePolicy(S3Ceph product, Integer num) {
        try (S3Ceph s3Ceph = product.createObjectExclusiveAccess()) {
            S3Ceph.BucketAttrs bucketAttrs = S3Ceph.BucketAttrs.builder()
                    .versioning(true)
                    .maxSizeGb(10)
                    .name(s3Ceph.getBucketName(new Generex("[a-z]{1}[a-z0-9-]{3,60}[a-z]{1}").random()))
                    .build();
            s3Ceph.addBucket(bucketAttrs);
            try {
                String userName = new Generex("[a-z]{1}[a-z0-9-]{1,18}[a-z]{1}").random();
                s3Ceph.addUser(userName,
                        new Generex(regexAccessKey).random(),
                        new Generex(regexSecretKey).random());

                S3Ceph.PolicyAttrs policyAttrs = S3Ceph.PolicyAttrs.builder()
                        .policy(S3Ceph.PolicyAttrs.Policy.builder()
                                .read(true)
                                .write(true)
                                .build())
                        .bucketName(bucketAttrs.getName())
                        .userId(userName)
                        .build();
                s3Ceph.addPolicy(policyAttrs);
                policyAttrs.setPolicy(S3Ceph.PolicyAttrs.Policy.builder()
                        .read(true)
                        .build());
                s3Ceph.updatePolicy(policyAttrs);

                s3Ceph.deleteUser(userName);
            } finally {
                s3Ceph.deleteBucket(bucketAttrs.getName());
            }
        }
    }


    @TmsLink("974393")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(S3Ceph product, Integer num) {
        try (S3Ceph s3Ceph = product.createObjectExclusiveAccess()) {
            s3Ceph.deleteObject();
        }
    }
}
