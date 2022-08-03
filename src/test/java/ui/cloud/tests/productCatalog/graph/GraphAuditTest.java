package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.uiModels.Graph;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Epic("Графы")
@Feature("Сравнение версий графа")
public class GraphAuditTest extends GraphBaseTest {

    private static final String graphsObject = "graphs";
    private static final String user = "portal_admin_at";
    private static final String noValue = "—";

    @Test
    @DisplayName("Просмотр аудита по графу")
    public void viewGraphAuditTest() {
        checkAuditRecord();
    }

    public void checkAuditRecord() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToAuditTab()
                .checkFirstRecord(formatter.format(LocalDateTime.now()), user, "create", graphsObject, "201", "создан")
                .checkFirstRecordDetails(graph.getGraphId(), graphsObject, noValue, noValue)
                .editGraph(new Graph(NAME, TITLE, GraphType.CREATING, "1.0.0", "description", "QA-1"))
                .saveGraphWithPatchVersion()
                .goToAuditTab()
                .checkFirstRecord(formatter.format(LocalDateTime.now()), user, "modify", graphsObject, "200", "ок")
                .checkFirstRecordDetails(graph.getGraphId(), graph.getGraphId(), noValue, noValue)
                .showRequestAndResponse()
                .checkFirstRecordDetails(graph.getGraphId(), graph.getGraphId(), "1.0.0", graph.getGraphId())
                .checkCopyToClipboard();
    }
}
