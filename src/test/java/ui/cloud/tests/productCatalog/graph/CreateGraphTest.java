package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;

import java.util.UUID;

@Feature("Создание графа")
public class CreateGraphTest extends GraphBaseTest {
    private static final String name = UUID.randomUUID().toString();

    @Test
    @TmsLink("486578")
    @DisplayName("Создание графа")
    public void createGraphTest() {
        checkGraphNameValidation();
        createGraph();
        createGraphWithoutRequiredParameters();
        createGraphWithNonUniqueName();
    }

    @Step("Создание графа")
    public void createGraph() {
        Graph graph = Graph.builder()
                .name(name)
                .title(TITLE)
                .type(GraphType.ACTION.getValue())
                .version("1.0.0")
                .description(DESCRIPTION)
                .author(AUTHOR)
                .build();
        new IndexPage().goToGraphsPage()
                .createGraph(graph)
                .checkAttributes(graph);
        deleteGraphByApi(name);
    }

    @Step("Создание графа без заполнения обязательных полей")
    public void createGraphWithoutRequiredParameters() {
        new IndexPage().goToGraphsPage()
                .checkCreateGraphDisabled("", NAME, "creating", DESCRIPTION, AUTHOR)
                .checkCreateGraphDisabled(TITLE, "", "creating", DESCRIPTION, AUTHOR)
                .checkCreateGraphDisabled(TITLE, NAME, "creating", DESCRIPTION, "");
    }

    @Step("Создание графа с неуникальным кодом графа")
    public void createGraphWithNonUniqueName() {
        new IndexPage().goToGraphsPage()
                .checkCreateGraphDisabled(TITLE, NAME, "action", DESCRIPTION, AUTHOR);
    }

    @Step("Создание графа с недопустимым кодом")
    public void checkGraphNameValidation() {
        new IndexPage().goToGraphsPage()
                .checkGraphNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }
}
