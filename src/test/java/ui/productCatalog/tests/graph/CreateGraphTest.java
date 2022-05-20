package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class CreateGraphTest extends GraphBaseTest {
    private static final String name = "at_ui_create_graph_test";

    @Test
    @DisplayName("Просмотр списка графов, создание, поиск")
    public void createGraph() {
        new MainPage().goToGraphsPage()
                .checkGraphsListHeaders()
                .createGraph(TITLE, name, "action", DESCRIPTION, AUTHOR)
                .findGraphByName(name)
                .findGraphByName(TITLE)
                .findGraphByName(name.substring(1).toUpperCase())
                .findGraphByTitle(TITLE.substring(1).toUpperCase());
        deleteGraph(name);
    }

    @Test
    @DisplayName("Создание графа без заполнения обязательных полей")
    public void createGraphWithoutRequiredParameters() {
        new MainPage().goToGraphsPage()
                .checkCreateGraphDisabled("", NAME, "creating", DESCRIPTION, AUTHOR)
                .checkCreateGraphDisabled(TITLE, "", "creating", DESCRIPTION, AUTHOR)
                .checkCreateGraphDisabled(TITLE, NAME, "creating", DESCRIPTION, "");
    }

    @Test
    @DisplayName("Создание графа с неуникальным кодом графа")
    public void createGraphWithNonUniqueName() {
        new MainPage().goToGraphsPage()
                .checkCreateGraphDisabled(TITLE, NAME, "action", DESCRIPTION, AUTHOR);
    }
}
