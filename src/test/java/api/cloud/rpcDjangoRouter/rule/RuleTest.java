package api.cloud.rpcDjangoRouter.rule;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.InputQueueSteps.createInputQueue;
import static steps.rpcRouter.RuleSteps.getObjectsUsedRule;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Rule")
@DisabledIfEnv("prod")
public class RuleTest extends Tests {

    @DisplayName("Получение списка объектов использующих Rule")
    @TmsLink("")
    @Test
    public void getObjectListUsedRuleTest() {
        InputQueueResponse inputQueue = createInputQueue();
        List<InputQueueResponse> inputQueueList = getObjectsUsedRule(inputQueue.getRules().get(0)).jsonPath()
                .getList("InputQueue", InputQueueResponse.class);
        InputQueueResponse inputQueueResponse = inputQueueList.get(0);
        assertEquals(1, inputQueueList.size());
        assertEquals(inputQueue.getName(), inputQueueResponse.getName());
        assertEquals(inputQueue.getId(), inputQueueResponse.getId());
        assertEquals(inputQueue.getTitle(), inputQueueResponse.getTitle());

    }
}
