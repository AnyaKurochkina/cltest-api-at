package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.ServiceAccount;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.HcpBucket;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Hcp Bucket")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("hcpbucket"), @Tag("prod")})
public class HcpBucketTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(HcpBucket product) {
        //noinspection EmptyTryBlock
        try (HcpBucket hcpBucket = product.createObjectExclusiveAccess()) {
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Измененить параметры версионирования {0}")
    void changeBucketVersioning(HcpBucket product) {
        try (HcpBucket hcpBucket = product.createObjectExclusiveAccess()) {
            hcpBucket.checkPreconditionStatusProduct(ProductStatus.CREATED);
            hcpBucket.changeBucketVersioning();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Измененить конфигурацию бакета {0}")
    void changeBucketConfig(HcpBucket product) {
        try (HcpBucket hcpBucket = product.createObjectExclusiveAccess()) {
            hcpBucket.checkPreconditionStatusProduct(ProductStatus.CREATED);
            hcpBucket.changeBucketConfig();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Настроить ACL бакета {0}")
    void editAcl(HcpBucket product) {
        try (HcpBucket hcpBucket = product.createObjectExclusiveAccess()) {
            hcpBucket.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ServiceAccount account = ServiceAccount.builder()
                    .title("serviceAccForStaticKey")
                    .projectId(hcpBucket.getProjectId())
                    .build()
                    .createObjectPrivateAccess();
            account.createStaticKey();
            hcpBucket.createOrChangeBucketAcls(account.getId(), account.getTitle());
            account.deleteStaticKey();
            account.deleteObject();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить HCP Bucket {0}")
    @MarkDelete
    void delete(HcpBucket product) {
        try (HcpBucket bucket = product.createObjectExclusiveAccess()) {
            bucket.deleteObject();
        }
    }
}
