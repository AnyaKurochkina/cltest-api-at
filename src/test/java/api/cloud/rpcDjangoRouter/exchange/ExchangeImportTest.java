package api.cloud.rpcDjangoRouter.exchange;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.ExchangeResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.rpcRouter.ExchangeSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Exchange")
@DisabledIfEnv("prod")
public class ExchangeImportTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Импорт Exchange")
    @TmsLink("")
    @Test
    public void importExchangeTest() {
        ExchangeResponse exchange = createExchange();
        String filePath = Configure.RESOURCE_PATH + "/json/rpcDjangoRouter/importExchange.json";
        DataFileHelper.write(filePath, exportExchange(exchange.getId()).toString());
        deleteExchange(exchange.getId());
        importExchange(filePath);
        DataFileHelper.delete(filePath);
        assertTrue(isExchangeExist(exchange.getName()), "Exchange не существует");
        deleteExchange(getExchangeByName(exchange.getName()).getId());
    }
}
