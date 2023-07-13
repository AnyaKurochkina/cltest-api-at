package api.cloud.rpcDjangoRouter.inputQueue;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.InputQueueResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.rpcRouter.InputQueueSteps.createInputQueue;
import static steps.rpcRouter.InputQueueSteps.getOrderingByFieldInputQueueList;


@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("InputQueue")
@DisabledIfEnv("prod")
public class InputQueueOrderingTest extends Tests {

    @DisplayName("Получение списка InputQueue отсортированного по title")
    @TmsLink("")
    @Test
    public void orderingInputQueueListByTitleTest() {
        createInputQueue();
        createInputQueue();
        createInputQueue();
        List<InputQueueResponse> inputQueueList = getOrderingByFieldInputQueueList("title");
        for (int i = 0; i < inputQueueList.size() - 1; i++) {
            InputQueueResponse currentQueue = inputQueueList.get(i);
            InputQueueResponse nextQueue = inputQueueList.get(i + 1);
            assertTrue(currentQueue.getTitle().compareTo(nextQueue.getTitle()) <= 0
                    , String.format("%s стоит выше чем %s", currentQueue.getTitle(), nextQueue.getTitle()));
        }
    }

    @DisplayName("Получение списка InputQueue отсортированного по name")
    @TmsLink("")
    @Test
    public void orderingInputQueueListByNameTest() {
        createInputQueue();
        createInputQueue();
        createInputQueue();
        List<InputQueueResponse> inputQueueList = getOrderingByFieldInputQueueList("name");
        for (int i = 0; i < inputQueueList.size() - 1; i++) {
            InputQueueResponse currentQueue = inputQueueList.get(i);
            InputQueueResponse nextQueue = inputQueueList.get(i + 1);
            assertTrue(currentQueue.getName().compareTo(nextQueue.getName()) < 0,
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }

    @DisplayName("Получение списка InputQueue отсортированного по create_dt")
    @TmsLink("")
    @Test
    public void orderingInputQueueListByCreateDtTest() {
        createInputQueue();
        createInputQueue();
        createInputQueue();
        List<InputQueueResponse> inputQueueList = getOrderingByFieldInputQueueList("create_dt");
        for (int i = 0; i < inputQueueList.size() - 1; i++) {
            InputQueueResponse currentQueue = inputQueueList.get(i);
            InputQueueResponse nextQueue = inputQueueList.get(i + 1);
            assertTrue(currentQueue.getCreate_dt().compareTo(nextQueue.getCreate_dt()) < 0,
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }

    @DisplayName("Получение списка InputQueue отсортированного по id")
    @TmsLink("")
    @Test
    public void orderingInputQueueListByIdTest() {
        createInputQueue();
        createInputQueue();
        createInputQueue();
        List<InputQueueResponse> inputQueueList = getOrderingByFieldInputQueueList("id");
        for (int i = 0; i < inputQueueList.size() - 1; i++) {
            InputQueueResponse currentQueue = inputQueueList.get(i);
            InputQueueResponse nextQueue = inputQueueList.get(i + 1);
            assertTrue(currentQueue.getId() < nextQueue.getId(),
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }
}
