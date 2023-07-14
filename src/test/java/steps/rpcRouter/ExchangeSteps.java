package steps.rpcRouter;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.rpcRouter.Exchange;
import models.cloud.rpcRouter.ExchangeResponse;
import models.cloud.rpcRouter.GetExchangeList;
import models.cloud.rpcRouter.OutputQueue;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;
import java.util.Optional;

import static core.helper.Configure.RpcRouter;

public class ExchangeSteps extends Steps {

    private static final String exchangeV1 = "/api/v1/exchanges/";

    @Step("Удаление Exchange по id {id}")
    public static Response deleteExchange(Integer id) {
//        List<OutputQueue> usedList = getUsedExchangeObjectsList(id);
//        if (!usedList.isEmpty()) {
//            usedList.forEach(outputQueue -> deleteOutPutQueue(outputQueue.getId()));
//        }
        return new Http(RpcRouter)
                .withServiceToken()
                .delete(exchangeV1 + "{}/", id)
                .assertStatus(204);
    }

    @Step("Создание Exchange c name {name}")
    public static ExchangeResponse createExchange() {
//        Optional<Exchange> exchangeOpt = getExchangeByName(name);
//        exchangeOpt.ifPresent(exchange -> deleteExchange(exchange.getId()));
        JSONObject exchange = Exchange.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "api_test")
                .build()
                .toJson();
        return new Http(RpcRouter)
                .withServiceToken()
                .body(exchange)
                .post(exchangeV1)
                .assertStatus(201)
                .extractAs(ExchangeResponse.class);
    }

    @Step("Проверка существования Exchange по name {name}")
    public static boolean isExchangeExist(String name) {
        List<Exchange> list = new Http(RpcRouter)
                .withServiceToken()
                .get(exchangeV1 + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
        return list.stream().findFirst().isPresent();
    }

    @Step("Получение Exchange по name {name}")
    public static Optional<Exchange> getExchangeByName(String name) {
        List<Exchange> list = new Http(RpcRouter)
                .withServiceToken()
                .get(exchangeV1 + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
        return list.stream().findFirst();
    }

    @Step("Получение объектов использующих Exchange по id {id}")
    public static List<OutputQueue> getUsedExchangeObjectsList(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(exchangeV1 + "{}/used/", id)
                .assertStatus(200)
                .jsonPath()
                .getList("OutputQueue", OutputQueue.class);
    }
}
