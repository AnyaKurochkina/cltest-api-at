package api.cloud.productCatalog.allowedAction;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.helper.StringUtils.getRandomStringApi;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.AllowedActionSteps.createAllowedAction;
import static steps.productCatalog.AllowedActionSteps.deleteAllowedActionByName;
import static steps.productCatalog.ProductCatalogSteps.getAuditListByObjKeys;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Разрешенные Действия")
@DisabledIfEnv("prod")
public class AllowedActionAuditTest extends Tests {
    private final static String ENTITY_TYPE = "allowed_actions";

    @DisplayName("Получение списка audit для obj_key allowed_action")
    @TmsLink("SOUL-8455")
    @Test
    public void getAuditListWithObjKeyAllowedActionTest() {
        AllowedAction testAction = createAllowedAction(AllowedAction.builder()
                .title(getRandomStringApi(6))
                .description("AT_" + randomAlphanumeric(10))
                .build().toJson()).extractAs(AllowedAction.class);
        testAction.delete();
        createAllowedAction(testAction.toJson());
        List<ProductAudit> auditListForObjKeys = getAuditListByObjKeys(ENTITY_TYPE, testAction.getName());
        auditListForObjKeys.forEach(x -> assertEquals(x.getObjKeys().get("name"), testAction.getName()));
        deleteAllowedActionByName(testAction.getName());
    }
}
