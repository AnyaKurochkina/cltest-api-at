package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

@Epic("Графы")
@Feature("Создание графа")
public class CreateGraphTest extends GraphBaseTest {
    private static final String name = "at_ui_create_graph_test";

    @Test
    @TmsLink("486578")
    @DisplayName("Создание графа")
    public void createGraphTest() {
        createGraph();
        createGraphWithoutRequiredParameters();
        createGraphWithNonUniqueName();
        checkGraphNameValidation();
    }

    @Step("Просмотр списка графов, создание, поиск")
    public void createGraph() {
        new IndexPage().goToGraphsPage()
                .checkGraphsListHeaders()
                .createGraph(TITLE, name, "action", DESCRIPTION, AUTHOR)
                .findGraphByName(name)
                .findGraphByName(TITLE)
                .findGraphByName(name.substring(1).toUpperCase())
                .findGraphByTitle(TITLE.substring(1).toUpperCase());
        deleteGraph(name);
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
                .checkGraphNameValidation(new String[] {"Test_name", "test name", "тест", "test_name$"});
    }
}
