package tests.orderService;

import models.orderService.interfaces.IProduct;
import models.orderService.products.*;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Набор для создания продуктов")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.OrderTest")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod")})
public class OrderTest extends Tests {

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethod")
    public void order(IProduct product) {
        product.order();
        switch (product.getClass().getSimpleName()){
            case "OpenShiftProject":
                ((OpenShiftProject) product).changeProject();
                ((OpenShiftProject) product).deleteProject();
                break;
            case "Windows":
                product.restart();
                product.stopSoft();
                product.resize();
                product.start();
                product.stopHard();
                product.delete();
                break;
            case "Redis":
            case "PostgreSQL":
                product.expand_mount_point();
                product.restart();
                product.stopSoft();
                product.start();
                product.stopHard();
                product.delete();
                break;
            case "RabbitMQCluster":
                product.expand_mount_point();
                product.rabbitmq_create_user();
                product.restart();
                product.stopSoft();
                product.start();
                product.stopHard();
                product.delete();
                break;
            case "ApacheKafkaCluster":
                ((ApacheKafkaCluster) product).updateCerts();
                ((ApacheKafkaCluster) product).createTopic("topicName");
                ((ApacheKafkaCluster) product).createAcl("*");
                ((ApacheKafkaCluster) product).deleteTopic("topicName");
                product.restart();
                product.expand_mount_point();
                product.stopSoft();
                product.start();
                product.delete();
                break;
            default:
                product.expand_mount_point();
                product.restart();
                product.stopSoft();
                product.resize();
                product.start();
                product.stopHard();
                product.delete();
        }
    }

    static Stream<Arguments> dataProviderMethod() {
        return Stream.of(
                  Arguments.arguments(ApacheKafkaCluster.builder().env("TEST").kafkaVersion("2.13-2.4.1").segment("test-srv-synt").dataCentre("5").platform("vsphere").build()),
                Arguments.arguments(ApacheKafkaCluster.builder().env("TEST").kafkaVersion("2.13-2.4.1").segment("test-srv-synt").dataCentre("5").platform("Nutanix").build())
//                Arguments.arguments(Nginx.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(Windows.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("Microsoft Windows Server 2019").build()),
//                Arguments.arguments(OpenShiftProject.builder().env("DEV").resourcePoolLabel("ds0-bank01 - Demo").build()),
//                Arguments.arguments(Redis.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(ApacheKafka.builder().env("DEV").kafkaVersion("2.13-2.4.1").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build())//
//                Arguments.arguments(OpenShiftProject.builder().env("DEV").resourcePoolLabel("e5b4d171-1cbb-4b93-8c98-79836c11ce67").build()),
//                Arguments.arguments(Rhel.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()),
//                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("7.latest").build()),
//                Arguments.arguments(RabbitMQCluster.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build())//,
//                Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresql_version("12").build()),
//                Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresql_version("11").build())
//                Arguments.arguments(Redis.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").build())
//                Arguments.arguments(WildFly.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()
        );
    }
}
