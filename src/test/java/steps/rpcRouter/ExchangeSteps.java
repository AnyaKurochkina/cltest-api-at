package steps.rpcRouter;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.rpcRouter.*;
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
        return new Http(RpcRouter)
                .withServiceToken()
                .delete(exchangeV1 + "{}/", id);
    }

    @Step("Создание Exchange")
    public static ExchangeResponse createExchange() {
        JSONObject exchange = Exchange.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_api_test")
                .build()
                .toJson();
        return new Http(RpcRouter)
                .withServiceToken()
                .body(exchange)
                .post(exchangeV1)
                .assertStatus(201)
                .extractAs(ExchangeResponse.class, true);
    }

    @Step("Создание Exchange")
    public static ExchangeResponse createExchangeWithOutAutoDelete() {
        JSONObject exchange = Exchange.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_exchange_api_test")
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
        List<ExchangeResponse> list = new Http(RpcRouter)
                .withServiceToken()
                .get(exchangeV1 + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
        return list.stream().findFirst().isPresent();
    }

    @Step("Получение Exchange по name {name}")
    public static Optional<ExchangeResponse> getExchangeByName(String name) {
        List<ExchangeResponse> list = new Http(RpcRouter)
                .withServiceToken()
                .get(exchangeV1 + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
        return list.stream().findFirst();
    }

    @Step("Получение объектов использующих Exchange по id {id}")
    public static Response getObjectsUsedExchange(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(exchangeV1 + "{}/used/", id)
                .assertStatus(200);
    }

    @Step("Получение списка Exchange отсортированного по {fieldName}")
    public static List<ExchangeResponse> getOrderingByFieldExchangeList(String fieldName) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(exchangeV1 + "?ordering={}", fieldName)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
    }

    @Step("Получение Exchange по id {id}")
    public static ExchangeResponse getExchangeById(Integer id) {
        return new Http(RpcRouter)
                .withServiceToken()
                .get(exchangeV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(ExchangeResponse.class);
    }
}
