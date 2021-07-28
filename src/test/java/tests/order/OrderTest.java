package tests.order;

import models.orderService.interfaces.IProduct;
import models.orderService.RabbitMq;
import models.orderService.Rhel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.Steps;
import steps.keyCloak.KeyCloakSteps;
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
        KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
        testVars.setVariables("token", keyCloakSteps.getToken());
        product.order();
        testVars.setVariables("token", keyCloakSteps.getToken());
        product.reset();
        testVars.setVariables("token", keyCloakSteps.getToken());
        product.stopSoft();
        testVars.setVariables("token", keyCloakSteps.getToken());
        product.start();
        testVars.setVariables("token", keyCloakSteps.getToken());
        product.stopHard();
        testVars.setVariables("token", keyCloakSteps.getToken());
        product.delete();
    }

    static Stream<Arguments> dataProviderMethod() {
        return Stream.of(
                Arguments.arguments(Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build()),
                Arguments.arguments(RabbitMq.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build())
        );
    }
}
