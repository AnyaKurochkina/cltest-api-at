package api.cloud.rpcDjangoRouter.outputQueue;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.OutputQueueSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class OutPutQueueExportTest extends Tests {

    @DisplayName("Экспорт OutputQueue")
    @TmsLink("")
    @Test
    public void exportOutputQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        Response exportResponse = exportOutPutQueue(outPutQueue.getId());
        OutputQueueResponse exportedQueue = exportResponse.jsonPath().getObject("OutputQueue", OutputQueueResponse.class);
        exportedQueue.setExchange(exportResponse.jsonPath()
                .getObject("OutputQueue.exchange", Integer.class));
        assertEquals(outPutQueue, exportedQueue);
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких OutputQueue")
    @TmsLink("")
    @Test
    public void exportOutputQueuesTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        OutputQueueResponse outPutQueue2 = createOutPutQueue();
        OutputQueueResponse outPutQueue3 = createOutPutQueue();
        ExportEntity e = new ExportEntity(outPutQueue.getId());
        ExportEntity e2 = new ExportEntity(outPutQueue2.getId());
        ExportEntity e3 = new ExportEntity(outPutQueue3.getId());
        exportOutPutQueuesById(new ExportData(Arrays.asList(e, e2, e3)).toJson());
    }
}
