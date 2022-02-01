package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Elasticsearch;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты DEV")
@Feature("ElasticSearch OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_elasticsearch"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldElasticsearchTest extends Tests {

    final Elasticsearch elastic = Elasticsearch.builder()
            .projectId("proj-67nljbzjtt")
            .productId("f7aec597-14d5-48c8-a4fe-25af4f19e5d5")
            .orderId("c3a941a5-1e9f-4409-b661-ad655fcc4a71")
            .productName("Elasticsearch X-pack cluster")
            .build();

    @Order(1)
    @DisplayName("Расширить Elasticsearch OLD")
    @Test
    void expandMountPoint() {
        if (elastic.productStatusIs(STOPPED)) {
            elastic.start();
        }
        elastic.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Включить Elasticsearch OLD")
    @Test
    void start() {
        if (elastic.productStatusIs(STARTED)) {
            elastic.stopHard();
        }
        elastic.start();
    }

    @Order(3)
    @DisplayName("Выключить Elasticsearch OLD")
    @Test
    void stopSoft() {
        if (elastic.productStatusIs(STOPPED)) {
            elastic.start();
        }
        elastic.stopSoft();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию Elasticsearch OLD")
    @Test
    void resize() {
        if (elastic.productStatusIs(STARTED)) {
            elastic.stopHard();
        }
        elastic.resize(elastic.getMaxFlavor());
        elastic.resize(elastic.getMinFlavor());
    }

    @Order(5)
    @DisplayName("Перезагрузить Elasticsearch OLD")
    @Test
    void restart() {
        if (elastic.productStatusIs(STOPPED)) {
            elastic.start();
        }
        elastic.restart();
    }

    @Order(6)
    @DisplayName("Выключить принудительно Elasticsearch OLD")
    @Test
    void stopHard() {
        if (elastic.productStatusIs(STOPPED)) {
            elastic.start();
        }
        elastic.stopHard();
    }
}
