package ui.cloud.tests.audit;

import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.graph.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.ContextPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.extesions.ConfigExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static steps.productCatalog.GraphSteps.*;

@Feature("Просмотр аудита на портале")
@ExtendWith(ConfigExtension.class)
public class PortalAuditTest extends Tests {

    private final String projectsObject = "projects";
    private final String noValue = AuditPage.NO_VALUE;
    private final GlobalUser cloudAdmin = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
    private final GlobalUser pcAdmin = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final String okCode = "200";
    private final String okStatus = "ок";
    private final String createType = "create";
    private final Project project = Project.builder().projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
            .build().createObject();
    private String graphName;
    private Graph graph;
    private Graph graphCopy;

    @BeforeEach
    public void setUp() {
        graphName = UUID.randomUUID().toString();
        graph = Graph.builder()
                .name(graphName)
                .title("AT UI Graph")
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .description("for audit test")
                .author("AT UI")
                .build()
                .createObject();

        copyGraphByIdInContext(graph.getGraphId(), project.getId());
        graphCopy = getGraphByNameFilter(graph.getName() + "-clone");
        deleteGraphByIdInContext(graphCopy.getGraphId(), project.getId());

        new LoginPage(project.getId()).signIn(cloudAdmin.getRole());
    }

    @Test
    @TmsLink("759370")
    @DisplayName("Просмотр аудита за период")
    public void checkFilterByDate() {
        AuditPage page = new IndexPage().goToPortalAuditPage();
        Assertions.assertEquals("последний 1 час", page.getPeriodSelect().getValue());
        page.getBeginDateInput().getInput().shouldBe(Condition.disabled);
        page.getEndDateInput().getInput().shouldBe(Condition.disabled);
        page.checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                projectsObject, okCode, okStatus);
        page.selectPeriod("последние 6 часов")
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                        projectsObject, okCode, okStatus)
                .setFilterByDate(LocalDateTime.now().minusDays(1).format(formatter),
                        LocalDateTime.now().format(formatter))
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                        projectsObject, okCode, okStatus);
    }

    @Test
    @TmsLink("762461")
    @DisplayName("Сортировка таблицы аудита")
    public void checkAuditTable() {
        new IndexPage().goToPortalAuditPage()
                .checkHeaders()
                .checkSortingByDate();
    }

    @Test
    @TmsLink("811980")
    @DisplayName("Фильтрация таблицы аудита")
    public void checkFilters() {
        new IndexPage().goToPortalAuditPage()
                .setOperationTypeFilterAndApply("create")
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                        projectsObject, okCode, okStatus)
                .setUserFilter("test_user")
                .applyAdditionalFilters()
                .checkUserNotFound(pcAdmin.getEmail())
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(okCode)
                .applyAdditionalFilters()
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                        projectsObject, okCode, okStatus);
    }

    @Test
    @TmsLink("762451")
    @DisplayName("Просмотр записи аудита")
    public void checkAuditRecordDetails() {
        new IndexPage().goToPortalAuditPage()
                .setOperationTypeFilterAndApply(createType)
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(okCode)
                .applyAdditionalFilters()
                .checkFirstRecord(LocalDateTime.now().format(formatter), pcAdmin.getUsername(), createType, projectsObject,
                        okCode, okStatus)
                .checkRecordDetailsByContextId(graphCopy.getGraphId(), projectsObject, noValue, noValue)
                .showRequestAndResponse()
                .checkRecordDetailsByContextId(graphCopy.getGraphId(), projectsObject, noValue, graphCopy.getGraphId())
                .checkCopyToClipboard(graphCopy.getTitle(), graphCopy.getGraphId())
                .checkResponseFullViewContains(graphCopy.getName(), graphCopy.getGraphId());
    }

    @Test
    @TmsLink("1458196")
    @DisplayName("Просмотр записи аудита в родительском контексте")
    public void checkAuditInParentContext() {
        new IndexPage().goToPortalAuditPage()
                .setOperationTypeFilterAndApply(createType)
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(okCode)
                .applyAdditionalFilters()
                .checkRecordDetailsByContextId(graphCopy.getGraphId(), projectsObject, noValue, noValue);
        new ContextPage().openUserContext().setContext("VTB-VTB.Cloud-QA-AT-DEV");
        new AuditPage()
                .setUserFilter(pcAdmin.getEmail())
                .applyAdditionalFilters()
                .checkRecordDetailsByContextId(graphCopy.getGraphId(), projectsObject, noValue, noValue);
    }
}
