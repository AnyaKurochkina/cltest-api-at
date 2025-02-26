package ui.cloud.tests.audit;

import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.authorizer.Folder;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.graph.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.ContextPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.extesions.ConfigExtension;
import ui.t1.tests.audit.AuditPeriod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static steps.productCatalog.GraphSteps.*;
import static steps.resourceManager.ResourceManagerSteps.getFolderById;
import static steps.resourceManager.ResourceManagerSteps.getProjectJsonPath;

@Feature("Просмотр аудита на портале")
@ExtendWith(ConfigExtension.class)
public class PortalAuditTest extends Tests {

    private final String projectsObject = "projects";
    private final String noValue = AuditPage.NO_VALUE;
    private final GlobalUser cloudAdmin = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
    private final GlobalUser pcAdmin = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final String createdCode = "201";
    private final String createdStatus = "создан";
    private final String copyType = "copy";
    private final Project project = Project.builder().projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
            .build().createObject();
    private String graphName;
    private Graph graph;
    private Graph graphCopy;

    @BeforeEach
    public void setUp() {
        graphName = UUID.randomUUID().toString();
        graph = createGraph(Graph.builder()
                .name(graphName)
                .title("AT UI Graph")
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .description("for audit test")
                .author("AT UI")
                .build());

        copyGraphByIdInContext(graph.getGraphId(), project.getId());
        graphCopy = getGraphByNameFilter(graph.getName() + "-clone");
        deleteGraphByIdInContext(graphCopy.getGraphId(), project.getId());

        new CloudLoginPage(project.getId()).signIn(cloudAdmin.getRole());
    }

    @Test
    @TmsLink("SOUL-2321")
    @DisplayName("Просмотр аудита за период")
    public void checkFilterByDate() {
        AuditPage page = new IndexPage().goToPortalAuditPage();
        Assertions.assertEquals("последний 1 час", page.getPeriodSelect().getValue());
        page.getBeginDateInput().getInput().shouldBe(Condition.disabled);
        page.getEndDateInput().getInput().shouldBe(Condition.disabled);
        page.checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), copyType,
                projectsObject, createdCode, createdStatus);
        page.selectPeriod(AuditPeriod.LAST_6_HOURS)
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), copyType,
                        projectsObject, createdCode, createdStatus)
                .setFilterByDate(LocalDateTime.now().minusDays(1).format(formatter),
                        LocalDateTime.now().format(formatter))
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), copyType,
                        projectsObject, createdCode, createdStatus);
    }

    @Test
    @TmsLink("SOUL-2324")
    @DisplayName("Сортировка таблицы аудита")
    public void checkAuditTable() {
        new IndexPage().goToPortalAuditPage()
                .checkHeaders()
                .checkSortingByDate();
    }

    @Test
    @TmsLink("SOUL-2325")
    @DisplayName("Фильтрация таблицы аудита")
    public void checkFilters() {
        new IndexPage().goToPortalAuditPage()
                .setOperationTypeFilterAndApply(copyType)
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), copyType,
                        projectsObject, createdCode, createdStatus)
                .setUserFilter("test_user")
                .applyAdditionalFilters()
                .checkRecordsNotFoundV2()
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(createdCode)
                .applyAdditionalFilters()
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), copyType,
                        projectsObject, createdCode, createdStatus);
    }

    @Test
    @TmsLink("SOUL-2323")
    @DisplayName("Просмотр записи аудита")
    public void checkAuditRecordDetails() {
        new IndexPage().goToPortalAuditPage()
                .setOperationTypeFilterAndApply(copyType)
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(createdCode)
                .applyAdditionalFilters()
                .checkFirstRecord(LocalDateTime.now().format(formatter), pcAdmin.getUsername(), copyType, projectsObject,
                        createdCode, createdStatus)
                .showRequestAndResponse()
                .checkRecordDetailsByResponse(project.getId(), projectsObject, noValue, graphCopy.getGraphId())
                .checkCopyToClipboard(graphCopy.getTitle(), project.getId())
                .checkResponseFullViewContains(graphCopy.getName(), project.getId());
    }

    @Test
    @TmsLink("SOUL-2326")
    @DisplayName("Просмотр записи аудита в родительском контексте")
    public void checkAuditInParentContext() {
        new IndexPage().goToPortalAuditPage();
        JsonPath projectJson = getProjectJsonPath(project.getId());
        String folderId = projectJson.getString("data.folder");
        Folder folder = getFolderById(folderId);
        new ContextPage().openUserContext().setContext(folder.getTitle());
        new AuditPage()
                .setOperationTypeFilterAndApply(copyType)
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(createdCode)
                .applyAdditionalFilters()
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), copyType,
                        projectsObject, createdCode, createdStatus);
    }
}
