package steps.rpcRouter;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.rpcRouter.GetRulesList;
import models.cloud.rpcRouter.Rule;
import models.cloud.rpcRouter.RuleResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.RpcRouter;

public class RuleSteps extends Steps {

    private static final String rulesV1 = "/api/v1/rules/";

    @Step("Удаление Rule по id {id}")
    public static Response deleteRuleById(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .delete(rulesV1 + "{}/", id);
    }

    @Step("Получение списка OutPutQueue отсортированного по {fieldName}")
    public static List<RuleResponse> getOrderingByFieldRulesList(String fieldName) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(rulesV1 + "?ordering={}", fieldName)
                .assertStatus(200)
                .extractAs(GetRulesList.class)
                .getList();
    }

    @Step("Создание Rule")
    public static RuleResponse createRule() {
        JSONObject exchange = Rule.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_rule_api_test")
                .build()
                .toJson();
        return new Http(RpcRouter)
                .withServiceToken()
                .body(exchange)
                .post(rulesV1)
                .assertStatus(201)
                .extractAs(RuleResponse.class, true);
    }

    @Step("Создание Rule")
    public static RuleResponse createRuleWithOutAutoDelete() {
        JSONObject exchange = Rule.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_rule_api_test")
                .build()
                .toJson();
        return new Http(RpcRouter)
                .withServiceToken()
                .body(exchange)
                .post(rulesV1)
                .assertStatus(201)
                .extractAs(RuleResponse.class);
    }

    @Step("Получение объектов использующих Rule по id {id}")
    public static Response getObjectsUsedRule(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(rulesV1 + "{}/used/", id)
                .assertStatus(200);
    }

    @Step("Получение RuleResponse по id {id}")
    public static RuleResponse getRuleById(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(rulesV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(RuleResponse.class);
    }
}
