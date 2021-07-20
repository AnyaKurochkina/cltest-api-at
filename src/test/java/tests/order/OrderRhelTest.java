package tests.order;

import core.exception.CustomException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.Hooks;
import steps.OrderServiceSteps;

import java.io.IOException;
import java.util.stream.Stream;

import static core.helper.JsonHelper.shareData;

@Order(2)
public class OrderRhelTest extends Hooks {

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethod")
    public void test(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException, CustomException {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.CreateOrderWithOutline(product, env, segment, dataCentre, platform);
        orderServiceSteps.CheckOrderStatus("success");
        orderServiceSteps.ExecuteAction("reset_vm");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_vm_soft");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("start_vm");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_vm_hard");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("delete_vm");
        orderServiceSteps.CheckActionStatus("success");

    }

    static Stream<Arguments> dataProviderMethod() {
        return Stream.of(Arguments.arguments("Rhel", "DEV", "dev-srv-app", "5", "vSphere"));/*,
                Arguments.arguments("Rhel", "DEV", "dev-srv-app", "5", "vSphere"),
                Arguments.arguments("Apache_Kafka", "DEV", "dev-srv-app", "5", "Nutanix"),
                Arguments.arguments("Apache_Kafka", "DEV", "dev-srv-app", "5", "vSphere"));*/
    }
}
