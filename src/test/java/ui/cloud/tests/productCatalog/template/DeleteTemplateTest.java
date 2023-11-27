package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.cloud.pages.productCatalog.template.TemplatePage;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;
import ui.elements.Alert;

import java.util.Collections;
import java.util.UUID;

import static models.cloud.productCatalog.graph.SourceType.TEMPLATE;
import static steps.productCatalog.GraphSteps.partialUpdateGraph;
import static ui.cloud.pages.productCatalog.template.TemplatesListPage.goToUsageButton;
import static ui.cloud.pages.productCatalog.template.TemplatesListPage.nameColumn;

@Feature("Удаление шаблона")
public class DeleteTemplateTest extends TemplateBaseTest {

    @AfterEach
    public void tearDown() {
    }

    @Test
    @TmsLink("504788")
    @DisplayName("Удаление шаблона из списка")
    public void deleteTemplateFromList() {
        new ControlPanelIndexPage().goToTemplatesPage()
                .deleteTemplate(NAME)
                .checkTemplateNotFound(NAME);
    }

    @Test
    @TmsLink("504726")
    @DisplayName("Удаление со страницы шаблона")
    public void deleteTemplateFromPage() {
        new ControlPanelIndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .openDeleteDialog()
                .submitAndDelete("Удаление выполнено успешно");
        new TemplatesListPage()
                .checkTemplateNotFound(NAME);
    }

    @Test
    @TmsLink("1416624")
    @DisplayName("Удаление шаблона, используемого в узле графе")
    public void deleteTemplateUsedInGraph() {
        String alertText = "Нельзя удалить шаблон, который используется другими объектами. " +
                "Отвяжите шаблон от объектов и повторите попытку";
        Graph graph = Graph.builder()
                .name(UUID.randomUUID().toString())
                .title("AT UI Graph")
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .description("Delete used template test")
                .author("QA")
                .build()
                .createObject();
        JSONObject graphItem = GraphItem.builder()
                .name("1")
                .description("1")
                .sourceId(String.valueOf(template.getId()))
                .sourceType(TEMPLATE.getValue())
                .build()
                .toJson();
        JSONObject graphJSON = new JSONObject().put("graph", Collections.singletonList(graphItem));
        partialUpdateGraph(graph.getGraphId(), graphJSON);
        new ControlPanelIndexPage().goToTemplatesPage()
                .findTemplateByValue(NAME, template);
        EntityListPage.delete(nameColumn, template.getName());
        new DeleteDialog().submitAndCheckNotDeletable(alertText);
        goToUsageButton.click();
        new Alert().close();
        TemplatePage page = new TemplatePage();
        page.checkTabIsSelected("Использование");
        page.getDeleteButton().click();
        new DeleteDialog().submitAndCheckNotDeletable(alertText);
        goToUsageButton.click();
        page.checkTabIsSelected("Использование");
    }
}
