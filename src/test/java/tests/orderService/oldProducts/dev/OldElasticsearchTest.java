package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Elasticsearch;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("ElasticSearch OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_elasticsearch"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldElasticsearchTest extends Tests {

    Elasticsearch elastic = Elasticsearch.builder()
            .projectId("proj-67nljbzjtt")
            .productId("f7aec597-14d5-48c8-a4fe-25af4f19e5d5")
            .orderId("c3a941a5-1e9f-4409-b661-ad655fcc4a71")
            .productName("Elasticsearch X-pack cluster")
            .build();

    @Order(1)
    @DisplayName("Расширить Elasticsearch OLD")
    @Test
    void expandMountPoint() {
        try {
            elastic.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            elastic.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Включить Elasticsearch OLD")
    @Test
    void start() {
        elastic.stopHard();
        elastic.start();
    }

    @Order(3)
    @DisplayName("Выключить Elasticsearch OLD")
    @Test
    void stopSoft() {
        elastic.stopSoft();
        elastic.start();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию Elasticsearch OLD")
    @Test
    void resize() {
        elastic.stopHard();
        try {
            elastic.resize();
        } finally {
            elastic.start();
        }
    }

    @Order(5)
    @DisplayName("Перезагрузить Elasticsearch OLD")
    @Test
    void restart() {
        elastic.restart();
    }

    @Order(6)
    @DisplayName("Выключить принудительно Elasticsearch OLD")
    @Test
    void stopHard() {
        elastic.stopHard();
    }
}
