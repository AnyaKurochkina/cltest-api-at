package tests.orderService;

import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
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

import static models.orderService.products.ApacheKafkaCluster.KAFKA_CREATE_TOPICS;
import static org.junit.jupiter.api.Assertions.assertAll;

@Epic("Продукты")
@Feature("ApacheKafkaCluster")
@Tags({@Tag("regress"), @Tag("negative"), @Tag("prod")})
public class ApacheKafkaClusterNegativeTest extends Tests {
    final OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @Tag("actions")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Негативные тесты создания топика над {0}")
    public void negativeCreateKafkaTopic(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
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

    public void checkIncorrectTopic(ApacheKafkaCluster kafkaCluster, KafkaTopic topic){
        orderServiceSteps.sendAction(KAFKA_CREATE_TOPICS, kafkaCluster, new JSONObject("{\"topics\": " + JsonHelper.toJson(topic) + "}"))
                .assertStatus(422);
    }

}
