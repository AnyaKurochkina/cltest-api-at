package api.cloud.orderService;

import core.enums.KafkaRoles;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.KafkaService;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;


@Epic("Продукты")
@Feature("Kafka service")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("kafka_service"), @Tag("prod")})
public class KafkaServiceTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1232494")
    @ParameterizedTest(name = "Создание {0}")
    void create(KafkaService product) {
        //noinspection EmptyTryBlock
        try (KafkaService kafkaService = product.createObjectExclusiveAccess()) {}
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLinks({@TmsLink("1232495"), @TmsLink("1232497")})
    @ParameterizedTest(name = "Создание/удаление acl {0}")
    void createAcl(KafkaService product) {
        try (KafkaService kafkaService = product.createObjectExclusiveAccess()) {
            kafkaService.createAclRole("cert1", KafkaRoles.CONSUMER);
            kafkaService.deleteAclRole("cert1", KafkaRoles.CONSUMER);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLinks({@TmsLink("1232500"), @TmsLink("1232501")})
    @ParameterizedTest(name = "Создание/удаление acl группы {0}")
    void createAclGroup(KafkaService product) {
        try (KafkaService kafkaService = product.createObjectExclusiveAccess()) {
            kafkaService.createAclGroup("1418_topic_name_consumergroup_group");
            kafkaService.deleteAclGroup("1418_topic_name_consumergroup_group");
        }
    }

    @TmsLink("1232502")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление {0}")
    @MarkDelete
    void delete(KafkaService product) {
        try (KafkaService kafkaService = product.createObjectExclusiveAccess()) {
            kafkaService.deleteObject();
        }
    }
}
