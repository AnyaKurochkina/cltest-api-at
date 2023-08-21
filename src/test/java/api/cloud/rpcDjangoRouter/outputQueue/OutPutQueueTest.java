package api.cloud.rpcDjangoRouter.outputQueue;

import api.Tests;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.rpcRouter.ExchangeResponse;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static models.Entity.serialize;
import static org.junit.jupiter.api.Assertions.*;
import static steps.rpcRouter.ExchangeSteps.getExchangeById;
import static steps.rpcRouter.OutputQueueSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class OutPutQueueTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Создание OutPutQueue")
    @TmsLink("")
    @Test
    public void createOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        OutputQueueResponse createdQueue = getOutPutQueueByName(outPutQueue.getName());
        assertEquals(outPutQueue, createdQueue);
    }

    @DisplayName("API. RPC-Django-Router. Удаление OutPutQueue")
    @TmsLink("")
    @Test
    public void deleteOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        deleteOutPutQueue(outPutQueue.getId());
        assertFalse(isOutPutQueueExist(outPutQueue.getName()));
    }

    @SneakyThrows
    @DisplayName("API. RPC-Django-Router. Обновление OutPutQueue")
    @TmsLink("")
    @Test
    public void updateOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        JSONObject serializedQueue = serialize(outPutQueue);
        serializedQueue.put("name", "name:test_api");
        OutputQueueResponse updatedQueue = new ObjectMapper().readValue(String.valueOf(serializedQueue), OutputQueueResponse.class);
        OutputQueueResponse updatedOutputQueueResponse = updateOutPutQueue(outPutQueue.getId(), serializedQueue);
        assertEquals(updatedQueue, updatedOutputQueueResponse);
    }

    @DisplayName("API. RPC-Django-Router. Частичное обновление OutPutQueue")
    @TmsLink("")
    @Test
    public void partialUpdateOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        String newName = "partial_update:test_api";
        partialUpdateOutPutQueue(outPutQueue.getId(), new JSONObject().put("name", newName));
        assertEquals(newName, getOutPutQueueById(outPutQueue.getId()).getName());
    }

    @DisplayName("API. RPC-Django-Router. Получение списка OutputQueue")
    @TmsLink("")
    @Test
    public void getOutPutQueueListTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        List<OutputQueueResponse> outPutQueueList = getOutPutQueueList();
        assertTrue(outPutQueueList.contains(outPutQueue));
    }

    @DisplayName("API. RPC-Django-Router. Копирование OutputQueue")
    @TmsLink("")
    @Test
    public void copyOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        OutputQueueResponse outputQueueResponse = copyOutPutQueue(outPutQueue.getId());
        assertEquals(outPutQueue.getName() + "-clone", outputQueueResponse.getName());
    }

    @DisplayName("API. RPC-Django-Router. Получение списка объектов использующих OutputQueue")
    @TmsLink("")
    @Test
    public void getObjectListUsedOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        Response objectsUsedOutputQueue = getObjectsUsedOutputQueue(outPutQueue.getId());
        String s = objectsUsedOutputQueue.jsonPath().get().toString();
        assertEquals("{}", s);
    }

    @DisplayName("API. RPC-Django-Router. Получение списка объектов используемых в OutputQueue")
    @TmsLink("")
    @Test
    public void getObjectListUsingOutputQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        List<ExchangeResponse> objectsUsedOutputQueue = getObjectsUsingOutputQueue(outPutQueue.getId()).jsonPath()
                .getList("Exchange", ExchangeResponse.class);
        ExchangeResponse exchange = getExchangeById(outPutQueue.getExchange());
        ExchangeResponse exchangeResponse = objectsUsedOutputQueue.get(0);
        assertEquals(1, objectsUsedOutputQueue.size());
        assertEquals(exchange.getId(), exchangeResponse.getId());
        assertEquals(exchange.getName(), exchangeResponse.getName());
        assertEquals(exchange.getTitle(), exchangeResponse.getTitle());
    }
}
