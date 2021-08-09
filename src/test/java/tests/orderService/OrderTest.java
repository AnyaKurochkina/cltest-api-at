package tests.orderService;

import models.orderService.interfaces.IProduct;
import models.orderService.products.*;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
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
public class OrderTest implements Tests {

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @Source(ProductArgumentsProvider.PRODUCTS)
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
                ((Redis) product).resetPassword();
                product.expandMountPoint();
                product.restart();
                product.stopSoft();
                product.start();
                product.stopHard();
                product.delete();
                break;
            case "PostgreSQL":
                product.expandMountPoint();
                ((PostgreSQL) product).createDb("testdb");
                ((PostgreSQL) product).resetDbOwnerPassword();
                ((PostgreSQL) product).createDbmsUser("testuser", "user");
                ((PostgreSQL) product).resetPassword();
                ((PostgreSQL) product).removeDbmsUser();
                ((PostgreSQL) product).removeDb();
                product.restart();
                product.stopSoft();
                product.start();
                product.stopHard();
                product.delete();
                break;
            case "RabbitMQCluster":
                product.expandMountPoint();
                ((RabbitMQCluster) product).rabbitmqCreateUser();
                product.restart();
                product.stopSoft();
                product.start();
                product.stopHard();
                product.delete();
                break;
            default:
                product.expandMountPoint();
                product.restart();
                product.stopSoft();
                product.resize();
                product.start();
                product.stopHard();
                product.delete();
        }
    }

//    //IFT
//    static Stream<Arguments> dataProviderMethod() {
//        return Stream.of(
////                Arguments.arguments(OpenShiftProject.builder().env("DEV").resourcePoolLabel("ds0-bank01 - Demo").build()),
////
////                Arguments.arguments(Nginx.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
////                Arguments.arguments(Windows.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("Microsoft Windows Server 2019").build()),
////                Arguments.arguments(Redis.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
////                Arguments.arguments(ApacheKafka.builder().env("DEV").kafkaVersion("2.13-2.4.1").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
////                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()),
////                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("7.latest").build()),
////                Arguments.arguments(RabbitMQCluster.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresqlVersion("12").build()),
//                Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresqlVersion("11").build())
//        );
//    }

    //PROD
//    static Stream<Arguments> dataProviderMethod() {
//        return Stream.of(
//                Arguments.arguments(OpenShiftProject.builder().env("DEV").resourcePoolLabel("ds1-genr01.corp.dev.vtb - DEV-SRV-APP").build()),
//
          //        Arguments.arguments(ApacheKafkaCluster.builder().env("TEST").kafkaVersion("2.13-2.4.1").segment("test-srv-synt").dataCentre("5").platform("vsphere").build()),
           //     Arguments.arguments(ApacheKafkaCluster.builder().env("TEST").kafkaVersion("2.13-2.4.1").segment("test-srv-synt").dataCentre("5").platform("Nutanix").build())
//                Arguments.arguments(Nginx.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(Windows.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("Microsoft Windows Server 2019").build()),
//                Arguments.arguments(Redis.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(ApacheKafka.builder().env("DEV").kafkaVersion("2.13-2.4.1").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()),
//                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("7.latest").build()),
//                Arguments.arguments(RabbitMQCluster.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build())//,
//                Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresql_version("12").build()),
//                Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresql_version("11").build()),
//                Arguments.arguments(WildFly.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()),
//
//                Arguments.arguments(Nginx.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").build()),
//                Arguments.arguments(Windows.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").osVersion("Microsoft Windows Server 2019").build()),
//                Arguments.arguments(Redis.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").build()),
//                Arguments.arguments(ApacheKafka.builder().env("DEV").kafkaVersion("2.13-2.4.1").segment("dev-srv-app").dataCentre("5").platform("OpenStack").build()),
//                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").osVersion("8.latest").build()),
//                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").osVersion("7.latest").build()),
//                Arguments.arguments(RabbitMQCluster.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").build()),
//                Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").osVersion("8.latest").postgresql_version("12").build()),
//                Arguments.arguments(PostgreSQL.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").osVersion("8.latest").postgresql_version("11").build()),
//                Arguments.arguments(WildFly.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("OpenStack").osVersion("8.latest").build()),
//
//                Arguments.arguments(Nginx.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(Windows.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").osVersion("Microsoft Windows Server 2019").build()),
//                Arguments.arguments(Redis.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(ApacheKafka.builder().env("TEST").kafkaVersion("2.13-2.4.1").segment("test-srv-synt").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(Rhel.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()),
//                Arguments.arguments(Rhel.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").osVersion("7.latest").build()),
//                Arguments.arguments(RabbitMQCluster.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").build()),
//                Arguments.arguments(PostgreSQL.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresql_version("12").build()),
//                Arguments.arguments(PostgreSQL.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").osVersion("8.latest").postgresql_version("11").build()),
//                Arguments.arguments(WildFly.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("Nutanix").osVersion("8.latest").build()),
//
//                Arguments.arguments(Nginx.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").build()),
//                Arguments.arguments(Windows.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").osVersion("Microsoft Windows Server 2019").build()),
//                Arguments.arguments(Redis.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").build()),
//                Arguments.arguments(ApacheKafka.builder().env("TEST").kafkaVersion("2.13-2.4.1").segment("test-srv-synt").dataCentre("5").platform("vsphere").build()),
//                Arguments.arguments(Rhel.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").osVersion("8.latest").build()),
//                Arguments.arguments(Rhel.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").osVersion("7.latest").build()),
//                Arguments.arguments(RabbitMQCluster.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").build()),
//                Arguments.arguments(PostgreSQL.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").osVersion("8.latest").postgresql_version("12").build()),
//                Arguments.arguments(PostgreSQL.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").osVersion("8.latest").postgresql_version("11").build()),
//                Arguments.arguments(WildFly.builder().env("TEST").segment("test-srv-synt").dataCentre("5").platform("vsphere").osVersion("8.latest").build()),
//
//
//        );
//    }
}
