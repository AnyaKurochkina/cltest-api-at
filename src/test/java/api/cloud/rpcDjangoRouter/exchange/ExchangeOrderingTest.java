package api.cloud.rpcDjangoRouter.exchange;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.ExchangeResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;
import static steps.rpcRouter.ExchangeSteps.createExchange;
import static steps.rpcRouter.ExchangeSteps.getOrderingByFieldExchangeList;


@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Exchange")
@DisabledIfEnv("prod")
public class ExchangeOrderingTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Получение списка Exchange отсортированного по title")
    @TmsLink("")
    @Test
    public void orderingExchangeListByTitleTest() {
        createExchange();
        createExchange();
        createExchange();
        List<ExchangeResponse> exchangeList = getOrderingByFieldExchangeList("title");
        for (int i = 0; i < exchangeList.size() - 1; i++) {
            ExchangeResponse currentQueue = exchangeList.get(i);
            ExchangeResponse nextQueue = exchangeList.get(i + 1);
            assertTrue(delNoDigOrLet(currentQueue.getTitle()).compareTo(delNoDigOrLet(nextQueue.getTitle())) <= 0
                    ,String.format("%s стоит выше чем %s", currentQueue.getTitle(), nextQueue.getTitle()));
        }
    }

    @DisplayName("API. RPC-Django-Router. Получение списка Exchange отсортированного по name")
    @TmsLink("")
    @Test
    public void orderingExchangeListByNameTest() {
        createExchange();
        createExchange();
        createExchange();
        List<ExchangeResponse> exchangeList = getOrderingByFieldExchangeList("name");
        for (int i = 0; i < exchangeList.size() - 1; i++) {
            ExchangeResponse currentQueue = exchangeList.get(i);
            ExchangeResponse nextQueue = exchangeList.get(i + 1);
            assertTrue(delNoDigOrLet(currentQueue.getName()).compareTo(delNoDigOrLet(nextQueue.getName())) < 0,
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }

    @DisplayName("API. RPC-Django-Router. Получение списка Exchange отсортированного по create_dt")
    @TmsLink("")
    @Test
    public void orderingExchangeListByCreateDtTest() {
        createExchange();
        createExchange();
        createExchange();
        List<ExchangeResponse> exchangeList = getOrderingByFieldExchangeList("create_dt");
        for (int i = 0; i < exchangeList.size() - 1; i++) {
            ExchangeResponse currentQueue = exchangeList.get(i);
            ExchangeResponse nextQueue = exchangeList.get(i + 1);
            assertTrue(currentQueue.getCreate_dt().compareTo(nextQueue.getCreate_dt()) < 0,
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }

    @DisplayName("API. RPC-Django-Router. Получение списка Exchange отсортированного по id")
    @TmsLink("")
    @Test
    public void orderingExchangeListByIdTest() {
        createExchange();
        createExchange();
        createExchange();
        List<ExchangeResponse> exchangeList = getOrderingByFieldExchangeList("id");
        for (int i = 0; i < exchangeList.size() - 1; i++) {
            ExchangeResponse currentQueue = exchangeList.get(i);
            ExchangeResponse nextQueue = exchangeList.get(i + 1);
            assertTrue(currentQueue.getId() < nextQueue.getId(),
                    String.format("%s стоит выше чем %s", currentQueue.getName(), nextQueue.getName()));
        }
    }
}
