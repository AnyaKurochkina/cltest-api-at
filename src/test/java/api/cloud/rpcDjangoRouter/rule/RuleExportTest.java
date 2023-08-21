package api.cloud.rpcDjangoRouter.rule;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.rpcRouter.RuleResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.rpcRouter.RuleSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Exchange")
@DisabledIfEnv("prod")
public class RuleExportTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Экспорт Rule")
    @TmsLink("")
    @Test
    public void exportRuleTest() {
        RuleResponse rule = createRule();
        Response exportResponse = exportRule(rule.getId());
        RuleResponse exportedRule = exportResponse.jsonPath().getObject("Rule", RuleResponse.class);
        assertEquals(rule, exportedRule);
    }

    @SneakyThrows
    @DisplayName("API. RPC-Django-Router. Экспорт нескольких Rule")
    @TmsLink("")
    @Test
    public void exportRulesTest() {
        RuleResponse rule = createRule();
        RuleResponse rule2 = createRule();
        RuleResponse rule3 = createRule();
        ExportEntity e = new ExportEntity(rule.getId());
        ExportEntity e2 = new ExportEntity(rule2.getId());
        ExportEntity e3 = new ExportEntity(rule3.getId());
        exportRuleById(new ExportData(Arrays.asList(e, e2, e3)).toJson());
    }
}
