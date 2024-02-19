package api.cloud.productCatalog.action;

import core.enums.Role;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.action.Action;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static core.enums.Role.PRODUCT_CATALOG_VIEWER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.ActionSteps.*;
import static tests.routes.ActionProductCatalogApi.apiV1ActionsRead;

@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionRestrictedAndAllowedGroupsTest extends ActionBaseTest {

    @DisplayName("Создание действия с ограничением restricted group на уровне realm")
    @TmsLink("1274379")
    @Test
    public void actionRestrictedGroupRealmLevelTest() {
        Action actionModel = createActionModel("action_for_restricted_group_api_test");
        actionModel.setRestrictedGroups(Collections.singletonList("qa-admin"));
        Action action = createAction(actionModel);

        Action actionById = getActionById(action.getId());
        assertNotNull(actionById);
        AssertResponse.run(() -> getObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsRead, action.getId()))
                .status(404)
                .responseContains("No Action matches the given query.");
    }

    @DisplayName("Создание действия с ограничением allowed_group на уровне realm")
    @TmsLink("1282682")
    @Test
    public void actionAllowedGroupRealmLevelTest() {
        Action actionModel = createActionModel("action_for_allowed_group_realm_api_test");
        actionModel.setAllowedGroups(Collections.singletonList("superadmin"));
        Action action = createAction(actionModel);
        Action actionById = getActionById(action.getId());
        assertNotNull(actionById);
        AssertResponse.run(() -> getObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsRead, action.getId()))
                .status(404)
                .responseContains("No Action matches the given query.");
    }

    @DisplayName("Создание действия с ограничением restricted_group на уровне realm и ограничением allowed_group на уровне account")
    @TmsLink("1279003")
    @Test
    public void actionRestrictedGroupRealmLevelAndAllowedGroupAccountTest() {
        Action actionModel = createActionModel("action_for_restricted_group_and_allowed_group_api_test");
        actionModel.setAllowedGroups(Collections.singletonList("account:test"));
        actionModel.setRestrictedGroups(Collections.singletonList("qa-admin"));
        Action action = createAction(actionModel);
        Action actionById = getActionById(action.getId());
        assertNotNull(actionById);
        AssertResponse.run(() -> getObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsRead, action.getId()))
                .status(404)
                .responseContains("No Action matches the given query.");
    }

    @DisplayName("Создание действия с ограничением allowed_group на уровне realm и ограничением restricted_group на уровне account")
    @TmsLink("1282689")
    @Test
    public void actionAllowedGroupRealmLevelAndRestrictedGroupAccountTest() {
        Action actionModel = createActionModel("action_for_restricted_group_account_lvl_and_allowed_group_realm_lvl_api_test");
        actionModel.setAllowedGroups(Collections.singletonList("superadmin"));
        actionModel.setRestrictedGroups(Collections.singletonList("account:role2_api_tests"));
        Action action = createAction(actionModel);
        Action actionById = getActionById(action.getId());
        assertNotNull(actionById);
        AssertResponse.run(() -> getObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsRead, action.getId()))
                .status(404)
                .responseContains("No Action matches the given query.");
    }

    @DisplayName("Создание действия с ограничением allowed_group на уровне account")
    @TmsLink("1279058")
    @Test
    public void actionAllowedGroupAccountTest() {
        Action actionModel = createActionModel("action_for_allowed_group_api_test");
        actionModel.setAllowedGroups(Collections.singletonList("account:role_api_tests"));
        Action action = createAction(actionModel);
        Action actionById = getActionById(action.getId());
        assertNotNull(actionById);
        AssertResponse.run(() -> getObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsRead, action.getId()))
                .status(404)
                .responseContains("No Action matches the given query.");
    }

    @DisplayName("Создание действия с ограничением restricted_group на уровне account")
    @TmsLink("1282697")
    @Test
    public void actionRestrictedGroupAccountTest() {
        Action actionModel = createActionModel("action_for_restricted_group_api_test");
        actionModel.setRestrictedGroups(Collections.singletonList("account:role2_api_tests"));
        Action action = createAction(actionModel);
        Action actionById = getActionById(action.getId());
        assertNotNull(actionById);
        AssertResponse.run(() -> getObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsRead, action.getId()))
                .status(404)
                .responseContains("No Action matches the given query.");
    }

    @DisplayName("Создание действия с ограничением по имени пользователя в restricted_group")
    @TmsLink("1361613")
    @Test
    public void actionWithUserNameInRestrictedGroupTest() {
        GlobalUser user = GlobalUser.builder()
                .role(PRODUCT_CATALOG_VIEWER)
                .build().createObject();
        Action actionModel = createActionModel("action_with_user_name_in_restriction_group_api_test");
        actionModel.setRestrictedGroups(Collections.singletonList(user.getUsername()));
        Action action = createAction(actionModel);
        Action actionById = getActionById(action.getId());
        assertNotNull(actionById);
        AssertResponse.run(() -> getObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsRead, action.getId()))
                .status(404)
                .responseContains("No Action matches the given query.");
    }

    @DisplayName("Создание действия с ограничением по имени в allowed_group")
    @TmsLink("1361615")
    @Test
    public void actionWithUserNameInAllowedGroupTest() {
        GlobalUser user = GlobalUser.builder()
                .role(Role.PRODUCT_CATALOG_ADMIN)
                .build().createObject();
        Action actionModel = createActionModel("action_with_user_name_in_allowed_group_api_test");
        actionModel.setAllowedGroups(Collections.singletonList(user.getUsername()));
        Action action = createAction(actionModel);
        Action actionById = getActionById(action.getId());
        assertNotNull(actionById);
        AssertResponse.run(() -> getObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsRead, action.getId()))
                .status(404)
                .responseContains("No Action matches the given query.");
    }
}
