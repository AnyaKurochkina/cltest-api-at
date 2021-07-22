package tests.order;

import core.exception.CustomException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.Hooks;
import steps.OrderServiceSteps;

import java.io.IOException;
import java.util.stream.Stream;
@ExtendWith(BeforeEx.class)
@Order(2)
public class OrderRhelTest extends Hooks {

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethodRhel")
    public void Rhel(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException, CustomException {
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

    static Stream<Arguments> dataProviderMethodRhel() {
        return Stream.of(Arguments.arguments("Rhel", "DEV", "dev-srv-app", "5", "vSphere"));
    }

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethodNginx")
    public void Nginx(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException, CustomException {
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
        orderServiceSteps.ExecuteAction("delete_two_layer");
        orderServiceSteps.CheckActionStatus("success");

    }

    static Stream<Arguments> dataProviderMethodNginx() {
        return Stream.of(Arguments.arguments("Nginx", "DEV", "dev-srv-app", "5", "vSphere"));
    }

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethodRabbitMQ")
    public void RabbitMQ(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException, CustomException {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.CreateOrderWithOutline(product, env, segment, dataCentre, platform);
        orderServiceSteps.CheckOrderStatus("success");
        orderServiceSteps.ExecuteAction("reset_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("start_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app_hard");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("delete_two_layer");
        orderServiceSteps.CheckActionStatus("success");

    }

    static Stream<Arguments> dataProviderMethodRabbitMQ() {
        return Stream.of(Arguments.arguments("RabbitMQ", "DEV", "dev-srv-app", "5", "vSphere"));
    }

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethodWindows")
    public void Windows(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException, CustomException {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.CreateOrderWithOutline(product, env, segment, dataCentre, platform);
        orderServiceSteps.CheckOrderStatus("success");
        orderServiceSteps.ExecuteAction("reset_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("start_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app_hard");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("delete_two_layer");
        orderServiceSteps.CheckActionStatus("success");

    }

    static Stream<Arguments> dataProviderMethodWindows() {
        return Stream.of(Arguments.arguments("Windows", "DEV", "dev-srv-app", "5", "vSphere"));
    }

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethodRedis")
    public void Redis(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException, CustomException {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.CreateOrderWithOutline(product, env, segment, dataCentre, platform);
        orderServiceSteps.CheckOrderStatus("success");
        orderServiceSteps.ExecuteAction("reset_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("start_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app_hard");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("delete_two_layer");
        orderServiceSteps.CheckActionStatus("success");

    }

    static Stream<Arguments> dataProviderMethodRedis() {
        return Stream.of(Arguments.arguments("Redis", "DEV", "dev-srv-app", "5", "vSphere"));
    }

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethodApacheKafka")
    public void ApacheKafka(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException, CustomException {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.CreateOrderWithOutline(product, env, segment, dataCentre, platform);
        orderServiceSteps.CheckOrderStatus("success");
        orderServiceSteps.ExecuteAction("reset_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("start_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app_hard");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("delete_two_layer");
        orderServiceSteps.CheckActionStatus("success");

    }

    static Stream<Arguments> dataProviderMethodApacheKafka() {
        return Stream.of(Arguments.arguments("Apache_Kafka", "DEV", "dev-srv-app", "5", "vSphere"));
    }

    @ParameterizedTest
    @DisplayName("Заказ продуктов с разной комбинацией среды, сегмента, дата-центра и платформы")
    @MethodSource("dataProviderMethodPosgreSQL")
    public void PosgreSQL(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException, CustomException {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.CreateOrderWithOutline(product, env, segment, dataCentre, platform);
        orderServiceSteps.CheckOrderStatus("success");
        orderServiceSteps.ExecuteAction("reset_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("start_app");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("stop_app_hard");
        orderServiceSteps.CheckActionStatus("success");
        orderServiceSteps.ExecuteAction("delete_two_layer");
        orderServiceSteps.CheckActionStatus("success");

    }

    static Stream<Arguments> dataProviderMethodPosgreSQL() {
        return Stream.of(Arguments.arguments("PosgreSQL", "DEV", "dev-srv-app", "5", "vSphere"));
    }
}
