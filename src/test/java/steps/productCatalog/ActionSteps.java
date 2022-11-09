package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.Meta;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.action.GetActionList;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ActionSteps extends Steps {

    private static final String actionUrl = "/api/v1/actions/";

    @Step("Получение списка действий продуктового каталога")
    public static List<Action> getActionList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl)
                .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetActionList.class).getList();
    }

    @Step("Получение Meta данных списка действий продуктового каталога")
    public static Meta getMetaActionList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl)
                .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetActionList.class).getMeta();
    }

    @Step("Создание действия")
    public static Response createAction(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(actionUrl);
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
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(actionUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Поиск ID действия по имени с использованием multiSearch")
    public static String getActionIdByNameWithMultiSearch(String name) {
        String actionId = null;
        List<Action> list = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?include=total_count&page=1&per_page=50&multisearch=" + name)
                .assertStatus(200).extractAs(GetActionList.class).getList();
        for (Action action : list) {
            if (action.getActionName().equals(name)) {
                actionId = action.getActionId();
                break;
            }
        }
        Assertions.assertNotNull(actionId, String.format("Действие с именем: %s, с помощью multiSearch не найден", name));
        return actionId;
    }

    @Step("Удаление действия по имени")
    public static void deleteActionByName(String name) {
        deleteActionById(getActionIdByNameWithMultiSearch(name));
    }

    @Step("Проверка существования действия по имени")
    public static boolean isActionExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Получение действия по Id")
    public static Action getActionById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + objectId + "/")
                .extractAs(Action.class);
    }

    @Step("Получение действия по Id под ролью Viewer")
    public static Response getActionViewerById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(actionUrl + objectId + "/");
    }

    @Step("Импорт действия продуктового каталога")
    public static void importAction(String pathName) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(actionUrl + "obj_import/", "file", new File(pathName))
                .assertStatus(200);
    }

    @Step("Копирование действия по Id")
    public static Action copyActionById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(actionUrl + objectId + "/copy/")
                .assertStatus(200)
                .extractAs(Action.class);
    }

    @Step("Частичное обновление действия")
    public static Response partialUpdateAction(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(actionUrl + id + "/");
    }

    @Step("Получение списка действия по имени")
    public static List<Action> getActionListByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?name=" + name)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий по именам")
    public static List<Action> getActionListByNames(String... name) {
        String names = String.join(",", name);
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?name__in=" + names)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действия по type")
    public static List<Action> getActionListByType(String type) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?type=" + type)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий используя multisearch")
    public static List<Action> getActionListWithMultiSearch(String str) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?multisearch=" + str)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий по фильтру")
    public static List<Action> getActionListByFilter(String filter, Object value) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?{}={}", filter, value)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение действия по фильтру = {filter}")
    public static Action getActionByFilter(String id, String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "{}/?{}", id, filter)
                .assertStatus(200)
                .extractAs(Action.class);

    }

    @Step("Получение действия по Id без токена")
    public static String getActionByIdWithOutToken(String objectId) {
        return new Http(ProductCatalogURL).setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + objectId + "/").assertStatus(401)
                .jsonPath().getString("error.message");
    }

    @Step("Копирование действия по Id без токена")
    public static String copyActionByIdWithOutToken(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(actionUrl + objectId + "/copy/")
                .assertStatus(401).jsonPath().getString("error.message");
    }

    @Step("Удаление действия по Id без токена")
    public static String deleteActionByIdWithOutToken(String id) {
        return new Http(ProductCatalogURL)
                .setWithoutToken()
                .delete(actionUrl + id + "/").assertStatus(401)
                .jsonPath().getString("error.message");
    }

    @Step("Частичное обновление действия без токена")
    public static String partialUpdateActionWithOutToken(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(actionUrl + id + "/")
                .assertStatus(401)
                .jsonPath().getString("error.message");
    }

    @Step("Загрузка действия в Gitlab")
    public static Response dumpActionToGit(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(actionUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка действия из Gitlab")
    public static void loadActionFromGit(JSONObject body) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(actionUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Сравнение версий действия")
    public static Action compareActionVersions(String id, String version1, String version2) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + id + "/?version={}&compare_with_version={}", version1, version2)
                .extractAs(Action.class);
    }

    @Step("Сортировка действий по дате создания")
    public static List<Action> orderingActionByCreateData() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Сортировка действий по дате обновления")
    public static List<Action> orderingActionByUpDateData() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение списка действий по списку type_provider")
    public static List<Action> getActionListByTypeProvider(JSONObject body) {
        return new Http(ProductCatalogURL)
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
                    String.format("%s не содержит eventType %s и %s eventProvider", action.getActionName(), eventType, eventProvider));
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
}
