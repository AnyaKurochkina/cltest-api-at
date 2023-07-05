package steps.rpcRouter;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.rpcRouter.Exchange;
import models.cloud.rpcRouter.GetOutPutQueueList;
import models.cloud.rpcRouter.OutputQueue;
import org.json.JSONObject;
import org.openqa.selenium.NotFoundException;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.RpcRouter;
import static steps.rpcRouter.ExchangeSteps.createExchange;

public class OutputQueueSteps extends Steps {

    private static final String outPutQueueV1 = "/api/v1/output_queues/";

    @Step("Удаление OutPutQueue")
    public static Response deleteOutPutQueue(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(outPutQueueV1 + "{}/", id)
                .assertStatus(204);
    }

    @Step("Создание OutPutQueue")
    public static Response createOutPutQueue(JSONObject jsonObject) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .post(outPutQueueV1)
                .assertStatus(201);
    }

    @Step("Создание OutPutQueue с именем {name}")
    public static OutputQueue createOutPutQueue(String name) {
        Exchange exchange = createExchange("exchange_for_output_queue_test_api");
        JSONObject queue = OutputQueue.builder()
                .name(name)
                .exchange(exchange.getId())
                .build()
                .toJson();
        return new Http(RpcRouter)
                .setRole(Role.CLOUD_ADMIN)
                .body(queue)
                .post(outPutQueueV1)
                .assertStatus(201)
                .extractAs(OutputQueue.class);
    }

    @Step("Получение списка OutPutQueue")
    public static List<OutputQueue> getOutPutQueueList() {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(outPutQueueV1)
                .assertStatus(201)
                .extractAs(GetOutPutQueueList.class)
                .getList();
    }

    @Step("Обновление OutPutQueue")
    public static OutputQueue updateOutPutQueue(Integer id, JSONObject jsonObject) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .put(outPutQueueV1 + "{}/", id)
                .assertStatus(201)
                .extractAs(OutputQueue.class);
    }

    @Step("Частичное обновление OutPutQueue")
    public static OutputQueue partialUpdateOutPutQueue(Integer id, JSONObject jsonObject) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .patch(outPutQueueV1 + "{}/", id)
                .assertStatus(201)
                .extractAs(OutputQueue.class);
    }

    @Step("Получение OutPutQueue по id {id}")
    public static OutputQueue getOutPutQueueById(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(outPutQueueV1 + "{}/", id)
                .assertStatus(201)
                .extractAs(OutputQueue.class);
    }

    @Step("Получение OutPutQueue по name {name}")
    public static OutputQueue getOutPutQueueByName(String name) {
        List<OutputQueue> list = new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(outPutQueueV1 + "?name={}", name)
                .assertStatus(201)
                .extractAs(GetOutPutQueueList.class)
                .getList();
        return list.stream().findFirst().orElseThrow(() -> new NotFoundException("Исходящая очередь не найдена"));
    }

    @Step("Проверка существования OutPutQueue по name {name}")
    public static boolean isOutPutQueueExist(String name) {
        List<OutputQueue> list = new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(outPutQueueV1 + "?name={}", name)
                .assertStatus(201)
                .extractAs(GetOutPutQueueList.class)
                .getList();
        return list.stream().findFirst().isPresent();
    }
}
