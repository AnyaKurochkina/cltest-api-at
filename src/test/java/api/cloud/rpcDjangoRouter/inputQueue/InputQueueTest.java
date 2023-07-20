package api.cloud.rpcDjangoRouter.inputQueue;

import api.Tests;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.rpcRouter.InputQueueResponse;
import models.cloud.rpcRouter.RuleResponse;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static models.Entity.serialize;
import static org.junit.jupiter.api.Assertions.*;
import static steps.rpcRouter.InputQueueSteps.*;
import static steps.rpcRouter.RuleSteps.getRuleById;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("InputQueue")
@DisabledIfEnv("prod")
public class InputQueueTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Создание InputQueue")
    @TmsLink("")
    @Test
    public void createInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        InputQueueResponse createdQueue = getInputQueueById(inputQueue.getId());
        assertEquals(inputQueue, createdQueue);
    }

    @DisplayName("API. RPC-Django-Router. Удаление InputQueue")
    @TmsLink("")
    @Test
    public void deleteInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        deleteInputQueue(inputQueue.getId());
        assertFalse(isInputQueueExist(inputQueue.getName()));
    }

    @SneakyThrows
    @DisplayName("API. RPC-Django-Router. Обновление InputQueue")
    @TmsLink("")
    @Test
    public void updateInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        JSONObject serializedQueue = serialize(inputQueue);
        serializedQueue.put("name", "name:test_api");
        InputQueueResponse updatedQueue = new ObjectMapper().readValue(String.valueOf(serializedQueue), InputQueueResponse.class);
        InputQueueResponse updatedOutputQueueResponse = updateInputQueue(inputQueue.getId(), serializedQueue);
        assertEquals(updatedQueue, updatedOutputQueueResponse);
    }

    @DisplayName("API. RPC-Django-Router. Частичное обновление InputQueue")
    @TmsLink("")
    @Test
    public void partialUpdateInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        String newName = "partial_update:test_api";
        partialUpdateInputQueue(inputQueue.getId(), new JSONObject().put("name", newName));
        assertEquals(newName, getInputQueueById(inputQueue.getId()).getName());
    }

    @DisplayName("API. RPC-Django-Router. Получение списка InputQueue")
    @TmsLink("")
    @Test
    public void getInputQueueListTest() {
        InputQueueResponse inputQueue = createInputQueue();
        List<InputQueueResponse> inputQueueList = getInputQueueList();
        assertTrue(inputQueueList.contains(inputQueue));
    }

    @DisplayName("API. RPC-Django-Router. Копирование InputQueue")
    @TmsLink("")
    @Test
    public void copyInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        InputQueueResponse inputQueueResponse = copyInputQueue(inputQueue.getId());
        assertEquals(inputQueue.getName() + "-clone", inputQueueResponse.getName());
    }

    @DisplayName("API. RPC-Django-Router. Получение списка объектов использующих InputQueue")
    @TmsLink("")
    @Test
    public void getObjectListUsedInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        Response objectsUsedInputQueue = getObjectsUsedInputQueue(inputQueue.getId());
        String s = objectsUsedInputQueue.jsonPath().get().toString();
        assertEquals("{}", s);
    }

    @DisplayName("API. RPC-Django-Router. Получение списка объектов используемых в InputQueue")
    @TmsLink("")
    @Test
    public void getObjectListUsingInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        List<RuleResponse> objectsUsedInputQueue = getObjectsUsingInputQueue(inputQueue.getId()).jsonPath()
                .getList("Rule", RuleResponse.class);
        RuleResponse ruleById = getRuleById(inputQueue.getRules().get(0));
        RuleResponse ruleResponse = objectsUsedInputQueue.get(0);
        assertEquals(1, objectsUsedInputQueue.size());
        assertEquals(ruleById.getId(), ruleResponse.getId());
        assertEquals(ruleById.getName(), ruleResponse.getName());
        assertEquals(ruleById.getTitle(), ruleResponse.getTitle());
    }
}
