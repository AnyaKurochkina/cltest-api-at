package api.cloud.rpcDjangoRouter.exchange;

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
import static steps.rpcRouter.ExchangeSteps.*;
import static steps.rpcRouter.OutputQueueSteps.createOutPutQueue;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Exchange")
@DisabledIfEnv("prod")
public class ExchangeTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Создание Exchange")
    @TmsLink("")
    @Test
    public void createExchangeTest() {
        ExchangeResponse exchangeResponse = createExchange();
        ExchangeResponse createdExchange = getExchangeById(exchangeResponse.getId());
        assertEquals(exchangeResponse, createdExchange);
    }

    @DisplayName("API. RPC-Django-Router. Удаление Exchange")
    @TmsLink("")
    @Test
    public void deleteExchangeTest() {
        ExchangeResponse exchangeResponse = createExchange();
        deleteExchange(exchangeResponse.getId());
        assertFalse(isExchangeExist(exchangeResponse.getName()));
    }

    @SneakyThrows
    @DisplayName("API. RPC-Django-Router. Обновление Exchange")
    @TmsLink("")
    @Test
    public void updateExchangeTest() {
        ExchangeResponse exchangeResponse = createExchange();
        JSONObject serializedQueue = serialize(exchangeResponse);
        serializedQueue.put("name", "update_name_exchange_test_api");
        ExchangeResponse updatedExchange = new ObjectMapper().readValue(String.valueOf(serializedQueue), ExchangeResponse.class);
        ExchangeResponse updatedExchangeResponse = updateExchange(exchangeResponse.getId(), serializedQueue);
        assertEquals(updatedExchange, updatedExchangeResponse);
    }

    @DisplayName("API. RPC-Django-Router. Частичное обновление Exchange")
    @TmsLink("")
    @Test
    public void partialUpdateExchangeTest() {
        ExchangeResponse exchangeResponse = createExchange();
        String newName = "partial_update:test_api";
        partialUpdateExchange(exchangeResponse.getId(), new JSONObject().put("name", newName));
        assertEquals(newName, getExchangeById(exchangeResponse.getId()).getName());
    }

    @DisplayName("API. RPC-Django-Router. Получение списка Exchange")
    @TmsLink("")
    @Test
    public void getExchangeListTest() {
        ExchangeResponse exchangeResponse = createExchange();
        List<ExchangeResponse> exchangeList = getExchangeList();
        assertTrue(exchangeList.contains(exchangeResponse));
    }

    @DisplayName("API. RPC-Django-Router. Копирование Exchange")
    @TmsLink("")
    @Test
    public void copyExchangeTest() {
        ExchangeResponse exchange = createExchange();
        ExchangeResponse copyExchange = copyExchange(exchange.getId());
        assertEquals(exchange.getName() + "-clone", copyExchange.getName());
    }

    @DisplayName("API. RPC-Django-Router. Получение списка объектов использующих Exchange")
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

    @DisplayName("API. RPC-Django-Router. Получение списка объектов используемых в Exchange")
    @TmsLink("")
    @Test
    public void getObjectListUsingExchangeTest() {
        ExchangeResponse exchange = createExchange();
        Response objectsUsedExchange = getObjectsUsingExchange(exchange.getId());
        String s = objectsUsedExchange.jsonPath().get().toString();
        assertEquals("{}", s);
    }
}
