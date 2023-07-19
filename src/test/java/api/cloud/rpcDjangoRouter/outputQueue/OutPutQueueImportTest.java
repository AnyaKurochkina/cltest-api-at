package api.cloud.rpcDjangoRouter.outputQueue;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.rpcRouter.OutputQueueSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class OutPutQueueImportTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Импорт OutputQueue")
    @TmsLink("")
    @Test
    public void importOutputQueueTest() {
        OutputQueueResponse outPutQueue = createOutPutQueue();
        String filePath = Configure.RESOURCE_PATH + "/json/rpcDjangoRouter/importOutputQueue.json";
        DataFileHelper.write(filePath, exportOutPutQueue(outPutQueue.getId()).toString());
        deleteOutPutQueue(outPutQueue.getId());
        importOutPutQueue(filePath);
        DataFileHelper.delete(filePath);
        assertTrue(isOutPutQueueExist(outPutQueue.getName()), "OutputQueue не существует");
        deleteOutPutQueue(getOutPutQueueByName(outPutQueue.getName()).getId());
    }
}
