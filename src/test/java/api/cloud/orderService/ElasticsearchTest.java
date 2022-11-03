package api.cloud.orderService;

import core.helper.http.Http;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import io.restassured.path.json.JsonPath;
import models.cloud.orderService.products.Elasticsearch;
import org.json.JSONObject;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import api.Tests;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Deprecated
@Epic("Продукты")
@Feature("ElasticSearch")
//@Tags({@Tag("regress"), @Tag("orders"), @Tag("elasticsearch"), @Tag("prod")})
public class ElasticsearchTest extends Tests {

    @TmsLink("396147")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Elasticsearch product) {
        //noinspection EmptyTryBlock
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("401283")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания. API Elasticsearch {0}")
    void checkElasticsearchApi(Elasticsearch product) throws ConnectException {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            JsonPath path = JsonPath.from(new JSONObject((Map) OrderServiceSteps.getProductsField(elastic, "", JSONObject.class)).toString());
            String apiUrl = path.getString("data.find{it.data.config.containsKey('api_url')}.data.config.api_url");
            List<String> ipList = path.getList("data.findAll{it.data.config.containsKey('default_v4_address')}.data.config.default_v4_address");
            String response = null;
            try {
                response = new Http(apiUrl)
                        .setSourceToken("Basic " + Base64.getEncoder().encodeToString(("admin:" + elastic.getAdminPassword()).getBytes(StandardCharsets.UTF_8)))
                        .getOrThrow("/_cat/nodes?v=true&pretty")
                        .assertStatus(200)
                        .toString();
            } catch (ConnectException e) {
                elastic.connectVmException(e.getMessage());
            }
            for (String ip : ipList)
                Assertions.assertTrue(response.contains(ip), "В списке нет ноды с адресом " + ip);
        }
    }

    @TmsLink("688500")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания. Exporter {0}")
    void checkElasticsearchExporter(Elasticsearch product) throws ConnectException {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            String exporterUrl = ((String) OrderServiceSteps.getProductsField(elastic,
                    "data.find{it.data.config.containsKey('api_url')}.data.config.additional_urls.elasticsearch-exporter"));
            String response = null;
            try {
                response = new Http(exporterUrl)
                        .setSourceToken("Basic " + Base64.getEncoder().encodeToString(("admin:" + elastic.getAdminPassword()).getBytes(StandardCharsets.UTF_8)))
                        .getOrThrow("/metrics")
                        .assertStatus(200)
                        .toString();
            } catch (ConnectException e) {
                elastic.connectVmException(e.getMessage());
            }
            Assertions.assertTrue(response.contains(",color=\"green\"} 1"),
                    "elasticsearch_cluster_health_status != 1");
        }
    }

    @TmsLink("401342")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания. Kibana {0}")
    void checkElasticsearchKibana(Elasticsearch product) throws ConnectException {
        Waiting.sleep(60000);
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            String kibanaUrl = ((String) OrderServiceSteps.getProductsField(elastic,
                    "data.find{it.data.config.containsKey('api_url')}.data.config.additional_urls.kibana"));
            try {
                new Http(kibanaUrl)
                        .setSourceToken("Basic " + Base64.getEncoder().encodeToString(("admin:" + elastic.getAdminPassword()).getBytes(StandardCharsets.UTF_8)))
                        .getOrThrow("/status")
                        .assertStatus(200);
            } catch (ConnectException e) {
                elastic.connectVmException(e.getMessage());
            }
        }
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

    @TmsLinks({@TmsLink("425726"),@TmsLink("425735")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(Elasticsearch product) {
        try (Elasticsearch elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            elastic.start();
        }
    }

    @TmsLink("425727")
    @Tag("actions")
    @Disabled
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
