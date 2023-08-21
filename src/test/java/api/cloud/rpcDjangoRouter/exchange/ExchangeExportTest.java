package api.cloud.rpcDjangoRouter.exchange;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.rpcRouter.ExchangeResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.ExchangeSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Exchange")
@DisabledIfEnv("prod")
public class ExchangeExportTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Экспорт Exchange")
    @TmsLink("")
    @Test
    public void exportExchangeTest() {
        ExchangeResponse exchange = createExchange();
        Response exportResponse = exportExchange(exchange.getId());
        ExchangeResponse exportedExchange = exportResponse.jsonPath().getObject("Exchange", ExchangeResponse.class);
        assertEquals(exchange, exportedExchange);
    }

    @SneakyThrows
    @DisplayName("API. RPC-Django-Router. Экспорт нескольких Exchange")
    @TmsLink("")
    @Test
    public void exportExchangesTest() {
        ExchangeResponse exchange = createExchange();
        ExchangeResponse exchange2 = createExchange();
        ExchangeResponse exchange3 = createExchange();
        ExportEntity e = new ExportEntity(exchange.getId());
        ExportEntity e2 = new ExportEntity(exchange2.getId());
        ExportEntity e3 = new ExportEntity(exchange3.getId());
        exportExchangeById(new ExportData(Arrays.asList(e, e2, e3)).toJson());
    }
}
