package tests.orderService;

import core.CacheService;
import models.orderService.products.ApacheKafkaCluster;
import models.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.orderService.OrderServiceSteps;

import java.util.stream.Stream;

@DisplayName("Негативные тесты ApacheKafkaCluster")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("regress"), @Tag("negative")})
@OrderLabel("tests.orderService.ApacheKafkaClusterNegativeTests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApacheKafkaClusterNegativeTests {
    CacheService cacheService = new CacheService();
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @Order(1)
    @DisplayName("Негативные тесты создания топика")
    @ParameterizedTest
    @MethodSource("provideKafkaTopicNegative")
    public void negativeCreateKafkaTopic(KafkaTopic kafkaTopic) {
        ApacheKafkaCluster apacheKafkaCluster = cacheService.entity(ApacheKafkaCluster.class)
                .getEntityWithoutAssert();
        Assume.assumeNotNull("Не найден ApacheKafkaCluster для тестов", apacheKafkaCluster);
        orderServiceSteps.sendAction(ApacheKafkaCluster.KAFKA_CREATE_TOPIC, apacheKafkaCluster, new JSONObject(cacheService.toJson(kafkaTopic)))
                .assertStatus(422);
    }

    private static Stream<Arguments> provideKafkaTopicNegative() {
        return Stream.of(
                Arguments.of(new KafkaTopic("delete", 1, 1, 1, 1209600001, "TopicName")),
                Arguments.of(new KafkaTopic("delete", 1, 1, 1, 1800000, "*TopicName")),
                Arguments.of(new KafkaTopic("delete", 1, 1, 1, 1800000, "-TopicName")),
                Arguments.of(new KafkaTopic("delete", 1, 1, 1, 1800000, "_TopicName")),
                Arguments.of(new KafkaTopic("delete", 1, 11, 1, 1800000, "TopicName")),
                Arguments.of(new KafkaTopic("delete", 7, 1, 1, 1800000, "TopicName")),
                Arguments.of(new KafkaTopic("delete", 1, 1, 7, 1800000, "TopicName")),
                Arguments.of(new KafkaTopic("create", 1, 1, 7, 1800000, "TopicName"))
        );
    }
}
