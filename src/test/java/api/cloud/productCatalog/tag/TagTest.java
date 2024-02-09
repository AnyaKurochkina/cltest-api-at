package api.cloud.productCatalog.tag;

import api.Tests;
import core.helper.StringUtils;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.template.Template;
import models.cloud.productCatalog.visualTeamplate.*;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.TagSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Теги")
@DisabledIfEnv("prod")
public class TagTest extends Tests {
    private static List<String> tagList = new ArrayList<>();

    @AfterAll
    public static void deleteTag() {
        for (String name : tagList) {
            deleteTagByName(name);
        }

    }

    @DisplayName("Создание Тега/Получение Тега по имени")
    @TmsLinks({@TmsLink("1695266"), @TmsLink("1695674")})
    @Test
    public void createTagTest() {
        String tagName = "create_tag";
        tagList.add(tagName);
        createTag(tagName);
        assertEquals(tagName, getTagByName(tagName).getName());
    }

    @DisplayName("Получение списка Тегов")
    @TmsLink("1695651")
    @Test
    public void getTagsListTest() {
        String tagName = "get_tag_list_test_api";
        tagList.add(tagName);
        createTag(tagName);
        List<models.cloud.productCatalog.tag.Tag> tagList = getTagList();
        assertTrue(tagList.stream().anyMatch(x -> x.getName().equals(tagName)));
    }

    @DisplayName("Проверка существования Тега")
    @TmsLink("1695658")
    @Test
    public void checkTagExist() {
        String tagName = "exist_tag_test_api";
        tagList.add(tagName);
        createTag(tagName);
        assertTrue(isTagExists(tagName));
        assertFalse(isTagExists("not_exist"));
    }

    @DisplayName("Удаление Тега")
    @TmsLink("1695680")
    @Test
    public void deleteTagTest() {
        String tagName = "delete_tag_test_api";
        createTag(tagName);
        String actionName = "action_delete_tag_test_api";
        JSONObject jsonObject = Action.builder()
                .name(actionName)
                .graphId(createGraph(StringUtils.getRandomStringApi(7)).getGraphId())
                .tagList(Collections.singletonList(tagName))
                .build()
                .toJson();
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        Action action = createAction(jsonObject).extractAs(Action.class);
        deleteActionById(action.getId());
        deleteTagByName(tagName);
        assertFalse(isTagExists(tagName));
    }

    @DisplayName("Копирование Тега")
    @TmsLink("1695688")
    @Test
    public void copyTagTest() {
        String tagName = "copy_tag_test_api";
        String cloneTag = tagName + "-clone";
        tagList.add(tagName);
        tagList.add(cloneTag);
        createTag(tagName);
        models.cloud.productCatalog.tag.Tag tag = copyTagByName(tagName);
        assertEquals(cloneTag, tag.getName());
    }

    @DisplayName("Получение списка объектов использующих Тег")
    @TmsLink("1696325")
    @Test
    public void getListObjectsUsedTagTest() {
        String tagName = "used_tag_test_api";
        Action action = createAction(Action.builder()
                .name("action_used_tag_test_api")
                .tagList(Collections.singletonList(tagName))
                .build());
        Product product = Product.builder()
                .name("product_used_tag_test_api")
                .title("AtTestApiProduct")
                .tagList(Collections.singletonList(tagName))
                .version("1.0.0")
                .build()
                .createObject();
        Graph graph = createGraph(Graph.builder()
                .name("graph_used_tag_test_api")
                .tagList(Collections.singletonList(tagName))
                .build());
        Template template = Template.builder()
                .name("template_used_tag_test_api")
                .tagList(Collections.singletonList(tagName))
                .build()
                .createObject();
        ItemVisualTemplate visualTemplate = ItemVisualTemplate.builder()
                .name("visual_template_used_tag_test_api")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(CompactTemplate.builder()
                        .name(new Name("name"))
                        .type(new Type("type", "label"))
                        .status(new Status("status"))
                        .build())
                .fullTemplate(FullTemplate.builder()
                        .type("type")
                        .value(Arrays.asList("value", "value2"))
                        .build())
                .tagList(Collections.singletonList(tagName))
                .isActive(false)
                .build()
                .createObject();
        Response resp = getTagUsedObjectsByName(tagName);
        assertEquals(action.getId(), resp.jsonPath().getString("Action[0].id"));
        assertEquals(1, resp.jsonPath().getList("Action").size());
        assertEquals(graph.getGraphId(), resp.jsonPath().getString("Graph[0].id"));
        assertEquals(1, resp.jsonPath().getList("Graph").size());
        assertEquals(product.getProductId(), resp.jsonPath().getString("Product[0].id"));
        assertEquals(1, resp.jsonPath().getList("Product").size());
        assertEquals(String.valueOf(template.getId()), resp.jsonPath().getString("Template[0].id"));
        assertEquals(1, resp.jsonPath().getList("Template").size());
        assertEquals(visualTemplate.getId(), resp.jsonPath().getString("ItemVisualisationTemplate[0].id"));
        assertEquals(1, resp.jsonPath().getList("ItemVisualisationTemplate").size());
    }

    @DisplayName("Негативный тест на удаление используемого тега")
    @TmsLink("1697698")
    @Test
    public void deleteUsedTagTest() {
        String tagName = "delete_used_tag_test_api";
        createTag(tagName);
        JSONObject jsonObject = Action.builder()
                .name("action_delete_tag_test_api")
                .graphId(createGraph(StringUtils.getRandomStringApi(7)).getGraphId())
                .tagList(Collections.singletonList(tagName))
                .build()
                .toJson();
        Action action = createAction(jsonObject).extractAs(Action.class);
        String errorMsg = deleteTagByNameResponse(tagName).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Нельзя удалить тег %s. Он используется в {'Action': [{'id': UUID('%s'), 'name': '%s', 'title': '%s'}]}",
                tagName, action.getId(), action.getName(), action.getTitle()), errorMsg);
        deleteActionById(action.getId());
        deleteTagByName(tagName);
        assertFalse(isTagExists(tagName));
    }

    @DisplayName("Негативный тест на создание Тега с существующим именем")
    @TmsLink("1697940")
    @Test
    public void createTagWithExistNameTest() {
        String tagName = "create_with_exist_name_tag";
        createTag(tagName);
        String errorMsg = createTagByNameResponse(tagName).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("\"name\": tag с таким name уже существует.", errorMsg);
        deleteTagByName(tagName);
    }

    @DisplayName("Негативный тест на создание Тега с невалидным именем")
    @TmsLink("1707621")
    @Test
    public void createTagInvalidNameTest() {
        String tagName = "Create_invalid_name_tag";
        String errorMsg = createTagByNameResponse(tagName).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Cannot instantiate (Tag) named (%s)", tagName), errorMsg);
    }

    @DisplayName("Негативный тест на создание Тега с пустым именем")
    @TmsLink("1710316")
    @Test
    public void createTagEmptyNameTest() {
        String tagName = " ";
        String errorMsg = createTagByNameResponse(tagName).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(("\"name\": Это поле не может быть пустым."), errorMsg);
    }
}
