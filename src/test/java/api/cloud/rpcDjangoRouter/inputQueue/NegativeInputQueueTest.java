package api.cloud.rpcDjangoRouter.inputQueue;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.rpcRouter.InputQueue;
import models.cloud.rpcRouter.RuleResponse;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.InputQueueSteps.createInputQueue;
import static steps.rpcRouter.RuleSteps.createRule;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("InputQueue")
@DisabledIfEnv("prod")
public class NegativeInputQueueTest extends Tests {

    @DisplayName("Создание InputQueue без ':' в имени")
    @TmsLink("")
    @Test
    public void createInputQueueWithOutColonInNameTest() {
        RuleResponse rule = createRule();
        String name = "create_input_queue_without_colon_in_name";
        JSONObject jsonObject = InputQueue.builder()
                .name(name)
                .rules(Collections.singletonList(rule.getId()))
                .build()
                .toJson();
        String errorMessage = createInputQueue(jsonObject).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Имя очереди (InputQueue - %s) должно содержать двоеточие", name), errorMessage);
    }
}
