package tests.productCatalog.action;

import core.helper.Configure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Action;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionsNegativeTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("actions/",
            "productCatalog/actions/createAction.json", Configure.ProductCatalogURL);

    @DisplayName("Негативный тест на получение действия по Id без токена")
    @TmsLink("642485")
    @Test
    public void getActionByIdWithOutToken() {
        String actionName = "get_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.getByIdWithOutToken(action.getActionId());
    }

    @DisplayName("Негативный тест на копирование действия по Id без токена")
    @TmsLink("642497")
    @Test
    public void copyActionByIdWithOutToken() {
        String actionName = "copy_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.copyByIdWithOutToken(action.getActionId());
    }

    @DisplayName("Негативный тест на удаление действия без токена")
    @TmsLink("642528")
    @Test
    public void deleteActionWithOutToken() {
        String actionName = "delete_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(action.getActionId());
        assertTrue(steps.isExists(actionName), "Действие существует");
    }

    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @TmsLink("642523")
    @Test
    public void createActionWithInvalidCharacters() {
        assertAll("Действие создалось с недопустимым именем",
                () -> steps.createProductObject(steps
                        .createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("имя")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("Имя")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("a&b&c")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("")).assertStatus(400),
                () -> steps.createProductObject(steps
                        .createJsonObject(" ")).assertStatus(400)
        );
    }

    @DisplayName("Негативный тест на создание действия с существующим именем")
    @TmsLink("642520")
    @Test
    public void createActionWithSameName() {
        String actionName = "create_action_with_same_name_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.createProductObject(steps
                .createJsonObject(action.getActionName())).assertStatus(400);
    }

    @DisplayName("Негативный тест на обновление действия по Id без токена")
    @TmsLink("642510")
    @Test
    public void updateActionByIdWithOutToken() {
        String actionName = "update_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(action.getActionId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на создание действия с двумя параметрами одновременно graph_version_pattern и graph_version")
    @TmsLink("642514")
    @Test
    public void doubleVersionTest() {
        steps.createProductObject(Action.builder()
                        .actionName("negative_object")
                        .build()
                        .init()
                        .getTemplate()
                        .set("$.version", "1.1.1")
                        .set("$.graph_version", "1.0.0")
                        .set("$.graph_version_pattern", "1.")
                        .build())
                .assertStatus(500);
    }

    @DisplayName("Негативный тест на обновление действия до той же версии/текущей")
    @TmsLink("642518")
    @Test
    public void sameVersionTest() {
        String actionName = "action_same_version_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.1")
                .build()
                .createObject();
        steps.patchRow(Action.builder().actionName(actionName).build().init().getTemplate()
                .set("$.version", "1.0.1")
                .build(), action.getActionId()).assertStatus(500);
    }
}
