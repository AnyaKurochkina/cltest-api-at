package api.cloud.orderService;

import api.Tests;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ApacheKafkaCluster;
import models.cloud.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;

import java.util.Collections;

import static models.cloud.orderService.products.ApacheKafkaCluster.KAFKA_CREATE_TOPICS;
import static org.junit.jupiter.api.Assertions.assertAll;

@Epic("Продукты")
@Feature("ApacheKafkaCluster")
@Tags({@Tag("regress"), @Tag("negative"), @Tag("prod"), @Tag("apachekafkacluster")})
@Disabled
public class ApacheKafkaClusterNegativeTest extends Tests {

    @Tag("actions")
    @TmsLink("719578")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "[{index}] Негативные тесты создания топика {0}")
    public void negativeCreateKafkaTopic(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            assertAll("Проверка ошибки при передачи неверных параметров топика",
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 1, 5184000001L, "TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 1, 1800000, "*TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 51, 1800000, "_TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("create", 1, 1800000, "TopicName")));
        }
    }

    @Tag("actions")
    @TmsLink("725948")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "[{index}] Создание Topic Kafka(топик существует) {0}")
    public void CreateKafkaTopicIfExist(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            String topicName = "CreateKafkaTopicIfExist";
            kafka.createTopics(Collections.singletonList(topicName));
            checkIncorrectTopic(kafka, new KafkaTopic("delete", 1, 1800000, topicName));
        }
    }

    public void checkIncorrectTopic(ApacheKafkaCluster kafkaCluster, KafkaTopic topic) {
        OrderServiceSteps.sendAction(ActionParameters.builder().name(KAFKA_CREATE_TOPICS).product(kafkaCluster)
                .data(new JSONObject("{\"topics\": " + JsonHelper.toJson(topic) + "}")).build()).assertStatus(422);
    }

}
