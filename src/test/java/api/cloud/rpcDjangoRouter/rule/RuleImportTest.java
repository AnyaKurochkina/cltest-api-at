package api.cloud.rpcDjangoRouter.rule;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.rpcRouter.RuleResponse;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.rpcRouter.RuleSteps.*;

@Tag("rpc-django-router")
@Epic("RPC Django Router")
@Feature("Exchange")
@DisabledIfEnv("prod")
public class RuleImportTest extends Tests {

    @DisplayName("API. RPC-Django-Router. Импорт Rule")
    @TmsLink("")
    @Test
    public void importRuleTest() {
        RuleResponse rule = createRule();
        String filePath = Configure.RESOURCE_PATH + "/json/rpcDjangoRouter/importRule.json";
        DataFileHelper.write(filePath, exportRule(rule.getId()).toString());
        deleteRuleById(rule.getId());
        importRule(filePath);
        DataFileHelper.delete(filePath);
        assertTrue(isRuleExist(rule.getName()), "Rule не существует");
        deleteRuleById(getRuleByName(rule.getName()).getId());
    }
}
