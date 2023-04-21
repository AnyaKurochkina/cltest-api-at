package ui.cloud.tests.productCatalog.graph;

import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.graph.GraphPage;

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
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToAuditTab()
                .checkFirstRecord(LocalDateTime.now().format(formatter), user.getUsername(), "create", graphsObject, "201", "создан")
                .checkFirstRecordDetails(graph.getGraphId(), graphsObject, noValue, noValue);
        new GraphPage().setAuthor("QA-1")
                .saveGraphWithPatchVersion()
                .goToAuditTab()
                .checkFirstRecord(LocalDateTime.now().format(formatter), user.getUsername(), "modify", graphsObject, "200", "ок")
                .checkFirstRecordDetails(graph.getGraphId(), graph.getGraphId(), noValue, noValue)
                .showRequestAndResponse()
                .checkFirstRecordDetails(graph.getGraphId(), graph.getGraphId(), "1.0.0", graph.getGraphId())
                .checkCopyToClipboard(graph.getTitle(), graph.getGraphId())
                .checkResponseFullViewContains(graph.getName(), graph.getGraphId());
    }

    @Step("Проверка фильтрации по диапазону дат")
    private void checkFilterByDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        new AuditPage().setFilterByDate(LocalDateTime.now().plusDays(1).format(formatter),
                        LocalDateTime.now().plusDays(2).format(formatter))
                .checkRecordsNotFoundV2()
                .selectPeriod("день")
                .checkRecordWithOperationTypeFound("modify");
    }

    @Step("Проверка фильтрации по дополнительным фильтрам")
    private void checkAdditionalFilters() {
        new AuditPage().setOperationTypeFilterAndApply("delete")
                .checkRecordsNotFoundV2()
                .clearAdditionalFilters()
                .setUserFilter("test_user")
                .applyAdditionalFilters()
                .checkRecordsNotFoundV2()
                .clearAdditionalFilters()
                .setStatusCodeFilter("500")
                .applyAdditionalFilters()
                .checkRecordsNotFoundV2()
                .clearAdditionalFilters()
                .setOperationTypeFilterAndApply("create")
                .setUserFilter(user.getUsername())
                .applyAdditionalFilters()
                .setStatusCodeFilter("201")
                .applyAdditionalFilters()
                .checkRecordWithOperationTypeFound("create");
    }
}
