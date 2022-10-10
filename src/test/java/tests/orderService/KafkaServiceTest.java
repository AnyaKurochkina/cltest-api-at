package tests.orderService;

import core.enums.KafkaRoles;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.KafkaService;
import models.orderService.products.Moon;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@Epic("Продукты")
@Feature("Kafka service")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("kafka_service"), @Tag("prod")})
public class KafkaServiceTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
//    @TmsLink("847365")
    @ParameterizedTest(name = "Создание {0}")
    void create(KafkaService product) {
        //noinspection EmptyTryBlock
        try (KafkaService kafkaService = product.createObjectExclusiveAccess()) {}
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
//    @TmsLink("847365")
    @ParameterizedTest(name = "Создание/удаление acl {0}")
    void createAcl(KafkaService product) {
        try (KafkaService kafkaService = product.createObjectExclusiveAccess()) {
            kafkaService.createAclRole("cert1", KafkaRoles.CONSUMER);
            kafkaService.deleteAclRole("cert1", KafkaRoles.CONSUMER);
        }
    }

//    @TmsLink("847367")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление {0}")
    @MarkDelete
    void delete(KafkaService product) {
        try (KafkaService kafkaService = product.createObjectExclusiveAccess()) {
            kafkaService.deleteObject();
        }
    }
}
