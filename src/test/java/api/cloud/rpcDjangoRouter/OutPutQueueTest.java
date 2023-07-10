package api.cloud.rpcDjangoRouter;

import api.Tests;
import com.google.gson.Gson;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.OutputQueue;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static models.Entity.serialize;
import static org.junit.jupiter.api.Assertions.*;
import static steps.rpcRouter.OutputQueueSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class OutPutQueueTest extends Tests {

    @DisplayName("Создание OutPutQueue")
    @TmsLink("")
    @Test
    public void createOutPutQueueTest() {
        OutputQueue queue = OutputQueue.builder()
                .name("create_output_queue:test_api")
                .build();
        OutputQueueResponse outPutQueue = createOutPutQueue(queue.toJson()).extractAs(OutputQueueResponse.class);
        OutputQueue createdQueue = getOutPutQueueByName(outPutQueue.getName());
        assertEquals(queue, createdQueue);
    }

    @DisplayName("Удаление OutPutQueue")
    @TmsLink("")
    @Test
    public void deleteOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        deleteOutPutQueue(outPutQueue.getId());
        assertFalse(isOutPutQueueExist(outPutQueue.getName()));
    }

    @DisplayName("Обновление OutPutQueue")
    @TmsLink("")
    @Test
    public void updateOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        JSONObject serializedQueue = serialize(outPutQueue);
        serializedQueue.put("name", "name:test_api");
        OutputQueueResponse updatedQueue = new Gson().fromJson(String.valueOf(serializedQueue), OutputQueueResponse.class);
        OutputQueueResponse updatedOutputQueueResponse = updateOutPutQueue(outPutQueue.getId(), serializedQueue);
        assertEquals(updatedQueue, updatedOutputQueueResponse);
    }

    @DisplayName("Частичное обновление OutPutQueue")
    @TmsLink("")
    @Test
    public void partialUpdateOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        String newName = "partial_update:test_api";
        partialUpdateOutPutQueue(outPutQueue.getId(), new JSONObject().put("name", newName));
        assertEquals(newName, getOutPutQueueById(outPutQueue.getId()).getName());
    }

    @DisplayName("Получение списка OutputQueue")
    @TmsLink("")
    @Test
    public void getOutPutQueueListTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        List<OutputQueue> outPutQueueList = getOutPutQueueList();
        assertTrue(outPutQueueList.contains(outPutQueue));
    }

    @DisplayName("Копирование OutputQueue")
    @TmsLink("")
    @Test
    public void copyOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        OutputQueueResponse outputQueueResponse = copyOutPutQueue(outPutQueue.getId());
        assertEquals(outPutQueue.getName() + "-clone", outputQueueResponse.getName());
    }
}
