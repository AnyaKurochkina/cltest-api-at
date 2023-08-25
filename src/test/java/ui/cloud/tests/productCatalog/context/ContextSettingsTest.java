package ui.cloud.tests.productCatalog.context;

import com.codeborne.selenide.Condition;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.ContextSettingsPage;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;

public class ContextSettingsTest extends ProductCatalogUITest {

    private final String defaultDevProject = "proj-sandbox-dev";
    private final String defaultTestProject = "proj-sandbox-ift";
    private final String defaultProdProject = "proj-sandbox-prom";
    private final Project devProject = Project.builder().projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
            .build().createObject();
    private final Project testProject = Project.builder().projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("TEST"))
            .build().createObject();

    @Test
    @TmsLink("1257138")
    @DisplayName("Восстановить проекты по умолчанию")
    public void restoreDefaultProjects() {
        ContextSettingsPage page = new ControlPanelIndexPage().goToContextSettingsPage();
        page.getDevProjectInput().setValue(devProject.getId());
        page.getTestProjectInput().setValue(testProject.getId());
        page.save();
        page.getRestoreDefaultProjectsButton().click();
        page.getDevProjectInput().getInput().shouldHave(Condition.exactValue(defaultDevProject));
        page.getTestProjectInput().getInput().shouldHave(Condition.exactValue(defaultTestProject));
        page.getProdProjectInput().getInput().shouldHave(Condition.exactValue(defaultProdProject));
        page.save();
    }

    @Test
    @TmsLink("1257620")
    @DisplayName("Сбросить введенные данные")
    public void resetEnteredData() {
        ContextSettingsPage page = new ControlPanelIndexPage().goToContextSettingsPage();
        page.getDevProjectInput().setValue("new_project");
        page.getTestProjectInput().setValue("new_project");
        page.getProdProjectInput().setValue("new_project");
        page.getResetEnteredDataButton().click();
        page.getDevProjectInput().getInput().shouldHave(Condition.exactValue(defaultDevProject));
        page.getTestProjectInput().getInput().shouldHave(Condition.exactValue(defaultTestProject));
        page.getProdProjectInput().getInput().shouldHave(Condition.exactValue(defaultProdProject));
    }
}
