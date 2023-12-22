package steps.rpcRouter;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.AbstractEntity;
import models.cloud.productCatalog.ImportObject;
import models.cloud.rpcRouter.GetRulesList;
import models.cloud.rpcRouter.Rule;
import models.cloud.rpcRouter.RuleResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.openqa.selenium.NotFoundException;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.rpcRouter;

public class RuleSteps extends Steps {

    private static final String rulesV1 = "/api/v1/rules/";

    @Step("Удаление Rule по id {id}")
    public static Response deleteRuleById(Integer id) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(rulesV1 + "{}/", id);
    }

    @Step("Получение списка Rules отсортированного по {fieldName}")
    public static List<RuleResponse> getOrderingByFieldRulesList(String fieldName) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(rulesV1 + "?ordering={}", fieldName)
                .assertStatus(200)
                .extractAs(GetRulesList.class)
                .getList();
    }

    @Step("Создание Rule")
    public static RuleResponse createRule() {
        JSONObject json = Rule.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_rule_api_test")
                .build()
                .toJson();
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .post(rulesV1)
                .assertStatus(201)
                .extractAs(RuleResponse.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Создание Rule")
    public static RuleResponse createRuleWithOutAutoDelete() {
        JSONObject json = Rule.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_rule_api_test")
                .build()
                .toJson();
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .post(rulesV1)
                .assertStatus(201)
                .extractAs(RuleResponse.class);
    }

    @Step("Получение объектов использующих Rule по id {id}")
    public static Response getObjectsUsedRule(Integer id) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(rulesV1 + "{}/used/", id)
                .assertStatus(200);
    }

    @Step("Получение списка объектов используемых в c")
    public static Response getObjectsUsingRule(Integer id) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(rulesV1 + "{}/using_objects/", id)
                .assertStatus(200);
    }

    @Step("Получение RuleResponse по id {id}")
    public static RuleResponse getRuleById(Integer id) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(rulesV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(RuleResponse.class);
    }

    @Step("Обновление Rule")
    public static RuleResponse updateRule(Integer id, JSONObject jsonObject) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .put(rulesV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(RuleResponse.class);
    }

    @Step("Частичное обновление Rule")
    public static void partialUpdateRule(Integer id, JSONObject jsonObject) {
        new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .patch(rulesV1 + "{}/", id)
                .assertStatus(200);
    }

    @Step("Получение списка Rule")
    public static List<RuleResponse> getRuleList() {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(rulesV1)
                .assertStatus(200)
                .extractAs(GetRulesList.class)
                .getList();
    }

    @Step("Копирование Rule")
    public static RuleResponse copyRule(Integer id) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(rulesV1 + "{}/copy/", id)
                .assertStatus(200)
                .extractAs(RuleResponse.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Экспорт Rule")
    public static Response exportRule(Integer id) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(rulesV1 + "{}/obj_export/?as_file=true", id)
                .assertStatus(200);
    }

    @Step("Экспорт нескольких Rule по Id")
    public static Response exportRuleById(JSONObject json) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .post(rulesV1 + "objects_export/")
                .assertStatus(200);
    }

    @Step("Импорт Rule")
    public static ImportObject importRule(String pathName) {
        return new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(rulesV1 + "obj_import/", "file", new File(pathName))
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Проверка существования Rule по name {name}")
    public static boolean isRuleExist(String name) {
        List<RuleResponse> list = new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(rulesV1 + "?name__exact={}", name)
                .assertStatus(200)
                .extractAs(GetRulesList.class)
                .getList();
        return list.stream().findFirst().isPresent();
    }

    @Step("Получение Rule по name {name}")
    public static RuleResponse getRuleByName(String name) {
        List<RuleResponse> list = new Http(rpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(rulesV1 + "?name__exact={}", name)
                .assertStatus(200)
                .extractAs(GetRulesList.class)
                .getList();
        return list.stream().findFirst().orElseThrow(() -> new NotFoundException("Rule не найдено"));
    }
}
