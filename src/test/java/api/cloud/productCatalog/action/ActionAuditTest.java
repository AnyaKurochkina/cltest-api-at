package api.cloud.productCatalog.action;

import api.Tests;
import core.enums.Role;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.keyCloak.UserInfo;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.keyCloak.KeyCloakSteps.getUserInfo;
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
        List<ProductAudit> auditListForObjKeys = getAuditListByObjKeys(ENTITY_TYPE, testAction.getName());
        auditListForObjKeys.forEach(x -> assertEquals(x.getObjKeys().get("name"), testAction.getName()));
        deleteActionByName(testAction.getName());
    }

    @DisplayName("Получение информации о пользователе в списке Аудита.")
    @TmsLink("SOUL-8457")
    @Test
    public void getAuditUserInfoWhoDidChangesTest() {
        Action action = createAction();
        List<ProductAudit> objectAuditList = getObjectAuditList(ENTITY_TYPE, action.getActionId());
        UserInfo userInfo = getUserInfo(Role.PRODUCT_CATALOG_ADMIN);
        ProductAudit productAudit = objectAuditList.get(0);
        assertEquals(userInfo.getEmail(), productAudit.getUserEmail());
        assertEquals(userInfo.getGivenName(), productAudit.getUserFirstName());
        assertEquals(userInfo.getFamilyName(), productAudit.getUserLastName());
    }

    @DisplayName("Получение списка audit по фильтру user__icontains")
    @TmsLink("SOUL-8458")
    @Test
    public void getObjectAuditWithFilterByUserEmailTest() {
        String name = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        Action action = createAction();

        partialUpdateActionWithAnotherRole(action.getActionId(), new JSONObject().put("name", name), Role.CLOUD_ADMIN);
        List<ProductAudit> objectAuditList = getObjectAuditList(ENTITY_TYPE, action.getActionId());
        assertEquals(2, objectAuditList.size());

        UserInfo userProductCatalogAdmin = getUserInfo(Role.PRODUCT_CATALOG_ADMIN);
        UserInfo userCloudAdmin = getUserInfo(Role.CLOUD_ADMIN);

        List<ProductAudit> objectAuditListWithFilteredByFirstName = getObjectAuditListWithFilter(ENTITY_TYPE, action.getActionId(), format("user__icontains={}", userProductCatalogAdmin.getGivenName()));
        assertEquals(1, objectAuditListWithFilteredByFirstName.size());
        ProductAudit productAudit = objectAuditListWithFilteredByFirstName.get(0);
        assertEquals(productAudit.getUserFirstName(), userProductCatalogAdmin.getGivenName());

        List<ProductAudit> objectAuditListWithFilteredByEmail = getObjectAuditListWithFilter(ENTITY_TYPE, action.getActionId(), format("user__icontains={}", userCloudAdmin.getEmail()));
        assertEquals(1, objectAuditListWithFilteredByEmail.size());
        ProductAudit productAuditEmail = objectAuditListWithFilteredByEmail.get(0);
        assertEquals(productAuditEmail.getUserEmail(), userCloudAdmin.getEmail());

        List<ProductAudit> objectAuditListWithFilteredByLastName = getObjectAuditListWithFilter(ENTITY_TYPE, action.getActionId(), format("user__icontains={}", userCloudAdmin.getFamilyName()));
        assertEquals(1, objectAuditListWithFilteredByLastName.size());
        ProductAudit productAuditLastName = objectAuditListWithFilteredByLastName.get(0);
        assertEquals(productAuditLastName.getUserLastName(), userCloudAdmin.getFamilyName());
    }
}
