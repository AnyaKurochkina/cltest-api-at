package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.uiModels.Graph;

@Epic("Графы")
@Feature("Сохранение графа")
public class SaveGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("487709")
    @DisplayName("Сохранение графа с указанием версии вручную")
    public void saveGraphWithManualVersion() {
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999");
    }

    @Test
    @TmsLink("529313")
    @DisplayName("Сохранение графа с указанием некорректной версии")
    public void saveGraphWithIncorrectVersion() {
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .editGraph(new Graph(NAME, TITLE, GraphType.CREATING, "1.0.0", "description", "QA-1"))
                .saveGraphWithPatchVersion()
                .checkGraphVersion("1.0.1")
                .trySaveGraphWithIncorrectVersion("1.0.0")
                .trySaveGraphWithIncorrectVersion("1.0.1");
    }

    @Test
    @TmsLinks({@TmsLink("487621"), @TmsLink("600394")})
    @DisplayName("Проверка изменений и лимита патч-версий")
    public void checkPatchVersionLimit() {
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999")
                .editGraph(new Graph(NAME, TITLE, GraphType.CREATING, "1.0.999", "description", "QA-1"))
                .saveGraphWithPatchVersion()
                .checkGraphVersion("1.1.0")
                .saveGraphWithManualVersion("1.999.999")
                .checkGraphVersion("1.999.999")
                .editGraph(new Graph(NAME, TITLE, GraphType.CREATING, "1.999.999", "description", "QA-2"))
                .saveGraphWithPatchVersion()
                .checkGraphVersion("2.0.0")
                .saveGraphWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("600752")
    @DisplayName("Проверка изменений и лимита версий, указанных вручную")
    public void checkManualVersionLimit() {
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999")
                .editGraph(new Graph(NAME, TITLE, GraphType.CREATING, "1.0.999", "description", "QA-1"))
                .checkAndSaveNextManualVersion("1.1.0")
                .checkGraphVersion("1.1.0")
                .saveGraphWithManualVersion("1.999.999")
                .checkGraphVersion("1.999.999")
                .editGraph(new Graph(NAME, TITLE, GraphType.CREATING, "1.999.999", "description", "QA-2"))
                .checkAndSaveNextManualVersion("2.0.0")
                .checkGraphVersion("2.0.0")
                .saveGraphWithManualVersion("999.999.999")
                .checkVersionLimit();
    }
}
