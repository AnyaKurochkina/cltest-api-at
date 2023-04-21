package ui.cloud.tests.productCatalog.graph.modifier;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.graph.GraphModifiersPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.models.GraphModifier;

@Feature("Редактирование модификатора")
public class EditModifierTest extends GraphBaseTest {

    @Test
    @TmsLink("1042198")
    @DisplayName("Редактирование модификатора")
    public void editModifier() {
        GraphModifier modifier = new GraphModifier("test-modifier");
        String modifierData = "\"devTitle\"";
        modifier.setPath("title");
        modifier.setModifierData(modifierData);
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToOrderParamsTab()
                .setJSONSchemaAndSave("{\"title\":\"defaultTitle\"}")
                .goToModifiersTab()
                .addModifierAndSave(modifier)
                .checkModifiedJSONSchemaContains(modifierData);
        modifier.setEnvs(new String[]{"test"});
        modifierData = "\"testTitle\"";
        modifier.setModifierData(modifierData);
        new GraphModifiersPage()
                .editModifierAndSave(modifier)
                .setEnvType("test")
                .checkModifiedJSONSchemaContains(modifierData)
                .setEnv("IFT")
                .checkModifiedJSONSchemaContains(modifierData)
                .setEnvType("dev")
                .checkModifiedJSONSchemaContains("\"defaultTitle\"");
    }
}
