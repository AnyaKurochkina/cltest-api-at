package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.DiffPage;

@Feature("Просмотр шаблона узлов")
public class ViewTemplateTest extends TemplateBaseTest {

    @Test
    @TmsLink("1206042")
    @DisplayName("Сравнение версий шаблона")
    public void compareVersionsTest() {
        new IndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .setRunQueue("test_1")
                .saveWithPatchVersion()
                .goToVersionComparisonTab();
        new DiffPage()
                .checkCurrentVersionInDiff("1.0.1")
                .compareWithVersion("1.0.0")
                .selectVersion("1.0.0")
                .checkCurrentVersionInDiff("1.0.0")
                .compareWithVersion("1.0.0")
                .compareWithVersion("1.0.1");
    }
}
