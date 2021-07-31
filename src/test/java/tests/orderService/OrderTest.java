package tests.orderService;

import models.orderService.*;
import models.orderService.interfaces.IProduct;
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
@Order(650)
@Tags({@Tag("regress"), @Tag("orders")})
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
            default:
                product.expand_mount_point();
                product.reset();
                product.stopSoft();
                product.start();
                product.stopHard();
                product.delete();
                break;
        }
    }

    static Stream<Arguments> dataProviderMethod() {
        return Stream.of(
                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()),
                Arguments.arguments(Redis.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
                Arguments.arguments(ApacheKafka.builder().env("DEV").kafkaVersion("2.13-2.4.1").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
                Arguments.arguments(OpenShiftProject.builder().env("DEV").resourcePoolId("e5b4d171-1cbb-4b93-8c98-79836c11ce67").build())
                //Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()),
                //Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("7.latest").build()),
                //Arguments.arguments(RabbitMq.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
                //Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresql_version("12").build()),
                //Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresql_version("11").build())
        );
    }
}
