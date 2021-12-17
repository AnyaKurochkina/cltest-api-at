package tests.orderService;

import core.helper.MarkDelete;
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
@Tags({@Tag("regress"), @Tag("orders"), @Tag("elasticsearch"), @Tag("prod")})
public class ElasticsearchTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Elasticsearch product) {
        //noinspection EmptyTryBlock
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {}
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.checkPreconditionStatusProduct(ProductStatus.CREATED);
            elastic.expandMountPoint();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.checkPreconditionStatusProduct(ProductStatus.CREATED);
            elastic.stopHard();
            elastic.start();

        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.stopSoft();
            elastic.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            elastic.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            try {
                elastic.resize();
            } finally {
                elastic.start();
            }
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить по питанию {0}")
    void restart(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.deleteObject();
        }
    }
}
