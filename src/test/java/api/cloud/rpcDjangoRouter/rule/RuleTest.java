package api.cloud.rpcDjangoRouter.rule;

import api.Tests;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.rpcRouter.InputQueueResponse;
import models.cloud.rpcRouter.RuleResponse;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static models.Entity.serialize;
import static org.junit.jupiter.api.Assertions.*;
import static steps.rpcRouter.InputQueueSteps.createInputQueue;
import static steps.rpcRouter.RuleSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Rule")
@DisabledIfEnv("prod")
public class RuleTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Получение списка объектов использующих Rule")
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

    @DisplayName("API. RPC-Django-Router. Получение списка объектов используемых в Rule")
    @TmsLink("")
    @Test
    public void getObjectListUsingRuleTest() {
        RuleResponse rule = createRule();
        Response objectsUsedRule = getObjectsUsingRule(rule.getId());
        String s = objectsUsedRule.jsonPath().get().toString();
        assertEquals("{}", s);
    }

    @DisplayName("API. RPC-Django-Router. Создание Rule")
    @TmsLink("")
    @Test
    public void createRuleTest() {
        RuleResponse ruleResponse = createRule();
        RuleResponse createdRule = getRuleById(ruleResponse.getId());
        assertEquals(ruleResponse, createdRule);
    }

    @DisplayName("API. RPC-Django-Router. Удаление Rule")
    @TmsLink("")
    @Test
    public void deleteRuleTest() {
        RuleResponse ruleResponse = createRuleWithOutAutoDelete();
        deleteRuleById(ruleResponse.getId());
        assertFalse(isRuleExist(ruleResponse.getName()));
    }

    @SneakyThrows
    @DisplayName("API. RPC-Django-Router. Обновление Rule")
    @TmsLink("")
    @Test
    public void updateRuleTest() {
        RuleResponse ruleResponse = createRule();
        JSONObject serializedQueue = serialize(ruleResponse);
        serializedQueue.put("name", "update_name_rule_test_api");
        RuleResponse updatedRule = new ObjectMapper().readValue(String.valueOf(serializedQueue), RuleResponse.class);
        RuleResponse updatedRuleResponse = updateRule(ruleResponse.getId(), serializedQueue);
        assertEquals(updatedRule, updatedRuleResponse);
    }

    @DisplayName("API. RPC-Django-Router. Частичное обновление Rule")
    @TmsLink("")
    @Test
    public void partialUpdateRuleTest() {
        RuleResponse ruleResponse = createRule();
        String newName = "partial_update:test_api";
        partialUpdateRule(ruleResponse.getId(), new JSONObject().put("name", newName));
        assertEquals(newName, getRuleById(ruleResponse.getId()).getName());
    }

    @DisplayName("API. RPC-Django-Router. Получение списка Rule")
    @TmsLink("")
    @Test
    public void getRuleListTest() {
        RuleResponse ruleResponse = createRule();
        List<RuleResponse> ruleList = getRuleList();
        assertTrue(ruleList.contains(ruleResponse));
    }

    @DisplayName("API. RPC-Django-Router. Копирование Rule")
    @TmsLink("")
    @Test
    public void copyRuleTest() {
        RuleResponse rule = createRule();
        RuleResponse copyRule = copyRule(rule.getId());
        assertEquals(rule.getName() + "-clone", copyRule.getName());
    }
}
