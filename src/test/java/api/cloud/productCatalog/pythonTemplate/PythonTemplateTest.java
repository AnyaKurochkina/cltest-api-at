package api.cloud.productCatalog.pythonTemplate;


import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.pythonTemplate.PythonTemplate;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.PythonTemplateSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Python Template")
@DisabledIfEnv("prod")
public class PythonTemplateTest extends Tests {

    @DisplayName("Создание python_template в продуктовом каталоге")
    @TmsLink("SOUL-7705")
    @Test
    public void createPythonTemplateTest() {
        PythonTemplate expectedPythonTemplate = createPythonTemplateByName("create_python_template_api_test");
        PythonTemplate actualPythonTemplate = getPythonTemplateById(expectedPythonTemplate.getId());
        assertEquals(expectedPythonTemplate, actualPythonTemplate);
    }

    @DisplayName("Проверка существования python_template по имени")
    @TmsLink("SOUL-7706")
    @Test
    public void checkPythonTemplateExists() {
        String name = "is_python_template_exist_api_test";
        PythonTemplate pythonTemplate = createPythonTemplateByName(name);
        assertTrue(isPythonTemplateExists(name), "Python_template не существует");
        pythonTemplate.deleteObject();
        assertFalse(isPythonTemplateExists(name), "Python_template существует");
    }

    @DisplayName("Получение списка python_template")
    @TmsLink("SOUL-7707")
    @Test
    public void getPythonTemplateListTest() {
        String name = "get_python_template_list_api_test";
        createPythonTemplateByName("get_python_template_list_api_test");
        List<PythonTemplate> list = getPythonTemplateList();
        assertTrue(list.stream().anyMatch(x -> x.getName().equals(name)));
    }

    @DisplayName("Частичное обновление python_template без указания версии, версия должна инкрементироваться")
    @TmsLink("SOUL-7708")
    @Test
    public void patchPythonTemplateTest() {
        String name = "python_template_patch_test_api";
        PythonTemplate pythonTemplate = createPythonTemplateByName(name);
        String pythonCode = "some python code";
        partialUpdatePythonTemplate(pythonTemplate.getId(), new JSONObject().put("python_code", pythonCode));
        PythonTemplate actualPythonTemplate = getPythonTemplateById(pythonTemplate.getId());
        assertEquals("1.0.1", actualPythonTemplate.getVersion(), "Версии не совпадают");
        assertEquals(pythonCode, actualPythonTemplate.getPythonCode(), "Python_code не совпадает");
    }

    @DisplayName("Обновление python_template")
    @TmsLink("SOUL-7709")
    @Test
    public void putPythonTemplateTest() {
        String name = "python_template_put_test_api";
        PythonTemplate pythonTemplate = createPythonTemplateByName(name);
        pythonTemplate.setName("updated_python_template_put_test_api");
        pythonTemplate.setVersion("1.0.1");
        pythonTemplate.setLastVersion("1.0.1");
        PythonTemplate updatedPythonTemplate = updatePythonTemplate(pythonTemplate.getId(), pythonTemplate.toJson());
        assertEquals(pythonTemplate, updatedPythonTemplate);
    }

    @DisplayName("Копирование python_template по Id")
    @TmsLink("SOUL-7710")
    @Test
    public void copyPythonTemplateByIdTest() {
        String name = "clone_python_template_test_api";
        PythonTemplate pythonTemplate = createPythonTemplateByName(name);
        PythonTemplate clonePythonTemplate = copyPythonTemplateById(pythonTemplate.getId());
        String cloneName = clonePythonTemplate.getName();
        assertEquals(name + "-clone", cloneName);
        assertTrue(isPythonTemplateExists(cloneName), "Python_template не существует");
        deletePythonTemplate(clonePythonTemplate.getId());
        assertFalse(isPythonTemplateExists(cloneName), "Python_template существует");
    }

    @Test
    @DisplayName("Удаление python_template")
    @TmsLink("SOUL-7711")
    public void deletePythonTemplateTest() {
        String name = "delete_python_template_test_api";
        if (isPythonTemplateExists(name)) {
            deletePythonTemplateByName(name);
        }
        JSONObject json = PythonTemplate.builder()
                .name(name)
                .build()
                .init()
                .toJson();
        PythonTemplate pythonTemplate = createPythonTemplate(json);
        deletePythonTemplate(pythonTemplate.getId());
        assertFalse(isPythonTemplateExists(name));
    }

    @Test
    @DisplayName("Удаление python_template используемого в ноде графа")
    @TmsLink("SOUL-7712")
    public void deletePythonTemplateUsedInGraphNodeTest() {
        String name = "delete_python_template_used_in_graph_node_test_api";
        PythonTemplate pythonTemplate = createPythonTemplateByName(name);
        GraphItem graphItem = GraphItem.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api")
                .sourceType("python")
                .sourceId(pythonTemplate.getId())
                .build();
        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Collections.singletonList(graphItem))
                .build());
        String message = deletePythonTemplateResponse(pythonTemplate.getId()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(format("Нельзя удалить PythonTemplate: {}. Он используется:\nGraph: (name: {}, version: {})", name, graph.getName(), graph.getVersion()),
                message);
    }

    @Test
    @DisplayName("Получение списка объектов использующих python_template")
    @TmsLink("SOUL-7713")
    public void getUsedPythonTemplateObjectListTest() {
        String name = "get_object_list_used_python_template_test_api";
        PythonTemplate pythonTemplate = createPythonTemplateByName(name);
        GraphItem graphItem = GraphItem.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api")
                .sourceType("python")
                .sourceId(pythonTemplate.getId())
                .build();
        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Collections.singletonList(graphItem))
                .build());
        Response response = getObjectListUsedPythonTemplate(pythonTemplate.getId());
        assertEquals(1, response.jsonPath().getList("").size());
        assertEquals(graph.getName(), response.jsonPath().getString("[0].name"));
        assertEquals(graphItem.getName(), response.jsonPath().getString("[0].nodes[0].node_name"));
    }
}
