package tests.orderService;

import core.CacheService;
import io.qameta.allure.Allure;
import models.orderService.products.ApacheKafkaCluster;
import models.subModels.KafkaTopic;
import org.json.JSONObject;
//import org.junit.Assume;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Негативные тесты ApacheKafkaCluster")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Tags({@Tag("regress"), @Tag("negative"), @Tag("prod")})
//@OrderLabel("tests.orderService.ApacheKafkaClusterNegativeTests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApacheKafkaClusterNegativeTest extends Tests {
    CacheService cacheService = new CacheService();
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @Order(1)
    @DisplayName("Негативные тесты создания топика")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideKafkaTopicNegative")
    public void negativeCreateKafkaTopic(String tmsId, KafkaTopic kafkaTopic) {
        Allure.tms("40." + tmsId, "");
        ApacheKafkaCluster apacheKafkaCluster = cacheService.entity(ApacheKafkaCluster.class)
                .getEntityWithoutAssert();
        Assumptions.assumeTrue(apacheKafkaCluster != null, "Не найден ApacheKafkaCluster для тестов");
        orderServiceSteps.sendAction("Создать Topic Kafka", apacheKafkaCluster, new JSONObject(CacheService.toJson(kafkaTopic)))
                .assertStatus(422);
    }

    private static Stream<Arguments> provideKafkaTopicNegative() {
        return Stream.of(
                Arguments.of("1", new KafkaTopic("delete", 1, 1, 1, 1209600001, "TopicName")),
                Arguments.of("2", new KafkaTopic("delete", 1, 1, 1, 1800000, "*TopicName")),
                Arguments.of("3", new KafkaTopic("delete", 1, 1, 1, 1800000, "-TopicName")),
                Arguments.of("4", new KafkaTopic("delete", 1, 1, 1, 1800000, "_TopicName")),
                Arguments.of("5", new KafkaTopic("delete", 1, 11, 1, 1800000, "TopicName")),
                Arguments.of("6", new KafkaTopic("delete", 7, 1, 1, 1800000, "TopicName")),
                Arguments.of("7", new KafkaTopic("delete", 1, 1, 7, 1800000, "TopicName")),
                Arguments.of("8", new KafkaTopic("create", 1, 1, 7, 1800000, "TopicName"))
        );
    }
}
