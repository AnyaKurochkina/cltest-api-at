package api.cloud.productCatalog.action;

import core.helper.StringUtils;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.*;
import static tests.routes.ActionProductCatalogApi.*;

@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionsNegativeTest extends ActionBaseTest {

    @DisplayName("Негативный тест на получение действия по Id без токена")
    @TmsLink("642485")
    @Test
    public void getActionByIdWithOutTokenTest() {
        Action action = createAction(createActionModel("get_action_without_token_example_test_api"));
        AssertResponse.run(() -> requestWithOutToken(apiV1ActionsRead, action.getId()))
                .status(401)
                .responseContains("Unauthorized");
    }

    @DisplayName("Негативный тест на копирование действия по Id без токена")
    @TmsLink("642497")
    @Test
    public void copyActionByIdWithOutTokenTest() {
        Action action = createAction(createActionModel("copy_action_without_token_example_test_api"));
        AssertResponse.run(() -> requestWithOutToken(apiV1ActionsCopy, action.getId()))
                .status(401)
                .responseContains("Unauthorized");
    }

    @DisplayName("Негативный тест на удаление действия без токена")
    @TmsLink("642528")
    @Test
    public void deleteActionWithOutToken() {
        Action action = createAction(createActionModel("delete_action_without_token_example_test_api"));
        AssertResponse.run(() -> requestWithOutToken(apiV1ActionsDelete, action.getId()))
                .status(401)
                .responseContains("Unauthorized");
        assertTrue(isActionExists(action.getName()), "Действие существует");
    }

    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @TmsLink("642523")
    @ParameterizedTest
    @MethodSource("testData")
    public void createActionWithInvalidCharacters(String name, String message) {
        AssertResponse.run(() -> createAction(createActionModel(name))).status(400)
                .responseContains(format(message, name));
    }

    static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of("NameWithUppercase", "Cannot instantiate (Action) named ({})"),
                Arguments.of("nameWithUppercaseInMiddle", "Cannot instantiate (Action) named ({})"),
                Arguments.of("Имя", "Cannot instantiate (Action) named ({})"),
                Arguments.of("a&b&c", "Cannot instantiate (Action) named ({})"),
                Arguments.of("", "Это поле не может быть пустым."),
                Arguments.of(" ", "Это поле не может быть пустым.")
        );
    }

    @DisplayName("Негативный тест на создание действия с существующим именем")
    @TmsLink("642520")
    @Test
    public void createActionWithSameName() {
        String name = "create_action_with_same_name_example_test_api";
        createAction(createActionModel(name));
        AssertResponse.run(() -> createAction(createActionModel(name)))
                .status(400)
                .responseContains("action с таким name уже существует.");
    }

    @DisplayName("Негативный тест на обновление действия по Id без токена")
    @TmsLink("642510")
    @Test
    public void updateActionByIdWithOutToken() {
        Action action = createAction(createActionModel("update_action_without_token_example_test_api"));
        AssertResponse.run(() -> requestWithBodyWithOutToken(apiV1ActionsPartialUpdate, action.getId(), new JSONObject().put("description", "UpdateDescription")))
                .status(401)
                .responseContains("Unauthorized");
    }

    @DisplayName("Негативный тест на создание действия с двумя параметрами одновременно graph_version_pattern и graph_version")
    @TmsLink("642514")
    @Test
    public void doubleVersionTest() {
        Action action = Action.builder()
                .name(StringUtils.getRandomStringApi(8))
                .version("1.1.1")
                .graphVersion("1.0.0")
                .graphVersionPattern("1.")
                .build();
        AssertResponse.run(() -> createAction(action))
                .status(400)
                .responseContains("You can't use both 'version' and 'version pattern' at same time in the ActionVersionSerializer");
    }

    @DisplayName("Негативный тест на обновление действия до той же версии/текущей")
    @TmsLink("642518")
    @Test
    public void sameVersionTest() {
        Action actionModel = createActionModel("action_same_version_test_api");
        actionModel.setVersion("1.0.1");
        Action action = createAction(actionModel);
        AssertResponse.run(() -> partialUpdateAction(action.getId(), action.toJson()))
                .status(400)
                .responseContains(format("Версия {} для {} уже существует", action.getVersion(), action.getName()));
    }

    @Test
    @DisplayName("Негативный тест на передачу невалидного значения current_version в действиях")
    @TmsLink("821961")
    public void setInvalidCurrentVersionAction() {
        Action action = createAction(createActionModel("invalid_current_version_action_test_api"));
        AssertResponse.run(() -> partialUpdateAction(action.getId(), new JSONObject().put("current_version", "2")))
                .status(400)
                .responseContains("You must specify version in pattern like \\\"{num}. | {num}.{num}.\\\"");
    }
}
