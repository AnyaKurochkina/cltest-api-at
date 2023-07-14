package steps.rpcRouter;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.rpcRouter.ExchangeResponse;
import models.cloud.rpcRouter.GetOutPutQueueList;
import models.cloud.rpcRouter.OutputQueue;
import models.cloud.rpcRouter.OutputQueueResponse;
import org.json.JSONObject;
import org.openqa.selenium.NotFoundException;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.RpcRouter;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static steps.rpcRouter.ExchangeSteps.createExchange;

public class OutputQueueSteps extends Steps {

    private static final String outPutQueueV1 = "/api/v1/output_queues/";

    @Step("Удаление OutPutQueue")
    public static Response deleteOutPutQueue(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .delete(outPutQueueV1 + "{}/", id)
                .assertStatus(204);
    }

    @Step("Создание OutPutQueue")
    public static Response createOutPutQueue(JSONObject jsonObject) {
        return new Http(RpcRouter)
                .withServiceToken()
                .body(jsonObject)
                .post(outPutQueueV1)
                .assertStatus(201);
    }

    @Step("Экспорт OutPutQueue")
    public static Response exportOutPutQueue(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
              //  .withServiceToken()
                .get(outPutQueueV1 + "{}/obj_export/?as_file=true", id)
                .assertStatus(200);
    }

    @Step("Создание OutPutQueue с именем {name}")
    public static OutputQueueResponse createOutPutQueue() {
        ExchangeResponse exchange = createExchange();
        JSONObject queue = OutputQueue.builder()
                .name(randomAlphabetic(5).toLowerCase() + ":" + randomAlphabetic(6).toLowerCase() + "_output_queue_test_api")
                .exchange(exchange.getId())
                .build()
                .toJson();
        return new Http(RpcRouter)
                .withServiceToken()
                .body(queue)
                .post(outPutQueueV1)
                .assertStatus(201)
                .extractAs(OutputQueueResponse.class);
    }

    @Step("Получение списка OutPutQueue")
    public static List<OutputQueueResponse> getOutPutQueueList() {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(outPutQueueV1)
                .assertStatus(200)
                .extractAs(GetOutPutQueueList.class)
                .getList();
    }

    @Step("Обновление OutPutQueue")
    public static OutputQueueResponse updateOutPutQueue(Integer id, JSONObject jsonObject) {
        return new Http(RpcRouter)
                .withServiceToken()
                .body(jsonObject)
                .put(outPutQueueV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(OutputQueueResponse.class);

    }

    @Step("Частичное обновление OutPutQueue")
    public static void partialUpdateOutPutQueue(Integer id, JSONObject jsonObject) {
        new Http(RpcRouter)
                .withServiceToken()
                .body(jsonObject)
                .patch(outPutQueueV1 + "{}/", id)
                .assertStatus(200);

    }

    @Step("Копирование OutPutQueue")
    public static OutputQueueResponse copyOutPutQueue(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .post(outPutQueueV1 + "{}/copy/", id)
                .assertStatus(200)
                .extractAs(OutputQueueResponse.class);
    }

    @Step("Получение OutPutQueue по id {id}")
    public static OutputQueueResponse getOutPutQueueById(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(outPutQueueV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(OutputQueueResponse.class);
    }

    @Step("Получение OutPutQueue по name {name}")
    public static OutputQueueResponse getOutPutQueueByName(String name) {
        List<OutputQueueResponse> list = new Http(RpcRouter)
                .withServiceToken()
                .get(outPutQueueV1 + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetOutPutQueueList.class)
                .getList();
        return list.stream().findFirst().orElseThrow(() -> new NotFoundException("Исходящая очередь не найдена"));
    }

    @Step("Проверка существования OutPutQueue по name {name}")
    public static boolean isOutPutQueueExist(String name) {
        List<OutputQueueResponse> list = new Http(RpcRouter)
                .withServiceToken()
                .get(outPutQueueV1 + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetOutPutQueueList.class)
                .getList();
        return list.stream().findFirst().isPresent();
    }
}
