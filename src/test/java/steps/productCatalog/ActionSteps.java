package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.productCatalog.action.Action;
import models.productCatalog.action.GetActionList;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ActionSteps extends Steps {

    private static final String actionUrl = "/api/v1/actions/";

    @Step("Получение списка Действий продуктового каталога")
    public static List<Action> getActionList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(actionUrl)
                .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetActionList.class).getList();
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
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get(actionUrl + objectId + "/")
                .extractAs(Action.class);
    }

    @Step("Импорт действия продуктового каталога")
    public static void importAction(String pathName) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(actionUrl + "obj_import/", "file", new File(pathName))
                .assertStatus(200);
    }

    @Step("Копирование действия по Id")
    public static void copyActionById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(actionUrl + objectId + "/copy/")
                .assertStatus(200);
    }
}
