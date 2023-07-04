package api.cloud.rpcDjangoRouter;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static steps.rpcRouter.OutputQueueSteps.createOutPutQueue;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class OutPutQueueTest extends Tests {

    @DisplayName("Создание OutPutQueue")
    @TmsLink("")
    @Test
    public void createOutPutQueueTest() {
        String queueName = "create_output_queue_test_api";
        createOutPutQueue(queueName);
    }
}
