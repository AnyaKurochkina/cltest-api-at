package api.cloud.rpcDjangoRouter.exchange;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.rpcRouter.ExchangeResponse;
import models.cloud.rpcRouter.OutputQueue;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.ExchangeSteps.createExchangeWithOutAutoDelete;
import static steps.rpcRouter.ExchangeSteps.deleteExchange;
import static steps.rpcRouter.OutputQueueSteps.createOutPutQueue;
import static steps.rpcRouter.OutputQueueSteps.deleteOutPutQueue;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class NegativeExchangeTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Удаление Exchange используемого в OutputQueue")
    @TmsLink("")
    @Test
    public void deleteExchangeUsedInOutputQueueTest() {
        String name = "create_output_queue:to_test_api";
        ExchangeResponse exchange = createExchangeWithOutAutoDelete();
        JSONObject jsonObject = OutputQueue.builder()
                .name(name)
                .exchange(exchange.getId())
                .build()
                .toJson();
        OutputQueueResponse outputQueue = createOutPutQueue(jsonObject)
                .assertStatus(201)
                .extractAs(OutputQueueResponse.class);
        String errorMessage = deleteExchange(exchange.getId()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Нельзя удалить экземпляр (%s), он используется ({'OutputQueue': [{'name': '%s', 'id': %d, 'title': '%s'}]})",
                exchange.getName(), outputQueue.getName(), outputQueue.getId(), outputQueue.getTitle()), errorMessage);
        deleteOutPutQueue(outputQueue.getId());
        deleteExchange(exchange.getId());
    }
}
