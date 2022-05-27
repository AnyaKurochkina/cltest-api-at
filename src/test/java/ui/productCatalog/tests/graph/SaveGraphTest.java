package ui.productCatalog.tests.graph;

import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class SaveGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("487709")
    @DisplayName("Сохранение графа с указанием версии вручную")
    public void saveGraphWithManualVersion() {
        new MainPage().goToGraphsPage()
                .openGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .editGraph("edited description")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999");
    }

    @Test
    @TmsLink("529313")
    @DisplayName("Сохранение графа с указанием некорректной версии")
    public void saveGraphWithIncorrectVersion() {
        new MainPage().goToGraphsPage()
                .openGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .editGraph("edited description")
                .saveGraphWithPatchVersion()
                .checkGraphVersion("1.0.1")
                .trySaveGraphWithIncorrectVersion("1.0.0")
                .trySaveGraphWithIncorrectVersion("1.0.1");
    }

    @Test
    @TmsLinks({@TmsLink("487621"),@TmsLink("600394")})
    @DisplayName("Проверка изменений и лимита патч-версий")
    public void checkPatchVersionLimit() {
        new MainPage().goToGraphsPage()
                .openGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999")
                .editGraph("edited description-1")
                .saveGraphWithPatchVersion()
                .checkGraphVersion("1.1.0")
                .saveGraphWithManualVersion("1.999.999")
                .checkGraphVersion("1.999.999")
                .editGraph("edited description-2")
                .saveGraphWithPatchVersion()
                .checkGraphVersion("2.0.0")
                .saveGraphWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("600752")
    @DisplayName("Проверка изменений и лимита версий, указанных вручную")
    public void checkManualVersionLimit() {
        new MainPage().goToGraphsPage()
                .openGraphPage(NAME)
                .checkGraphVersion("1.0.0")
                .saveGraphWithManualVersion("1.0.999")
                .checkGraphVersion("1.0.999")
                .editGraph("edited description-1")
                .checkAndSaveNextManualVersion("1.1.0")
                .checkGraphVersion("1.1.0")
                .saveGraphWithManualVersion("1.999.999")
                .checkGraphVersion("1.999.999")
                .editGraph("edited description-2")
                .checkAndSaveNextManualVersion("2.0.0")
                .checkGraphVersion("2.0.0")
                .saveGraphWithManualVersion("999.999.999")
                .checkVersionLimit();
    }
}
