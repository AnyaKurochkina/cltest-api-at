package steps.rpcRouter;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.rpcRouter.Exchange;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.RpcRouter;

public class ExchangeSteps extends Steps {

    private static final String exchangeV1 = "/api/v1/exchanges/";

    @Step("Удаление Exchange")
    public static Response deleteExchange(Integer id) {
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(exchangeV1 + "{}/", id)
                .assertStatus(204);
    }

    @Step("Создание Exchange")
    public static Exchange createExchange(String name) {
        JSONObject exchange = Exchange.builder()
                .name(name)
                .build()
                .toJson();
        return new Http(RpcRouter)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(exchange)
                .post(exchangeV1)
                .assertStatus(201)
                .extractAs(Exchange.class);
    }
}
