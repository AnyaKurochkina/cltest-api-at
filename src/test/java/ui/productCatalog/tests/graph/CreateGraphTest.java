package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class CreateGraphTest extends GraphBaseTest {

    @Test
    @DisplayName("Просмотр списка графов, создание, поиск")
    public void createGraph() {
        new MainPage().goToGraphsPage()
                .checkGraphsListHeaders()
                .createGraph(TITLE, NAME, "action", DESCRIPTION, AUTHOR)
                .findGraphByName(NAME)
                .findGraphByName(TITLE)
                .findGraphByName(NAME.substring(1).toUpperCase())
                .findGraphByTitle(TITLE.substring(1).toUpperCase());
    }

    @Test
    @DisplayName("Создание графа без заполнения обязательных полей")
    public void createGraphWithoutRequiredParameters() {
        new MainPage().goToGraphsPage()
                .checkCreateGraphDisabled("", NAME, "creating", DESCRIPTION, AUTHOR)
                .checkCreateGraphDisabled(TITLE, "", "creating", DESCRIPTION, AUTHOR)
                .checkCreateGraphDisabled(TITLE, NAME, "creating", DESCRIPTION, "")
                .createGraph(TITLE, NAME, "action", DESCRIPTION, AUTHOR);
    }

    @Test
    @DisplayName("Создание графа с неуникальным кодом графа")
    public void createGraphWithNonUniqueName() {
        new MainPage().goToGraphsPage()
                .createGraph(TITLE, NAME, "action", DESCRIPTION, AUTHOR)
                .checkCreateGraphDisabled(TITLE, NAME, "action", DESCRIPTION, AUTHOR);
    }
}