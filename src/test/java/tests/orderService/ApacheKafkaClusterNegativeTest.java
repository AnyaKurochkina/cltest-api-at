package tests.orderService;

import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.ApacheKafkaCluster;
import models.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

import java.util.Collections;

import static models.orderService.products.ApacheKafkaCluster.KAFKA_CREATE_TOPICS;
import static org.junit.jupiter.api.Assertions.assertAll;

@Epic("Продукты")
@Feature("ApacheKafkaCluster")
@Tags({@Tag("regress"), @Tag("negative"), @Tag("prod"), @Tag("apachekafkacluster")})
public class ApacheKafkaClusterNegativeTest extends Tests {

    @Tag("actions")
    @TmsLink("719578")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Негативные тесты создания топика {0}")
    public void negativeCreateKafkaTopic(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            assertAll("Проверка ошибки при передачи неверных параметров топика",
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 1, 1, 1, 1209600001, "TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 1, 1, 1, 1800000, "*TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 1, 1, 1, 1800000, "-TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 1, 1, 1, 1800000, "_TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 1, 11, 1, 1800000, "TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 7, 1, 1, 1800000, "TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("delete", 1, 1, 7, 1800000, "TopicName")),
                    () -> checkIncorrectTopic(kafka,
                            new KafkaTopic("create", 1, 1, 7, 1800000, "TopicName")));
        }
    }

    @Tag("actions")
    @TmsLink("725948")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Topic Kafka(топик существует) {0}")
    public void CreateKafkaTopicIfExist(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            String topicName = "CreateKafkaTopicIfExist";
            kafka.createTopics(Collections.singletonList(topicName));
            checkIncorrectTopic(kafka, new KafkaTopic("delete", 1, 1, 1, 1800000, topicName));
        }
    }

    public void checkIncorrectTopic(ApacheKafkaCluster kafkaCluster, KafkaTopic topic){
        OrderServiceSteps.sendAction(KAFKA_CREATE_TOPICS, kafkaCluster, new JSONObject("{\"topics\": " + JsonHelper.toJson(topic) + "}"), kafkaCluster.getProjectId())
                .assertStatus(422);
    }

}
