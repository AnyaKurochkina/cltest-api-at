package api.cloud.rpcDjangoRouter.outputQueue;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.rpcRouter.ExchangeResponse;
import models.cloud.rpcRouter.OutputQueue;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.ExchangeSteps.createExchange;
import static steps.rpcRouter.OutputQueueSteps.createOutPutQueue;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class NegativeOutPutQueueTest extends Tests {

    @DisplayName("Создание OutPutQueue без ':' в имени")
    @TmsLink("")
    @Test
    public void createOutPutQueueWithOutColonInNameTest() {
        String name = "create_output_queue_without_colon_in_name";
        ExchangeResponse exchange = createExchange();
        JSONObject jsonObject = OutputQueue.builder()
                .name(name)
                .exchange(exchange.getId())
                .build()
                .toJson();
        String errorMessage = createOutPutQueue(jsonObject).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Имя очереди (OutputQueue - %s) должно содержать двоеточие", name), errorMessage);
    }
}
