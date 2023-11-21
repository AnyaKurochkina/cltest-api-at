package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.ProductCatalogSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionAuditTest extends Tests {
    private final static String ENTITY_TYPE = "actions";

    @DisplayName("Получение списка audit для определенного действия")
    @TmsLink("SOUL-8312")
    @Test
    public void getActionAuditListTest() {
        Action action = createAction("get_audit_list_test_api");
        List<ProductAudit> objectAuditList = getObjectAuditList(ENTITY_TYPE, action.getActionId());
        assertEquals(1, objectAuditList.size());
        ProductAudit productAudit = objectAuditList.get(0);
        assertEquals(action.getActionId(), productAudit.getObjId());
    }

    @DisplayName("Получение деталей audit")
    @TmsLink("SOUL-8313")
    @Test
    public void getAuditDetailsTest() {
        Action action = createAction(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        List<ProductAudit> objectAuditList = getObjectAuditList(ENTITY_TYPE, action.getActionId());
        ProductAudit productAudit = objectAuditList.get(0);
        assertEquals(action.getActionId(), productAudit.getObjId());
        Response response = getObjectAudit(ENTITY_TYPE, productAudit.getAuditId());
        Action createdAction = response.jsonPath().getObject("new_value", Action.class);
        JSONObject old_value = response.jsonPath().getObject("old_value", JSONObject.class);
        assertEquals(action.getName(), createdAction.getName());
        assertTrue(old_value.isEmpty());
    }

    @DisplayName("Получение деталей audit для измененного действия")
    @TmsLink("SOUL-8315")
    @Test
    public void getAuditDetailsChangedActionTest() {
        Action action = createAction(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        String updatedName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        partialUpdateAction(action.getActionId(), new JSONObject().put("name", updatedName));
        List<ProductAudit> objectAuditList = getObjectAuditList(ENTITY_TYPE, action.getActionId());
        Response response = getObjectAudit(ENTITY_TYPE, objectAuditList.get(0).getAuditId());
        String newValueName = response.jsonPath().getString("new_value.name");
        String oldValueName = response.jsonPath().getString("old_value.name");
        assertEquals(action.getName(), oldValueName);
        assertEquals(updatedName, newValueName);
    }

    @DisplayName("Получение списка audit для obj_key")
    @TmsLink("SOUL-8314")
    @Test
    public void getAuditListWithObjKeyTest() {
        Action testAction = createAction();
        testAction.deleteObject();
        createAction(testAction.toJson());
        List<ProductAudit> auditListForObjKeys = getAuditListForObjKeys(ENTITY_TYPE, testAction.getName());
        auditListForObjKeys.forEach(x -> assertEquals(x.getObjKeys().get("name"), testAction.getName()));
        deleteActionByName(testAction.getName());
    }
}
