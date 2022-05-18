package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class SaveGraphTest extends GraphBaseTest {

    @Test
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
}
