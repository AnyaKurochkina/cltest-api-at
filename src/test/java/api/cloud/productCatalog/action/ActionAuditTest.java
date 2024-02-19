package api.cloud.productCatalog.action;

import core.enums.Role;
import core.helper.StringUtils;
import core.helper.http.QueryBuilder;
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
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.keyCloak.KeyCloakSteps.getUserInfo;
import static steps.productCatalog.ActionSteps.*;

@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionAuditTest extends ActionBaseTest {

    @DisplayName("Получение списка audit для определенного действия")
    @TmsLink("SOUL-8312")
    @Test
    public void getActionAuditListTest() {
        Action action = createAction(createActionModel("get_audit_list_test_api"));
        List<ProductAudit> objectAuditList = getActionAuditList(action.getId());
        assertEquals(1, objectAuditList.size());
        ProductAudit productAudit = objectAuditList.get(0);
        assertEquals(action.getId(), productAudit.getObjId());
    }

    @DisplayName("Получение деталей audit")
    @TmsLink("SOUL-8313")
    @Test
    public void getAuditDetailsTest() {
        Action action = createAction(createActionModel(StringUtils.getRandomStringApi(8)));
        List<ProductAudit> objectAuditList = getActionAuditList(action.getId());
        ProductAudit productAudit = objectAuditList.get(0);
        assertEquals(action.getId(), productAudit.getObjId());
        Response response = getActionAuditDetails(productAudit.getAuditId());
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
        partialUpdateAction(action.getId(), new JSONObject().put("name", updatedName));
        List<ProductAudit> objectAuditList = getActionAuditList(action.getId());
        Response response = getActionAuditDetails(objectAuditList.get(0).getAuditId());
        String newValueName = response.jsonPath().getString("new_value.name");
        String oldValueName = response.jsonPath().getString("old_value.name");
        assertEquals(action.getName(), oldValueName);
        assertEquals(updatedName, newValueName);
    }

    @DisplayName("Получение списка audit для obj_key")
    @TmsLink("SOUL-8314")
    @Test
    public void getAuditListWithObjKeyTest() {
        Action testAction = createAction(StringUtils.getRandomStringApi(6));
        deleteActionById(testAction.getId());
        createAction(testAction.toJson());
        List<ProductAudit> auditListForObjKeys = getAuditListByActionKeys(testAction.getName());
        auditListForObjKeys.forEach(x -> assertEquals(x.getObjKeys().get("name"), testAction.getName()));
        deleteActionByName(testAction.getName());
    }

    @DisplayName("Получение информации о пользователе в списке Аудита.")
    @TmsLink("SOUL-8457")
    @Test
    public void getAuditUserInfoWhoDidChangesTest() {
        Action action = createAction(createActionModel(StringUtils.getRandomStringApi(8)));
        List<ProductAudit> objectAuditList = getActionAuditList(action.getId());
        UserInfo userInfo = getUserInfo(Role.PRODUCT_CATALOG_ADMIN);
        ProductAudit productAudit = objectAuditList.get(0);
        assertAll(
                () -> assertEquals(userInfo.getEmail(), productAudit.getUserEmail()),
                () -> assertEquals(userInfo.getGivenName(), productAudit.getUserFirstName()),
                () -> assertEquals(userInfo.getFamilyName(), productAudit.getUserLastName()));
    }

    @DisplayName("Получение списка audit по фильтру user__icontains")
    @TmsLink("SOUL-8458")
    @Test
    public void getObjectAuditWithFilterByUserEmailTest() {
        String name = StringUtils.getRandomStringApi(7);
        Action action = createAction(StringUtils.getRandomStringApi(7));

        partialUpdateActionWithAnotherRole(action.getId(), new JSONObject().put("name", name), Role.CLOUD_ADMIN);
        List<ProductAudit> objectAuditList = getActionAuditList(action.getId());
        assertEquals(2, objectAuditList.size());

        UserInfo userProductCatalogAdmin = getUserInfo(Role.PRODUCT_CATALOG_ADMIN);
        UserInfo userCloudAdmin = getUserInfo(Role.CLOUD_ADMIN);

        List<ProductAudit> objectAuditListWithFilteredByFirstName = getActionAuditListWithQuery(action.getId(), new QueryBuilder().add("user__icontains", userProductCatalogAdmin.getGivenName()));
        assertEquals(1, objectAuditListWithFilteredByFirstName.size());
        ProductAudit productAudit = objectAuditListWithFilteredByFirstName.get(0);
        assertEquals(productAudit.getUserFirstName(), userProductCatalogAdmin.getGivenName());

        List<ProductAudit> objectAuditListWithFilteredByEmail = getActionAuditListWithQuery(action.getId(), new QueryBuilder().add("user__icontains", userCloudAdmin.getEmail()));
        assertEquals(1, objectAuditListWithFilteredByEmail.size());
        ProductAudit productAuditEmail = objectAuditListWithFilteredByEmail.get(0);
        assertEquals(productAuditEmail.getUserEmail(), userCloudAdmin.getEmail());

        List<ProductAudit> objectAuditListWithFilteredByLastName = getActionAuditListWithQuery(action.getId(), new QueryBuilder().add("user__icontains", userCloudAdmin.getFamilyName()));
        assertEquals(1, objectAuditListWithFilteredByLastName.size());
        ProductAudit productAuditLastName = objectAuditListWithFilteredByLastName.get(0);
        assertEquals(productAuditLastName.getUserLastName(), userCloudAdmin.getFamilyName());
    }
}
