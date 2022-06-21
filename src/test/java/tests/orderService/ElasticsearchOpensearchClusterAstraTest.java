package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.ElasticsearchOpensearchCluster;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Elasticsearch Opensearch Cluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("elasticsearchOpenSearchClusterAstra"), @Tag("prod")})
public class ElasticsearchOpensearchClusterAstraTest extends Tests {
    final String productName = "Elasticsearch Opensearch cluster (Astra)";

    @TmsLink("796246")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("796250")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.refreshVmConfig();
        }
    }

    @TmsLink("796249")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.stopSoft();
            elastic.start();
        }
    }

    @TmsLinks({@TmsLink("796244"),@TmsLink("796248")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            elastic.start();
        }
    }

    @TmsLink("796245")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить по питанию {0}")
    void restart(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.restart();
        }
    }

    @TmsLink("796247")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.deleteObject();
        }
    }
}
