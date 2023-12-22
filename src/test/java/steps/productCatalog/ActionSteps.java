package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.Meta;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.action.GetActionList;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;
import ui.cloud.pages.productCatalog.enums.action.ItemStatus;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ActionSteps extends Steps {

    private static final String actionUrl = "/api/v1/actions/";
    private static final String actionUrlV2 = "/api/v2/actions/";


    @Step("Получение списка действий продуктового каталога")
    public static List<Action> getActionList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl)
                .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetActionList.class).getList();
    }

    @Step("Получение Meta данных списка действий продуктового каталога")
    public static Meta getMetaActionList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl)
                .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetActionList.class).getMeta();
    }

    /*
    На данный момент можно изменить только один параметр is_for_item
     */
    @Step("Массовое изменение параметров действия")
    public static Response massChangeActionParam(List<String> id, boolean isForItem) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("objects_change", new JSONArray().put(new JSONObject().put("id", id)
                        .put("params", new JSONObject().put("is_for_items", isForItem)))))
                .post(actionUrl + "mass_change/")
                .assertStatus(200);
    }

    @Step("Создание действия")
    public static Response createAction(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(actionUrl);
    }

    @Step("Создание действия c именем {name}")
    public static Action createAction(String name) {
        return Action.builder()
                .name(name)
                .build()
                .createObject();
    }

    @Step("Создание действия")
    public static Action createAction() {
        return Action.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase())
                .build()
                .createObject();
    }

    @Step("Создание действия")
    public static Action createAction(String name, EventType type, EventProvider provider) {
        return Action.builder()
                .name(name)
                .title(name)
                .eventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                        .event_type(type.getValue())
                        .event_provider(provider.getValue())
                        .build()))
                .requiredItemStatuses(Collections.singletonList(ItemStatus.ON.getValue()))
                .build()
                .createObject();
    }

    @Step("Проверка сортировки списка действий")
    public static boolean isActionListSorted(List<Action> list) {
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            Action currentAction = list.get(i);
            Action nextAction = list.get(i + 1);
            Integer currentNumber = currentAction.getNumber();
            Integer nextNumber = nextAction.getNumber();
            String currentTitle = delNoDigOrLet(currentAction.getTitle());
            String nextTitle = delNoDigOrLet(nextAction.getTitle());
            if (currentNumber > nextNumber || ((currentNumber.equals(nextNumber)) && currentTitle.compareToIgnoreCase(nextTitle) > 0)) {
                return false;
            }
        }
        return true;
    }

    @Step("Удаление действия по id")
    public static void deleteActionById(String id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(actionUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Поиск ID действия по имени с использованием multiSearch")
    public static String getActionIdByNameWithMultiSearch(String name) {
        String actionId = null;
        List<Action> list = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?include=total_count&page=1&per_page=50&multisearch=" + name)
                .assertStatus(200).extractAs(GetActionList.class).getList();
        for (Action action : list) {
            if (action.getName().equals(name)) {
                actionId = action.getActionId();
                break;
            }
        }
        Assertions.assertNotNull(actionId, String.format("Действие с именем: %s, с помощью multiSearch не найден", name));
        return actionId;
    }

    @Step("Удаление действия по имени {name}")
    public static void deleteActionByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(actionUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Проверка существования действия по имени '{name}'")
    public static boolean isActionExists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Получение действия по Id")
    public static Action getActionById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + objectId + "/")
                .extractAs(Action.class);
    }

    @Step("Получение действия по имени {name}")
    public static Action getActionByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrlV2 + name + "/")
                .extractAs(Action.class);
    }

    @Step("Получение действия по Id под ролью Viewer")
    public static Response getActionViewerById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(actionUrl + objectId + "/");
    }

    @Step("Импорт действия продуктового каталога")
    public static ImportObject importAction(String pathName) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(actionUrl + "obj_import/", "file", new File(pathName))
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Копирование действия по Id")
    public static Action copyActionById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(actionUrl + objectId + "/copy/")
                .assertStatus(200)
                .extractAs(Action.class);
    }

    @Step("Копирование действия по имени {name}")
    public static Action copyActionByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(actionUrlV2 + name + "/copy/")
                .assertStatus(200)
                .extractAs(Action.class);
    }

    @Step("Частичное обновление действия")
    public static Response partialUpdateAction(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(actionUrl + id + "/");
    }

    @Step("Частичное обновление действия")
    public static Response partialUpdateActionWithAnotherRole(String id, JSONObject object, Role role) {
        return new Http(productCatalogURL)
                .setRole(role)
                .body(object)
                .patch(actionUrl + id + "/");
    }

    @Step("Частичное обновление действия по имени {name}")
    public static void partialUpdateActionByName(String name, JSONObject object) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(actionUrlV2 + name + "/")
                .assertStatus(200);
    }

    @Step("Получение списка действия по имени")
    public static List<Action> getActionListByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?name=" + name)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий по именам")
    public static List<Action> getActionListByNames(String... name) {
        String names = String.join(",", name);
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?name__in=" + names)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действия по type")
    public static List<Action> getActionListByType(String type) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?type=" + type)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий используя multisearch")
    public static List<Action> getActionListWithMultiSearch(String str) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?multisearch=" + str)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий по фильтру")
    public static List<Action> getActionListByFilter(String filter, Object value) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?{}={}", filter, value)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий по фильтрам")
    public static List<Action> getActionListByFilters(String... filter) {
        String filters = String.join("&", filter);
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?" + filters)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение действия по фильтру = {filter}")
    public static Action getActionByFilter(String id, String filter) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "{}/?{}", id, filter)
                .assertStatus(200)
                .extractAs(Action.class);

    }

    @Step("Получение действия по Id без токена")
    public static String getActionByIdWithOutToken(String objectId) {
        return new Http(productCatalogURL).setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + objectId + "/").assertStatus(401)
                .jsonPath().getString("error.message");
    }

    @Step("Копирование действия по Id без токена")
    public static String copyActionByIdWithOutToken(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(actionUrl + objectId + "/copy/")
                .assertStatus(401).jsonPath().getString("error.message");
    }

    @Step("Удаление действия по Id без токена")
    public static String deleteActionByIdWithOutToken(String id) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .delete(actionUrl + id + "/").assertStatus(401)
                .jsonPath().getString("error.message");
    }

    @Step("Частичное обновление действия без токена")
    public static String partialUpdateActionWithOutToken(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(actionUrl + id + "/")
                .assertStatus(401)
                .jsonPath().getString("error.message");
    }

    @Step("Загрузка действия в Gitlab")
    public static Response dumpActionToGit(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(actionUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Загрузка действия в Gitlab по имени {name}")
    public static Response dumpActionToGitByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(actionUrlV2 + name + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка действия из Gitlab")
    public static void loadActionFromGit(JSONObject body) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(actionUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Сравнение версий действия")
    public static Action compareActionVersions(String id, String version1, String version2) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + id + "/?version={}&compare_with_version={}", version1, version2)
                .extractAs(Action.class);
    }

    @Step("Сортировка действий по дате создания")
    public static List<Action> orderingActionByCreateData() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Сортировка действий по дате обновления")
    public static List<Action> orderingActionByUpDateData() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий по списку type_provider")
    public static List<Action> getActionListByTypeProvider(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .get(actionUrl)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    public static void checkEventProvider(List<Action> actionList, String eventType, String eventProvider) {
        for (Action action : actionList) {
            List<EventTypeProvider> eventTypeProviderList = action.getEventTypeProvider();
            assertTrue(isTypeProviderContains(eventType, eventProvider, eventTypeProviderList),
                    String.format("%s не содержит eventType %s и %s eventProvider", action.getName(), eventType, eventProvider));
        }
    }

    public static boolean isTypeProviderContains(String eventType, String eventProvider, List<EventTypeProvider> eventTypeProviderList) {
        if (!eventTypeProviderList.isEmpty()) {
            for (EventTypeProvider eventTypeProvider : eventTypeProviderList) {
                if (eventTypeProvider.getEvent_type().equals(eventType) & eventTypeProvider.getEvent_provider().equals(eventProvider)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Step("Экспорт действия по Id")
    public static Response exportActionById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + objectId + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Экспорт действия по имени {name}")
    public static void exportActionByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrlV2 + name + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Добавление списка Тегов действиям")
    public static void addTagListToAction(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("add_tags", tagsList))
                .post(actionUrl + "add_tag_list/?name__in=" + names)
                .assertStatus(200);
    }

    @Step("Удаление списка Тегов действиям")
    public static void removeTagListToAction(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("remove_tags", tagsList))
                .post(actionUrl + "remove_tag_list/?name__in=" + names)
                .assertStatus(200);
    }
}
