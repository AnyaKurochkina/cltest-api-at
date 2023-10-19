package steps.rpcRouter;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.AbstractEntity;
import models.cloud.productCatalog.ImportObject;
import models.cloud.rpcRouter.Exchange;
import models.cloud.rpcRouter.ExchangeResponse;
import models.cloud.rpcRouter.GetExchangeList;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.openqa.selenium.NotFoundException;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.RpcRouter;

public class ExchangeSteps extends Steps {

    private static final String exchangeV1 = "/api/v1/exchanges/";

    @Step("Удаление Exchange по id {id}")
    public static Response deleteExchange(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(exchangeV1 + "{}/", id);
    }

    @Step("Создание Exchange")
    public static ExchangeResponse createExchange() {
        JSONObject exchange = Exchange.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_exchange_api_test")
                .build()
                .toJson();
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(exchange)
                .post(exchangeV1)
                .assertStatus(201)
                .extractAs(ExchangeResponse.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Создание Exchange")
    public static ExchangeResponse createExchangeWithOutAutoDelete() {
        JSONObject exchange = Exchange.builder()
                .name(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_exchange_api_test")
                .build()
                .toJson();
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(exchange)
                .post(exchangeV1)
                .assertStatus(201)
                .extractAs(ExchangeResponse.class);
    }

    @Step("Проверка существования Exchange по name {name}")
    public static boolean isExchangeExist(String name) {
        List<ExchangeResponse> list = new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(exchangeV1 + "?name__exact={}", name)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
        return list.stream().findFirst().isPresent();
    }

    @Step("Получение Exchange по name {name}")
    public static ExchangeResponse getExchangeByName(String name) {
        List<ExchangeResponse> list = new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(exchangeV1 + "?name__exact={}", name)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
        return list.stream().findFirst().orElseThrow(() -> new NotFoundException("Exchange не найден"));
    }

    @Step("Получение объектов использующих Exchange по id {id}")
    public static Response getObjectsUsedExchange(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(exchangeV1 + "{}/used/", id)
                .assertStatus(200);
    }

    @Step("Получение списка объектов используемых в Exchange")
    public static Response getObjectsUsingExchange(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(exchangeV1 + "{}/using_objects/", id)
                .assertStatus(200);
    }

    @Step("Получение списка Exchange отсортированного по {fieldName}")
    public static List<ExchangeResponse> getOrderingByFieldExchangeList(String fieldName) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(exchangeV1 + "?ordering={}", fieldName)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
    }

    @Step("Получение Exchange по id {id}")
    public static ExchangeResponse getExchangeById(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(exchangeV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(ExchangeResponse.class);
    }

    @Step("Обновление Exchange")
    public static ExchangeResponse updateExchange(Integer id, JSONObject jsonObject) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .put(exchangeV1 + "{}/", id)
                .assertStatus(200)
                .extractAs(ExchangeResponse.class);
    }

    @Step("Частичное обновление Exchange")
    public static void partialUpdateExchange(Integer id, JSONObject jsonObject) {
        new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .patch(exchangeV1 + "{}/", id)
                .assertStatus(200);
    }

    @Step("Получение списка Exchange")
    public static List<ExchangeResponse> getExchangeList() {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(exchangeV1)
                .assertStatus(200)
                .extractAs(GetExchangeList.class)
                .getList();
    }

    @Step("Копирование Exchange")
    public static ExchangeResponse copyExchange(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(exchangeV1 + "{}/copy/", id)
                .assertStatus(200)
                .extractAs(ExchangeResponse.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Экспорт Exchange")
    public static Response exportExchange(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(exchangeV1 + "{}/obj_export/?as_file=true", id)
                .assertStatus(200);
    }

    @Step("Экспорт нескольких Exchange по Id")
    public static Response exportExchangeById(JSONObject json) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .post(exchangeV1 + "objects_export/")
                .assertStatus(200);
    }

    @Step("Импорт Exchange")
    public static ImportObject importExchange(String pathName) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(exchangeV1 + "obj_import/", "file", new File(pathName))
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }
}
