package api.cloud.rpcDjangoRouter.inputQueue;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.rpcRouter.InputQueueResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.InputQueueSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("InputQueue")
@DisabledIfEnv("prod")
public class InputQueueExportTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Экспорт InputQueue")
    @TmsLink("")
    @Test
    public void exportInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        Response exportResponse = exportInputQueue(inputQueue.getId());
        InputQueueResponse exportedQueue = exportResponse.jsonPath().getObject("InputQueue", InputQueueResponse.class);
        exportedQueue.setRules(Collections.singletonList(exportResponse.jsonPath()
                .getObject("rel_revers_models.rules[0].Rule.id", Integer.class)));
        assertEquals(inputQueue, exportedQueue);
    }

    @SneakyThrows
    @DisplayName("API. RPC-Django-Router. Экспорт нескольких InputQueue")
    @TmsLink("")
    @Test
    public void exportInputQueuesTest() {
        InputQueueResponse inputQueue = createInputQueue();
        InputQueueResponse inputQueue2 = createInputQueue();
        InputQueueResponse inputQueue3 = createInputQueue();
        ExportEntity e = new ExportEntity(inputQueue.getId());
        ExportEntity e2 = new ExportEntity(inputQueue2.getId());
        ExportEntity e3 = new ExportEntity(inputQueue3.getId());
        exportInputQueueById(new ExportData(Arrays.asList(e, e2, e3)).toJson());
    }
}
