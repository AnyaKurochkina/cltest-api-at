package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ProductCatalogSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionAuditTest extends Tests {
    private final static String ENTITY_TYPE = "actions";

    @DisplayName("Получение списка audit для определенного действия")
    @TmsLink("")
    @Test
    public void getActionAuditListTest() {
        Action action = createAction("get_audit_list_test_api");
        List<ProductAudit> objectAuditList = getObjectAuditList(ENTITY_TYPE, action.getActionId());
        assertEquals(1, objectAuditList.size());
        ProductAudit productAudit = objectAuditList.get(0);
        assertEquals(action.getActionId(), productAudit.getObjId());
    }

    @DisplayName("Получение списка audit с уникальными obj_keys для всех actions")
    @TmsLink("")
    @Test
    public void getAuditListWithUniqueObjKeysTest() {
        Action action = createAction(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        List<ProductAudit> objectAuditList = getObjectAuditList(ENTITY_TYPE, action.getActionId());
        ProductAudit productAudit = objectAuditList.get(0);
        assertEquals(action.getActionId(), productAudit.getObjId());
        Response response = getObjectAudit(ENTITY_TYPE, productAudit.getAuditId());
        Action createdAction = response.jsonPath().getObject("new_value", Action.class);
        Action oldAction = response.jsonPath().getObject("old_value", Action.class);
        assertEquals(action, createdAction);
        assertNull(oldAction);
    }
}
