package tests.orderService;

import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.ServiceAccount;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.HcpBucket;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Hcp Bucket")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("hcpbucket"), @Tag("prod")})
public class HcpBucketTest extends Tests {

    @TmsLink("581192")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(HcpBucket product) {
        //noinspection EmptyTryBlock
        try (HcpBucket hcpBucket = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("581457")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Измененить параметры версионирования {0}")
    void changeBucketVersioning(HcpBucket product) {
        try (HcpBucket hcpBucket = product.createObjectExclusiveAccess()) {
            hcpBucket.checkPreconditionStatusProduct(ProductStatus.CREATED);
            hcpBucket.changeBucketVersioning();
        }
    }

    @TmsLink("581198")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Измененить конфигурацию бакета {0}")
    void changeBucketConfig(HcpBucket product) {
        try (HcpBucket hcpBucket = product.createObjectExclusiveAccess()) {
            hcpBucket.checkPreconditionStatusProduct(ProductStatus.CREATED);
            hcpBucket.changeBucketConfig();
        }
    }

    @TmsLink("581200")
    @Tag("actions")
    @Disabled("Статический ключ не работает")
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

    @TmsLink("581195")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить HCP Bucket {0}")
    @MarkDelete
    void delete(HcpBucket product) {
        try (HcpBucket bucket = product.createObjectExclusiveAccess()) {
            bucket.deleteObject();
        }
    }
}
