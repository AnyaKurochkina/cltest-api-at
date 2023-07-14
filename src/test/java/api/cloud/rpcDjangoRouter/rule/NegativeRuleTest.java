package api.cloud.rpcDjangoRouter.rule;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.rpcRouter.InputQueue;
import models.cloud.rpcRouter.InputQueueResponse;
import models.cloud.rpcRouter.RuleResponse;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.InputQueueSteps.createInputQueue;
import static steps.rpcRouter.InputQueueSteps.deleteInputQueue;
import static steps.rpcRouter.RuleSteps.createRuleWithOutAutoDelete;
import static steps.rpcRouter.RuleSteps.deleteRuleById;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Rule")
@DisabledIfEnv("prod")
public class NegativeRuleTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Удаление Rule используемого в InputQueue")
    @TmsLink("")
    @Test
    public void deleteRuleUsedInOutputQueueTest() {
        String name = "create_input_queue:to_test_api";
        RuleResponse rule = createRuleWithOutAutoDelete();
        JSONObject jsonObject = InputQueue.builder()
                .name(name)
                .rules(Collections.singletonList(rule.getId()))
                .build()
                .toJson();
        InputQueueResponse inputQueue = createInputQueue(jsonObject)
                .assertStatus(201)
                .extractAs(InputQueueResponse.class);
        String errorMessage = deleteRuleById(rule.getId()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Нельзя удалить экземпляр (%s), он используется ({'InputQueue': [{'name': '%s', 'id': %d, 'title': '%s'}]})",
                rule.getName(), inputQueue.getName(), inputQueue.getId(), inputQueue.getTitle()), errorMessage);
        deleteInputQueue(inputQueue.getId()).assertStatus(204);
        deleteRuleById(rule.getId()).assertStatus(204);
    }
}
