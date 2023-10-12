package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.ElasticsearchOpensearchCluster;
import org.junit.EnabledIfEnv;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Epic("Продукты")
@Feature("Elasticsearch Opensearch Cluster Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("elasticsearchOpenSearchClusterAstra"), @Tag("prod")})
public class ElasticsearchOpensearchClusterAstraTest extends Tests {

    @TmsLink("796246")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(ElasticsearchOpensearchCluster product) {
        //noinspection EmptyTryBlock
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @EnabledIfEnv({"prod", "blue"})
    @ParameterizedTest(name = "[{index}] Заказ на быстрых дисках {0}")
    void checkDiskVm(ElasticsearchOpensearchCluster product) {
        List<String> envs = Arrays.asList("LT", "DEV", "PROD");
        Assumptions.assumeTrue(envs.contains(product.getEnv()), "Тест только для сред " + Arrays.toString(envs.toArray()));
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            String type = (elastic.getEnv().equals("LT") || elastic.getEnv().equals("PROD")) ? "nvme" : "ssd";
            elastic.checkVmDisk(new HashMap<String, String>() {{
                put("master", type);
                put("data", type);
            }});
        }
    }

    @TmsLink("796250")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Проверить конфигурацию {0}")
    void refreshVmConfig(ElasticsearchOpensearchCluster product) {
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.refreshVmConfig();
        }
    }

    @Disabled
    @TmsLink("796249")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить {0}")
    void stopSoft(ElasticsearchOpensearchCluster product) {
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.stopSoft();
            elastic.start();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("796244"), @TmsLink("796248")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить принудительно/Включить {0}")
    void stopHard(ElasticsearchOpensearchCluster product) {
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            elastic.start();
        }
    }

    @Disabled
    @TmsLink("796245")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Перезагрузить по питанию {0}")
    void restart(ElasticsearchOpensearchCluster product) {
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.restart();
        }
    }

    @TmsLink("1265630")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить выделенную ноду Kibana {0}")
    void addKibana(ElasticsearchOpensearchCluster product) {
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.addKibana();
        }
    }

    @TmsLink("796247")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    @MarkDelete
    void delete(ElasticsearchOpensearchCluster product) {
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.deleteObject();
        }
    }
}
