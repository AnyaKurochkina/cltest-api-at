package api.cloud.productCatalog.action;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.action.Action;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static core.enums.Role.PRODUCT_CATALOG_VIEWER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.ActionSteps.getActionById;
import static steps.productCatalog.ActionSteps.getActionViewerById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionRestrictedAndAllowedGroupsTest extends Tests {

    @DisplayName("Создание действия с ограничением restricted group на уровне realm")
    @TmsLink("1274379")
    @Test
    public void actionRestrictedGroupRealmLevelTest() {
        Action action = Action.builder()
                .name("action_for_restricted_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("qa-admin"))
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        assertNotNull(actionById);
        String msg = getActionViewerById(action.getActionId()).assertStatus(404).extractAs(ErrorMessage.class).getMessage();
        assertEquals("No Action matches the given query.", msg);
    }

    @DisplayName("Создание действия с ограничением allowed_group на уровне realm")
    @TmsLink("1282682")
    @Test
    public void actionAllowedGroupRealmLevelTest() {
        Action action = Action.builder()
                .name("action_for_allowed_group_realm_api_test")
                .version("1.0.1")
                .allowedGroups(Collections.singletonList("superadmin"))
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        assertNotNull(actionById);
        String msg = getActionViewerById(action.getActionId()).assertStatus(404).extractAs(ErrorMessage.class).getMessage();
        assertEquals("No Action matches the given query.", msg);
    }

    @DisplayName("Создание действия с ограничением restricted_group на уровне realm и ограничением allowed_group на уровне account")
    @TmsLink("1279003")
    @Test
    public void actionRestrictedGroupRealmLevelAndAllowedGroupAccountTest() {
        Action action = Action.builder()
                .name("action_for_restricted_group_and_allowed_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("qa-admin"))
                .allowedGroups(Collections.singletonList("account:test"))
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        assertNotNull(actionById);
        String msg = getActionViewerById(action.getActionId()).assertStatus(404).extractAs(ErrorMessage.class).getMessage();
        assertEquals("No Action matches the given query.", msg);
    }

    @DisplayName("Создание действия с ограничением allowed_group на уровне realm и ограничением restricted_group на уровне account")
    @TmsLink("1282689")
    @Test
    public void actionAllowedGroupRealmLevelAndRestrictedGroupAccountTest() {
        Action action = Action.builder()
                .name("action_for_restricted_group_account_lvl_and_allowed_group_realm_lvl_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("account:role2_api_tests"))
                .allowedGroups(Collections.singletonList("superadmin"))
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        assertNotNull(actionById);
        String msg = getActionViewerById(action.getActionId()).assertStatus(404).extractAs(ErrorMessage.class).getMessage();
        assertEquals("No Action matches the given query.", msg);
    }

    @DisplayName("Создание действия с ограничением allowed_group на уровне account")
    @TmsLink("1279058")
    @Test
    public void actionAllowedGroupAccountTest() {
        Action action = Action.builder()
                .name("action_for_allowed_group_api_test")
                .version("1.0.1")
                .allowedGroups(Collections.singletonList("account:role_api_tests"))
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        assertNotNull(actionById);
        String msg = getActionViewerById(action.getActionId()).assertStatus(404).extractAs(ErrorMessage.class).getMessage();
        assertEquals("No Action matches the given query.", msg);
    }

    @DisplayName("Создание действия с ограничением restricted_group на уровне account")
    @TmsLink("1282697")
    @Test
    public void actionRestrictedGroupAccountTest() {
        Action action = Action.builder()
                .name("action_for_restricted_group_account_level_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("account:role2_api_tests"))
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        assertNotNull(actionById);
        String msg = getActionViewerById(action.getActionId()).assertStatus(404).extractAs(ErrorMessage.class).getMessage();
        assertEquals("No Action matches the given query.", msg);
    }

    @DisplayName("Создание действия с ограничением по имени пользователя в restricted_group")
    @TmsLink("1361613")
    @Test
    public void actionWithUserNameInRestrictedGroupTest() {
        GlobalUser user = GlobalUser.builder()
                .role(PRODUCT_CATALOG_VIEWER)
                .build().createObject();
        Action action = Action.builder()
                .name("action_with_user_name_in_restriction_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList(user.getUsername()))
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        assertNotNull(actionById);
        String msg = getActionViewerById(action.getActionId()).assertStatus(404).extractAs(ErrorMessage.class).getMessage();
        assertEquals("No Action matches the given query.", msg);
    }

    @DisplayName("Создание действия с ограничением по имени в allowed_group")
    @TmsLink("1361615")
    @Test
    public void actionWithUserNameInAllowedGroupTest() {
        GlobalUser user = GlobalUser.builder()
                .role(Role.PRODUCT_CATALOG_ADMIN)
                .build().createObject();
        Action action = Action.builder()
                .name("action_with_user_name_in_allowed_group_api_test")
                .version("1.0.1")
                .allowedGroups(Collections.singletonList(user.getUsername()))
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        assertNotNull(actionById);
        String msg = getActionViewerById(action.getActionId()).assertStatus(404).extractAs(ErrorMessage.class).getMessage();
        assertEquals("No Action matches the given query.", msg);
    }
}
