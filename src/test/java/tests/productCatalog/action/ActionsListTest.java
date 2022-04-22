package tests.productCatalog.action;

import core.helper.Configure;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.action.getActionList.response.GetActionsListResponse;
import httpModels.productCatalog.action.getActionList.response.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Action;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionsListTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/actions/","productCatalog/actions/createAction.json");

    @DisplayName("Получение списка действий. Список отсортирован по дате создания от нового к старому и имени без учета спец. символов")
    @TmsLink("642429")
    @Test
    public void getActionList() {
        String actionName = "create_action_example_for_get_list_test_api";
        Action.builder()
                .actionName(actionName)
                .build()
                .createObject();
        List<ItemImpl> list = steps.getProductObjectList(GetActionsListResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка действий")
    @TmsLink("679025")
    @Test
    public void getMeta() {
        String str = steps.getMeta(GetActionsListResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"), "Значение поля next " +
                    "несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка действий по имени")
    @TmsLink("642526")
    @Test
    public void getActionListByName() {
        String actionName = "create_action_example_for_get_list_by_name_test_api";
        Action.builder()
                .actionName(actionName)
                .build()
                .createObject();
        GetActionsListResponse list = (GetActionsListResponse) steps.getObjectListByName(actionName, GetActionsListResponse.class);
        assertEquals(1, list.getList().size());
        assertEquals(actionName, list.getList().get(0).getName());
    }

    @DisplayName("Получение списка действий по именам")
    @TmsLink("783457")
    @Test
    public void getActionListByNames() {
        String actionName = "create_action_example_for_get_list_by_names_1_test_api";
        Action.builder()
                .actionName(actionName)
                .build()
                .createObject();
        String secondAction = "create_action_example_for_get_list_by_names_2_test_api";
        Action.builder()
                .actionName(secondAction)
                .build()
                .createObject();
        GetActionsListResponse list = (GetActionsListResponse) steps.getObjectsListByNames(GetActionsListResponse.class,
                actionName, secondAction);
        assertEquals(2, list.getList().size(), "Список не содержит значений");
        assertEquals(secondAction, list.getList().get(0).getName());
        assertEquals(actionName, list.getList().get(1).getName());
    }

    @DisplayName("Получение списка действий по типу")
    @TmsLink("783463")
    @Test
    public void getActionListByType() {
        String actionName = "create_action_example_for_get_list_by_type_test_api";
        String actionType = "delete";
        Action.builder()
                .actionName(actionName)
                .type(actionType)
                .build()
                .createObject();
        GetActionsListResponse list = (GetActionsListResponse) steps.getObjectListByType(actionType, GetActionsListResponse.class);
        for (ListItem item : list.getList()) {
            assertEquals(actionType, item.getType());
        }
    }

    @DisplayName("Получение списка действий по title используя multisearch")
    @TmsLink("783469")
    @Test
    public void getActionListByTitleWithMutisearch() {
        String actionName = "create_action_example_for_get_list_by_title_with_multisearch_test_api";
        String actionTitle = "action_title";
        Action.builder()
                .actionName(actionName)
                .title(actionTitle)
                .build()
                .createObject();
        List<ItemImpl> list =  steps.getProductObjectListWithMultiSearch(GetActionsListResponse.class, actionTitle);
        for (ItemImpl item : list) {
            ListItem listItem =(ListItem) item;
            assertTrue(listItem.getTitle().contains(actionTitle));
        }
    }

    @DisplayName("Поиск действия по имени, с использованием multiSearch")
    @TmsLink("642503")
    @Test
    public void searchActionByName() {
        String actionName = "action_multisearch_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        String actionIdWithMultiSearch = steps.getProductObjectIdByNameWithMultiSearch(action.getActionName(), GetActionsListResponse.class);
        assertAll(
                () -> assertNotNull(actionIdWithMultiSearch, String.format("Действие с именем: %s не найден", actionName)),
                () -> assertEquals(action.getActionId(), actionIdWithMultiSearch, "Id действия не совпадают"));
    }
}
