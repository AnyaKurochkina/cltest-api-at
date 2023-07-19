package api.cloud.rpcDjangoRouter.inputQueue;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.InputQueueResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.rpcRouter.InputQueueSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class InputQueueImportTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Импорт InputQueue")
    @TmsLink("")
    @Test
    public void importInputQueueTest() {
        InputQueueResponse inputQueue = createInputQueue();
        String filePath = Configure.RESOURCE_PATH + "/json/rpcDjangoRouter/importInputQueue.json";
        DataFileHelper.write(filePath, exportInputQueue(inputQueue.getId()).toString());
        deleteInputQueue(inputQueue.getId());
        importInputQueue(filePath);
        DataFileHelper.delete(filePath);
        assertTrue(isInputQueueExist(inputQueue.getName()), "InputQueue не существует");
        deleteInputQueue(getInputQueueByName(inputQueue.getName()).getId());
    }
}
