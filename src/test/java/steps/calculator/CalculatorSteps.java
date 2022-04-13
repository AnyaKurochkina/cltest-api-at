package steps.calculator;

import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import models.calculator.CostOrder;
import steps.Steps;

import static core.helper.Configure.CalculatorURL;

public class CalculatorSteps extends Steps {

    @SneakyThrows
    public static <T> T deserialize(String object, Class<?> clazz) {
        return (T) JsonHelper.getCustomObjectMapper().readValue(object, clazz);
    }

    @Step("Получение информации в калькуляторе о заказе {orderId}")
    public static CostOrder getCostOrderByOrderId(String orderId) {
        String response = new Http(CalculatorURL)
                .get("/orders/{}/", orderId)
                .assertStatus(200)
                .toString();
        return deserialize(response, CostOrder.class);
    }
}
