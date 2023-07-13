package api.cloud.rpcDjangoRouter.rule;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.RuleResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;
import static steps.rpcRouter.RuleSteps.createRule;
import static steps.rpcRouter.RuleSteps.getOrderingByFieldRulesList;


@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Rules")
@DisabledIfEnv("prod")
public class RulesOrderingTest extends Tests {

    @DisplayName("Получение списка Rule отсортированного по title")
    @TmsLink("")
    @Test
    public void orderingRuleListByTitleTest() {
        createRule();
        createRule();
        createRule();
        List<RuleResponse> exchangeList = getOrderingByFieldRulesList("title");
        for (int i = 0; i < exchangeList.size() - 1; i++) {
            RuleResponse currentQueue = exchangeList.get(i);
            RuleResponse nextQueue = exchangeList.get(i + 1);
            assertTrue(delNoDigOrLet(currentQueue.getTitle()).compareTo(delNoDigOrLet(nextQueue.getTitle())) <= 0
                    ,String.format("%s стоит выше чем %s", currentQueue.getTitle(), nextQueue.getTitle()));
        }
    }

    @DisplayName("Получение списка Rule отсортированного по name")
    @TmsLink("")
    @Test
    public void orderingRuleListByNameTest() {
        createRule();
        createRule();
        createRule();
        List<RuleResponse> exchangeList = getOrderingByFieldRulesList("name");
        for (int i = 0; i < exchangeList.size() - 1; i++) {
            RuleResponse currentQueue = exchangeList.get(i);
            RuleResponse nextQueue = exchangeList.get(i + 1);
            assertTrue(delNoDigOrLet(currentQueue.getName()).compareTo(delNoDigOrLet(nextQueue.getName())) < 0,
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }

    @DisplayName("Получение списка Rule отсортированного по create_dt")
    @TmsLink("")
    @Test
    public void orderingRuleListByCreateDtTest() {
        createRule();
        createRule();
        createRule();
        List<RuleResponse> exchangeList = getOrderingByFieldRulesList("create_dt");
        for (int i = 0; i < exchangeList.size() - 1; i++) {
            RuleResponse currentQueue = exchangeList.get(i);
            RuleResponse nextQueue = exchangeList.get(i + 1);
            assertTrue(currentQueue.getCreate_dt().compareTo(nextQueue.getCreate_dt()) < 0,
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }

    @DisplayName("Получение списка Rule отсортированного по id")
    @TmsLink("")
    @Test
    public void orderingExchangeListByIdTest() {
        createRule();
        createRule();
        createRule();
        List<RuleResponse> exchangeList = getOrderingByFieldRulesList("id");
        for (int i = 0; i < exchangeList.size() - 1; i++) {
            RuleResponse currentQueue = exchangeList.get(i);
            RuleResponse nextQueue = exchangeList.get(i + 1);
            assertTrue(currentQueue.getId() < nextQueue.getId(),
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }
}
