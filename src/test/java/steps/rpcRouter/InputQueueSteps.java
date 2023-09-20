package steps.rpcRouter;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.AbstractEntity;
import models.cloud.productCatalog.ImportObject;
import models.cloud.rpcRouter.*;
import org.json.JSONObject;
import org.openqa.selenium.NotFoundException;
import steps.Steps;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.RpcRouter;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static steps.rpcRouter.RuleSteps.createRule;

public class InputQueueSteps extends Steps {

    private static final String inputQueueV1 = "/api/v1/input_queues/";

    @Step("Удаление InputQueue по id {id}")
    public static Response deleteInputQueue(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .delete(inputQueueV1 + "{}/", id);
    }

    @Step("Получение списка InputQueue отсортированного по {fieldName}")
    public static List<InputQueueResponse> getOrderingByFieldInputQueueList(String fieldName) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(inputQueueV1 + "?ordering={}", fieldName)
                .assertStatus(200)
                .extractAs(GetInputQueueList.class)
                .getList();
    }

    @Step("Создание OutPutQueue")
    public static Response createInputQueue(JSONObject jsonObject) {
        return new Http(RpcRouter)
                .withServiceToken()
                .body(jsonObject)
                .post(inputQueueV1);
    }

    @Step("Создание InputQueue")
    public static InputQueueResponse createInputQueue() {
        RuleResponse rule = createRule();
        JSONObject queue = InputQueue.builder()
                .name(randomAlphabetic(5).toLowerCase() + ":" + randomAlphabetic(6).toLowerCase() + "_input_queue_test_api")
                .rules(Collections.singletonList(rule.getId()))
                .build()
                .toJson();
        return new Http(RpcRouter)
                .withServiceToken()
                .body(queue)
                .post(inputQueueV1)
                .assertStatus(201)
                .extractAs(InputQueueResponse.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Экспорт InputQueue")
    public static Response exportInputQueue(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                //  .withServiceToken()
                .get(inputQueueV1 + "{}/obj_export/?as_file=true", id)
                .assertStatus(200);
    }

    @Step("Экспорт нескольких InputQueue по Id")
    public static Response exportInputQueueById(JSONObject json) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .post(inputQueueV1 + "objects_export/")
                .assertStatus(200);
    }

    @Step("Импорт InputQueue")
    public static ImportObject importInputQueue(String pathName) {
        return new Http(RpcRouter)
                .withServiceToken()
                .multiPart(inputQueueV1 + "obj_import/", "file", new File(pathName))
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Получение списка объектов использующих InputQueue")
    public static Response getObjectsUsedInputQueue(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(inputQueueV1 + "{}/used/", id)
                .assertStatus(200);
    }

    @Step("Получение списка объектов используемых в InputQueue")
    public static Response getObjectsUsingInputQueue(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(inputQueueV1 + "{}/using_objects/", id)
                .assertStatus(200);
    }

    @Step("Получение InputQueue по id {id}")
    public static InputQueueResponse getInputQueueById(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(inputQueueV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(InputQueueResponse.class);
    }

    @Step("Получение InputQueue по name {name}")
    public static InputQueueResponse getInputQueueByName(String name) {
        List<InputQueueResponse> list = new Http(RpcRouter)
                .withServiceToken()
                .get(inputQueueV1 + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetInputQueueList.class)
                .getList();
        return list.stream().findFirst().orElseThrow(() -> new NotFoundException("Исходящая очередь не найдена"));
    }

    @Step("Проверка существования InputQueue по name {name}")
    public static boolean isInputQueueExist(String name) {
        List<InputQueueResponse> list = new Http(RpcRouter)
                .withServiceToken()
                .get(inputQueueV1 + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetInputQueueList.class)
                .getList();
        return list.stream().findFirst().isPresent();
    }

    @Step("Обновление InputQueue")
    public static InputQueueResponse updateInputQueue(Integer id, JSONObject jsonObject) {
        return new Http(RpcRouter)
                .withServiceToken()
                .body(jsonObject)
                .put(inputQueueV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(InputQueueResponse.class);
    }

    @Step("Частичное обновление InputQueue")
    public static void partialUpdateInputQueue(Integer id, JSONObject jsonObject) {
        new Http(RpcRouter)
                .withServiceToken()
                .body(jsonObject)
                .patch(inputQueueV1 + "{}/", id)
                .assertStatus(200);
    }

    @Step("Получение списка InputQueue")
    public static List<InputQueueResponse> getInputQueueList() {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(inputQueueV1)
                .assertStatus(200)
                .extractAs(GetInputQueueList.class)
                .getList();
    }

    @Step("Копирование OutPutQueue")
    public static InputQueueResponse copyInputQueue(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .post(inputQueueV1 + "{}/copy/", id)
                .assertStatus(200)
                .extractAs(InputQueueResponse.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }
}
