package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Elasticsearch;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("ElasticSearch")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("elasticsearch")})
public class ElasticsearchTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Elasticsearch product) {
        Elasticsearch elastic = product.createObjectExclusiveAccess();
        elastic.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.checkPreconditionStatusProduct(ProductStatus.CREATED);
            elastic.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted
    void delete(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.deleteObject();
        }
    }
}
