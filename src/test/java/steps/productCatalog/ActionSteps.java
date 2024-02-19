package steps.productCatalog;

import core.enums.Role;
import core.helper.StringUtils;
import core.helper.http.Attachment;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import core.helper.http.Response;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import models.AbstractEntity;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.action.GetActionList;
import org.json.JSONArray;
import org.json.JSONObject;
import tests.routes.ActionProductCatalogApi;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tests.routes.ActionProductCatalogApi.*;

public class ActionSteps extends ProductCatalogSteps {

    @Step("Получение списка действий продуктового каталога")
    public static GetActionList getActionList() {
        return getProductCatalogAdmin()
                .api(apiV1ActionsList)
                .extractAs(GetActionList.class);
    }

    @Step("Получение списка audit для действий с id {id}")
    public static List<ProductAudit> getActionAuditList(String id) {
        return getProductCatalogAdmin()
                .api(apiV1ActionsAudit, id)
                .jsonPath()
                .getList("list", ProductAudit.class);
    }

    @Step("Получение списка audit для действий с id {id} и фильтром {queryBuilder}")
    public static List<ProductAudit> getActionAuditListWithQuery(String id, QueryBuilder queryBuilder) {
        return getProductCatalogAdmin()
                .api(apiV1ActionsAudit, id, queryBuilder)
                .jsonPath()
                .getList("list", ProductAudit.class);
    }

    @Step("Получение деталей audit действия с audit_id {auditId}")
    public static Response getActionAuditDetails(String auditId) {
        return getProductCatalogAdmin()
                .api(apiV1ActionsAuditDetails, new QueryBuilder().add("audit_id", auditId));
    }

    @Step("Получение списка аудита действия для obj_keys")
    public static List<ProductAudit> getAuditListByActionKeys(String keyValue) {
        return getProductCatalogAdmin()
                .body(new JSONObject().put("obj_keys", new JSONObject().put("name", keyValue)))
                .api(apiV1ActionsAuditByObjectKeys)
                .jsonPath()
                .getList("list", ProductAudit.class);
    }

    /*
    На данный момент можно изменить только один параметр is_for_item
     */
    @Step("Массовое изменение параметров действия")
    public static void massChangeActionParam(List<String> id, boolean isForItem) {
        getProductCatalogAdmin()
                .body(new JSONObject().put("objects_change", new JSONArray().put(new JSONObject().put("id", id)
                        .put("params", new JSONObject().put("is_for_items", isForItem)))))
                .api(apiV1ActionsMassChange);
    }

    @Step("Создание действия c именем {action.name}")
    public static Action createAction(Action action) {
        return getProductCatalogAdmin()
                .body(action.toJson())
                .api(ActionProductCatalogApi.apiV1ActionsCreate)
                .extractAs(Action.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Создание действия")
    public static Response createAction(JSONObject body) {
        return getProductCatalogAdmin()
                .body(body)
                .api(ActionProductCatalogApi.apiV1ActionsCreate);
    }

    public static Action createAction(String name) {
        Action action = Action.builder()
                .name(name)
                .build();
        return createAction(action);
    }

    public static Action createAction() {
        Action action = Action.builder()
                .name(StringUtils.getRandomStringApi(7))
                .build();
        return createAction(action);
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
        getProductCatalogAdmin()
                .api(apiV1ActionsDelete, id)
                .assertStatus(204);
    }

    @Step("Удаление действия по имени {name}")
    public static void deleteActionByName(String name) {
        getProductCatalogAdmin()
                .api(apiV2ActionsDelete, name)
                .assertStatus(204);
    }

    @Step("Проверка существования действия по имени '{name}'")
    public static boolean isActionExists(String name) {
        return getProductCatalogAdmin()
                .api(apiV1ActionsExists, new QueryBuilder().add("name", name))
                .jsonPath().get("exists");
    }

    @Step("Получение действия по Id {actionId}")
    public static Action getActionById(String actionId) {
        return getProductCatalogAdmin()
                .api(apiV1ActionsRead, actionId)
                .extractAs(Action.class);
    }

    @Step("Получение действия по имени {name}")
    public static Action getActionByName(String name) {
        return getProductCatalogAdmin()
                .api(apiV2ActionsRead, name)
                .extractAs(Action.class);
    }

    @SneakyThrows
    @Step("Импорт действия продуктового каталога")
    public static ImportObject importAction(String pathName) {
        File file = new File(pathName);
        return getProductCatalogAdmin()
                .api(apiV1ActionsObjImport, new Attachment("file", file.getName(), Files.readAllBytes(file.toPath())))
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @SneakyThrows
    @Step("Импорт действия продуктового каталога")
    public static void importActionWithTagList(String pathName) {
        File file = new File(pathName);
        getProductCatalogAdmin()
                .api(apiV1ActionsObjImport, new QueryBuilder().add("with_tags", true), new Attachment("file", file.getName(), Files.readAllBytes(file.toPath())));
    }

    @Step("Копирование действия по Id")
    public static Action copyActionById(String actionId) {
        return getProductCatalogAdmin()
                .api(apiV1ActionsCopy, actionId)
                .extractAs(Action.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Копирование действия по имени {name}")
    public static Action copyActionByName(String name) {
        return getProductCatalogAdmin()
                .api(apiV2ActionsCopy, name)
                .extractAs(Action.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Частичное обновление действия")
    public static Response partialUpdateAction(String id, JSONObject object) {
        return getProductCatalogAdmin()
                .body(object)
                .api(apiV1ActionsPartialUpdate, id);
    }

    @Step("Частичное обновление действия под ролью {role}")
    public static void partialUpdateActionWithAnotherRole(String id, JSONObject object, Role role) {
        new Http(productCatalogURL)
                .setRole(role)
                .body(object)
                .api(apiV1ActionsPartialUpdate, id);
    }

    @Step("Частичное обновление действия по имени {name}")
    public static void partialUpdateActionByName(String name, JSONObject object) {
        getProductCatalogAdmin()
                .body(object)
                .api(apiV2ActionsPartialUpdate, name);
    }

    @Step("Получение действия по фильтру = {query}")
    public static List<Action> getActionListWithQueryParam(QueryBuilder query) {
        return getProductCatalogAdmin()
                .api(apiV1ActionsList, query)
                .assertStatus(200)
                .extractAs(GetActionList.class)
                .getList();
    }

    @Step("Получение действия по фильтру = {query}")
    public static Action getActionByIdWithQueryParam(String id, QueryBuilder query) {
        return getProductCatalogAdmin()
                .api(apiV1ActionsRead, id, query)
                .assertStatus(200)
                .extractAs(Action.class);

    }

    @Step("Получение списка действий по списку type_provider")
    public static List<Action> getActionListByTypeProvider(JSONObject body) {
        return getProductCatalogAdmin()
                .body(body)
                .api(apiV1ActionsList)
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
        return getProductCatalogAdmin()
                .api(apiV1ActionsObjExport, objectId, new QueryBuilder().add("as_file", true));
    }

    @Step("Экспорт действия по имени {name}")
    public static void exportActionByName(String name) {
        getProductCatalogAdmin()
                .api(apiV2ActionsObjExport, name);
    }

    @Step("Экспорт нескольких действий")
    public static void exportActions(JSONObject json) {
        getProductCatalogAdmin()
                .body(json)
                .api(apiV1ActionsObjectsExport);
    }

    @Step("Добавление списка Тегов действиям")
    public static void addTagListToAction(List<String> tagsList, QueryBuilder query) {
        getProductCatalogAdmin()
                .body(new JSONObject().put("add_tags", tagsList))
                .api(apiV1ActionsAddTagList, query);
    }

    @Step("Удаление списка Тегов действиям")
    public static void removeTagListToAction(List<String> tagsList, QueryBuilder query) {
        getProductCatalogAdmin()
                .body(new JSONObject().put("remove_tags", tagsList))
                .api(apiV1ActionsRemoveTagList, query);
    }
}
