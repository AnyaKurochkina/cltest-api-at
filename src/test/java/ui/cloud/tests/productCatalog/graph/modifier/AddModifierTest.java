package ui.cloud.tests.productCatalog.graph.modifier;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.graph.GraphModifiersPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.uiModels.GraphModifier;

@Epic("Графы")
@Feature("Добавление модификатора")
public class AddModifierTest extends GraphBaseTest {

    @Test
    @TmsLink("688238")
    @DisplayName("Добавление модификатора JSONSchema со способом replace")
    public void addJSONSchemaModifierWithReplaceType() {
        GraphModifier modifier = new GraphModifier("test-modifier");
        String modifierData = "\"devTitle\"";
        modifier.setPath("title");
        modifier.setModifierData(modifierData);
        modifier.setModifierDataSubstring(modifierData);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToOrderParamsTab()
                .setJSONSchemaAndSave("{\"title\":\"defaultTitle\"}")
                .goToModifiersTab()
                .addModifierAndSave(modifier)
                .checkModifierAttributes(modifier)
                .checkModifiedJSONSchemaContains(modifierData)
                .goToOrderParamsTab()
                .checkJSONSchemaContains("\"defaultTitle\"");
    }

    @Test
    @TmsLink("1047017")
    @DisplayName("Добавление нескольких модификаторов со способом replace")
    public void addMultipleModifiersWithReplaceType() {
        GraphModifier modifier1 = new GraphModifier("test_modifier_1");
        String modifierData = "\"devTitle_1\"";
        modifier1.setPath("title");
        modifier1.setModifierData(modifierData);
        modifier1.setModifierDataSubstring(modifierData);
        GraphModifier modifier2 = new GraphModifier("test_modifier_2");
        modifierData = "\"devTitle_2\"";
        modifier2.setPath("title");
        modifier2.setModifierData(modifierData);
        modifier2.setModifierDataSubstring(modifierData);
        modifier2.setNumber("2");
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToOrderParamsTab()
                .setJSONSchemaAndSave("{\"title\":\"defaultTitle\"}")
                .goToModifiersTab()
                .addModifierAndSave(modifier1)
                .addModifierAndSave(modifier2)
                .checkModifierAttributes(modifier2)
                .checkModifiedJSONSchemaContains(modifier2.getModifierData())
                .deleteModifier(modifier2.getName())
                .checkModifiedJSONSchemaContains(modifier1.getModifierData());
    }

    @Test
    @TmsLink("1038914")
    @DisplayName("Добавление модификатора JSONSchema со способом update")
    public void addModifierWithUpdateType() {
        GraphModifier modifier = new GraphModifier("test-modifier");
        modifier.setType("update");
        String modifierData = "{\"type\": \"object\"}";
        modifier.setModifierData(modifierData);
        String modifierDataSubstring = modifierData.substring(1, modifierData.indexOf(":"));
        modifier.setModifierDataSubstring(modifierDataSubstring);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToOrderParamsTab()
                .setJSONSchemaAndSave("{\"title\":\"defaultTitle\"}")
                .goToModifiersTab()
                .addModifierAndSave(modifier)
                .checkModifierAttributes(modifier)
                .checkModifiedJSONSchemaContains(modifierDataSubstring);
    }

    @Test
    @TmsLink("1042524")
    @DisplayName("Добавление модификатора UISchema со способом update")
    public void addUISchemaModifierWithUpdateType() {
        GraphModifier modifier = new GraphModifier("test_modifier");
        modifier.setType("update");
        modifier.setPath("ui:order");
        modifier.setSchema("ui_schema");
        String modifierData = "[\"data_center\"]";
        modifier.setModifierData(modifierData);
        String modifierDataSubstring = modifierData.substring(1, modifierData.length() - 1);
        modifier.setModifierDataSubstring(modifierDataSubstring);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToOrderParamsTab()
                .setUISchemaAndSave("{\"ui:order\":[\"title\", \"platform\"]}")
                .goToModifiersTab()
                .addModifierAndSave(modifier)
                .checkModifierAttributes(modifier)
                .checkModifiedUISchemaContains(modifierDataSubstring);
    }

    @Test
    @TmsLink("1042532")
    @DisplayName("Добавление модификатора StaticData со способом replace")
    public void addStaticDataModifierWithReplaceType() {
        GraphModifier modifier = new GraphModifier("test_modifier");
        String modifierData = "99";
        modifier.setSchema("static_data");
        modifier.setPath("quantity");
        modifier.setModifierData(modifierData);
        modifier.setModifierDataSubstring(modifierData);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .setStaticData("{\"quantity\": 2}")
                .goToModifiersTab()
                .addModifierAndSave(modifier)
                .checkModifierAttributes(modifier)
                .checkModifiedStaticDataContains(modifierData);
    }

    @Test
    @TmsLink("1046291")
    @DisplayName("Добавление модификатора с некорректными параметрами")
    public void createModifierWithIncorrectParameters() {
        GraphModifier modifier = new GraphModifier("test-modifier");
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToModifiersTab()
                .addModifierAndSave(modifier);

        new GraphModifiersPage()
                .checkModifierNameValidation(new String[]{"Modifier", "test modifier", "модификатор", "modifier$"})
                .checkModifierNumberValidation()
                .checkNonUniqueModifierName("test-modifier")
                .checkNonUniqueModifierNumber("1");
    }
}
