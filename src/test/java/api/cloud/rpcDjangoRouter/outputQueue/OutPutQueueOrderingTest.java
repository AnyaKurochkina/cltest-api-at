package api.cloud.rpcDjangoRouter.outputQueue;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.rpcRouter.OutputQueueSteps.createOutPutQueue;
import static steps.rpcRouter.OutputQueueSteps.getOrderingByFieldOutPutQueueList;


@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("OutPutQueue")
@DisabledIfEnv("prod")
public class OutPutQueueOrderingTest extends Tests {

    @DisplayName("Получение списка OutputQueue отсортированного по title")
    @TmsLink("")
    @Test
    public void orderingOutputQueueListByTitleTest() {
        createOutPutQueue();
        createOutPutQueue();
        createOutPutQueue();
        List<OutputQueueResponse> outPutQueueList = getOrderingByFieldOutPutQueueList("title");
        for (int i = 0; i < outPutQueueList.size() - 1; i++) {
            OutputQueueResponse currentQueue = outPutQueueList.get(i);
            OutputQueueResponse nextQueue = outPutQueueList.get(i + 1);
            assertTrue(currentQueue.getTitle().compareTo(nextQueue.getTitle()) <= 0
                    , String.format("%s стоит выше чем %s", currentQueue.getTitle(), nextQueue.getTitle()));
        }
    }

    @DisplayName("Получение списка OutputQueue отсортированного по name")
    @TmsLink("")
    @Test
    public void orderingOutputQueueListByNameTest() {
        createOutPutQueue();
        createOutPutQueue();
        createOutPutQueue();
        List<OutputQueueResponse> outPutQueueList = getOrderingByFieldOutPutQueueList("name");
        for (int i = 0; i < outPutQueueList.size() - 1; i++) {
            OutputQueueResponse currentQueue = outPutQueueList.get(i);
            OutputQueueResponse nextQueue = outPutQueueList.get(i + 1);
            assertTrue(currentQueue.getName().compareTo(nextQueue.getName()) < 0,
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }

    @DisplayName("Получение списка OutputQueue отсортированного по create_dt")
    @TmsLink("")
    @Test
    public void orderingOutputQueueListByCreateDtTest() {
        createOutPutQueue();
        createOutPutQueue();
        createOutPutQueue();
        List<OutputQueueResponse> outPutQueueList = getOrderingByFieldOutPutQueueList("create_dt");
        for (int i = 0; i < outPutQueueList.size() - 1; i++) {
            OutputQueueResponse currentQueue = outPutQueueList.get(i);
            OutputQueueResponse nextQueue = outPutQueueList.get(i + 1);
            assertTrue(currentQueue.getCreate_dt().compareTo(nextQueue.getCreate_dt()) < 0,
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }

    @DisplayName("Получение списка OutputQueue отсортированного по id")
    @TmsLink("")
    @Test
    public void orderingOutputQueueListByIdTest() {
        createOutPutQueue();
        createOutPutQueue();
        createOutPutQueue();
        List<OutputQueueResponse> outPutQueueList = getOrderingByFieldOutPutQueueList("id");
        for (int i = 0; i < outPutQueueList.size() - 1; i++) {
            OutputQueueResponse currentQueue = outPutQueueList.get(i);
            OutputQueueResponse nextQueue = outPutQueueList.get(i + 1);
            assertTrue(currentQueue.getId() < nextQueue.getId(),
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }
}
