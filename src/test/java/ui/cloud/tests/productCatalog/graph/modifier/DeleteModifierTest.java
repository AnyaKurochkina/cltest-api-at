package ui.cloud.tests.productCatalog.graph.modifier;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.uiModels.GraphModifier;

@Epic("Графы")
@Feature("Удаление модификатора")
public class DeleteModifierTest extends GraphBaseTest {

    @Test
    @TmsLink("688694")
    @DisplayName("Удаление модификатора")
    public void deleteModifier() {
        GraphModifier modifier = new GraphModifier("test-modifier");
        String modifierData = "\"devTitle\"";
        modifier.setPath("title");
        modifier.setModifierData(modifierData);
        modifier.setModifierDataSubstring(modifierData);
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToOrderParamsTab()
                .setJSONSchemaAndSave("{\"title\":\"defaultTitle\"}")
                .goToModifiersTab()
                .addModifierAndSave(modifier)
                .deleteModifier(modifier)
                .checkModifiedJSONSchemaContains("\"defaultTitle\"");
    }
}
