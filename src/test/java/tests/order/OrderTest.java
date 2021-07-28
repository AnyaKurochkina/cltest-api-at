package tests.order;

import models.interfaces.IProduct;
import models.products.RabbitMq;
import models.products.Rhel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.Steps;

import java.util.stream.Stream;

public class OrderTest extends Steps {

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethod")
    public void order(IProduct IProduct) {
        IProduct.order("");
        IProduct.reset();
        IProduct.stop("soft");
        IProduct.start();
        IProduct.stop("hard");
        IProduct.delete();
    }

    static Stream<Arguments> dataProviderMethod() {
        return Stream.of(Arguments.arguments(
                Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("vSphere").build(),
                Rhel.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("Nutanix").build(),
                RabbitMq.builder().env("DEV").segment("dev-srv-app").dataCentre("5").platform("vSphere").build()
        ));
    }
}
