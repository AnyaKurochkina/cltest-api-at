package api.cloud.rpcDjangoRouter;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.OutputQueue;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        String queueName = "create:output_queue_test_api";
        OutputQueue outPutQueue = createOutPutQueue(queueName);
        OutputQueue createdQueue = getOutPutQueueByName(queueName);
        assertEquals(outPutQueue, createdQueue);
    }

    @DisplayName("Удаление OutPutQueue")
    @TmsLink("")
    @Test
    public void deleteOutPutQueueTest() {
        String queueName = "delete:output_queue_test_api";
        OutputQueue outPutQueue = createOutPutQueue(queueName);
        deleteOutPutQueue(outPutQueue.getId());
        assertFalse(isOutPutQueueExist(queueName));
    }

    @DisplayName("Обновление OutPutQueue")
    @TmsLink("")
    @Test
    public void updateOutPutQueueTest() {
        String queueName = "update:output_queue_test_api";
        OutputQueue outPutQueue = createOutPutQueue(queueName);
        deleteOutPutQueue(outPutQueue.getId());
        assertFalse(isOutPutQueueExist(queueName));
    }
}
