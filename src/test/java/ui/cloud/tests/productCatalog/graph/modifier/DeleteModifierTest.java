package ui.cloud.tests.productCatalog.graph.modifier;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Env;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.Modification;
import models.cloud.productCatalog.graph.RootPath;
import models.cloud.productCatalog.graph.UpdateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.GraphSteps;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;

import java.util.Collections;

@Feature("Удаление модификатора")
public class DeleteModifierTest extends GraphBaseTest {
    Modification modifier;

    @Override
    @BeforeEach
    @DisplayName("Создание графов через API")
    public void setUpForGraphsTest() {
        modifier = Modification.builder()
                .name("test_modifier")
                .envs(Collections.singletonList(Env.DEV))
                .order(1)
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .build();

        GraphSteps.createGraph(Graph.builder()
                .name(NAME)
                .title(TITLE)
                .version("1.0.0")
                .type("creating")
                .description(DESCRIPTION)
                .author(AUTHOR)
                .modifications(Collections.singletonList(modifier))
                .build());
    }

    @Test
    @TmsLink("688694")
    @DisplayName("Удаление модификатора")
    public void deleteModifier() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToOrderParamsTab()
                .setJSONSchemaAndSave("{\"title\":\"defaultTitle\"}")
                .goToModifiersTab()
                .deleteModifier(modifier.getName())
                .checkModifiedJSONSchemaContains("\"defaultTitle\"");
    }
}
