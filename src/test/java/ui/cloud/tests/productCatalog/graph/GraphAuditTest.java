package ui.cloud.tests.productCatalog.graph;

import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.graph.Graph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Feature("Просмотр истории изменений")
public class GraphAuditTest extends GraphBaseTest {

    private static final String graphsObject = "graphs";
    private static final String noValue = "—";
    private static GlobalUser user;

    @Test
    @TmsLink("853374")
    @DisplayName("Просмотр аудита по графу")
    public void viewGraphAuditTest() {
        checkAuditRecord();
        checkFilterByDate();
        checkAdditionalFilters();
    }

    @Step("Проверка записи аудита")
    private void checkAuditRecord() {
        user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToAuditTab()
                .checkFirstRecord(LocalDateTime.now().format(formatter), user.getUsername(), "create", graphsObject, "201", "создан")
                .checkFirstRecordDetails(graph.getGraphId(), graphsObject, noValue, noValue)
                .setAuthor("QA-1")
                .saveGraphWithPatchVersion()
                .goToAuditTab()
                .checkFirstRecord(LocalDateTime.now().format(formatter), user.getUsername(), "modify", graphsObject, "200", "ок")
                .checkFirstRecordDetails(graph.getGraphId(), graph.getGraphId(), noValue, noValue)
                .showRequestAndResponse()
                .checkFirstRecordDetails(graph.getGraphId(), graph.getGraphId(), "1.0.0", graph.getGraphId())
                .checkCopyToClipboard(graph.getTitle())
                .checkResponseFullViewContains(graph.getName());
    }

    @Step("Проверка фильтрации по диапазону дат")
    private void checkFilterByDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        new AuditPage().setFilterByDate(LocalDateTime.now().plusDays(1).format(formatter),
                        LocalDateTime.now().plusDays(2).format(formatter))
                .checkRecordsNotFound()
                .selectPeriod("день")
                .checkRecordWithOperationTypeFound("modify");
    }

    @Step("Проверка фильтрации по дополнительным фильтрам")
    private void checkAdditionalFilters() {
        new AuditPage().setOperationTypeFilterAndApply("delete")
                .checkRecordsNotFound()
                .clearAdditionalFilters()
                .setUserFilterAndApply("test_user")
                .checkRecordsNotFound()
                .clearAdditionalFilters()
                .setStatusCodeFilterAndApply("500")
                .checkRecordsNotFound()
                .clearAdditionalFilters()
                .setOperationTypeFilterAndApply("create")
                .setUserFilterAndApply(user.getUsername())
                .setStatusCodeFilterAndApply("201")
                .checkRecordWithOperationTypeFound("create");
    }
}
