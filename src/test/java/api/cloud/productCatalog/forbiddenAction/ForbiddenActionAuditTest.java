package api.cloud.productCatalog.forbiddenAction;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.helper.StringUtils.getRandomStringApi;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ForbiddenActionSteps.createForbiddenAction;
import static steps.productCatalog.ForbiddenActionSteps.deleteForbiddenActionByName;
import static steps.productCatalog.ProductCatalogSteps.getAuditListByObjKeys;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionAuditTest extends Tests {
    private final static String ENTITY_TYPE = "forbidden_actions";

    @DisplayName("Получение списка audit для obj_key forbidden_actions")
    @TmsLink("SOUL-8456")
    @Test
    public void getAuditListWithObjKeyForbiddenActionTest() {
        ForbiddenAction testAction = createForbiddenAction(getRandomStringApi(6));
        testAction.deleteObject();
        createForbiddenAction(testAction.toJson());
        List<ProductAudit> auditListForObjKeys = getAuditListByObjKeys(ENTITY_TYPE, testAction.getName());
        auditListForObjKeys.forEach(x -> assertEquals(x.getObjKeys().get("name"), testAction.getName()));
        deleteForbiddenActionByName(testAction.getName());
    }
}
