package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Elasticsearch;
import org.junit.MarkDelete;
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

    @TmsLink("396147")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Elasticsearch product) {
        //noinspection EmptyTryBlock
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("425724")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.expandMountPoint();
        }
    }

    @TmsLink("425735")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            elastic.start();

        }
    }

    @TmsLink("425725")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.stopSoft();
            elastic.start();
        }
    }

    @TmsLink("425726")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            elastic.start();
        }
    }

    @TmsLink("425727")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            try {
                elastic.resize(elastic.getMaxFlavor());
            } finally {
                elastic.start();
            }
        }
    }

    @TmsLink("425728")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.restart();
        }
    }

    @TmsLink("396155")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.deleteObject();
        }
    }
}
