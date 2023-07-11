package api.cloud.rpcDjangoRouter;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.OutputQueueSteps.createOutPutQueue;
import static steps.rpcRouter.OutputQueueSteps.exportOutPutQueue;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class ExportImportOutPutQueueTest extends Tests {

    @DisplayName("Экспорт OutPutQueue")
    @TmsLink("")
    @Test
    public void exportOutPutQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        OutputQueueResponse exportQueue = exportOutPutQueue(outPutQueue.getId()).jsonPath().getObject("OutputQueue", OutputQueueResponse.class);
        assertEquals(outPutQueue, exportQueue);
    }
}
