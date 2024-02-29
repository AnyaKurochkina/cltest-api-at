package ui.cloud.tests.productCatalog.graph;

import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.enums.AuditChangeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.MeccanoAuditPage;
import ui.cloud.pages.productCatalog.graph.GraphPage;
import ui.elements.Table;
import ui.t1.tests.audit.AuditPeriod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Feature("Просмотр истории изменений")
public class GraphAuditTest extends GraphBaseTest {

    private static GlobalUser user;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    @TmsLink("853374")
    @DisplayName("Просмотр аудита по графу")
    public void viewGraphAuditTest() {
        checkAuditRecord();
        checkFilterByDate();
        checkUserFilter();
        checkDiff();
    }

    @Step("Проверка записи аудита")
    private void checkAuditRecord() {
        user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToAuditTab()
                .checkHeaders()
                .checkFirstRecord(user.getEmail(), AuditChangeType.CREATE, "1.0.0")
                .checkFirstRecordDetails("1.0.0", AuditChangeType.CREATE, user.getEmail(), graph.getGraphId());
        new GraphPage()
                .setAuthor("QA-1")
                .saveGraphWithPatchVersion()
                .goToAuditTab()
                .checkFirstRecord(user.getEmail(), AuditChangeType.UPDATE, "1.0.1")
                .checkFirstRecordDetails("1.0.1", AuditChangeType.UPDATE, user.getEmail(), graph.getGraphId())
                .checkCopyToClipBoard(graph.getGraphId());
    }

    @Step("Проверка фильтрации по диапазону дат")
    private void checkFilterByDate() {
        new MeccanoAuditPage()
                .selectPeriod(AuditPeriod.ONE_DAY)
                .checkRecordsFound()
                .selectPeriod(AuditPeriod.LAST_HOUR)
                .checkRecordsFound()
                .setFilterByDate(LocalDateTime.now().plusDays(1).format(formatter),
                        LocalDateTime.now().plusDays(2).format(formatter))
                .checkRecordsNotFound();
    }

    @Step("Проверка фильтрации по дополнительным фильтрам")
    private void checkUserFilter() {
        new MeccanoAuditPage()
                .setUserFilterAndApply("test_user")
                .checkRecordsNotFound()
                .setUserFilterAndApply(user.getEmail().substring(1, 8))
                .checkRecordsFound()
                .clearUserFilter()
                .checkRecordsFound();
    }

    @Step("Проверка диффа записи аудита")
    private void checkDiff() {
        new Table("Пользователь").getRow(0).get().click();
        new MeccanoAuditPage()
                .checkDiffContains(Arrays.asList("1.0.0", "QA"), Arrays.asList("1.0.1", "QA-1"));
    }
}
