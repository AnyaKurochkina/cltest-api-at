package api.cloud.rpcDjangoRouter.exchange;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.ExchangeSteps.getObjectsUsedExchange;
import static steps.rpcRouter.OutputQueueSteps.createOutPutQueue;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Exchange")
@DisabledIfEnv("prod")
public class ExchangeTest extends Tests {
    @DisplayName("Получение списка объектов использующих Exchange")
    @TmsLink("")
    @Test
    public void getObjectListUsedExchangeTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        List<OutputQueueResponse> outputQueueList = getObjectsUsedExchange(outPutQueue.getExchange()).jsonPath()
                .getList("OutputQueue", OutputQueueResponse.class);
        OutputQueueResponse outputQueueResponse = outputQueueList.get(0);
        assertEquals(1, outputQueueList.size());
        assertEquals(outPutQueue.getName(), outputQueueResponse.getName());
        assertEquals(outPutQueue.getId(), outputQueueResponse.getId());
        assertEquals(outPutQueue.getTitle(), outputQueueResponse.getTitle());
    }
}
