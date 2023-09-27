package ui.t1.tests.audit;

import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import io.restassured.path.json.JsonPath;
import models.cloud.authorizer.Folder;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.ContextPage;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static steps.productCatalog.GraphSteps.deleteGraphByIdInContext;
import static steps.productCatalog.GraphSteps.partialUpdateGraphInContext;
import static steps.resourceManager.ResourceManagerSteps.getFolderById;
import static steps.resourceManager.ResourceManagerSteps.getProjectJsonPath;
import static ui.cloud.pages.productCatalog.AuditPage.NO_VALUE;

@Feature("Просмотр аудита на портале Т1")
@ExtendWith(ConfigExtension.class)
public class T1PortalAuditTest extends Tests {

    private final String projectsObject = "projects";
    private final GlobalUser cloudAdmin = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
    private final GlobalUser pcAdmin = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final String okCode = "200";
    private final String okStatus = "ок";
    private final String modifyType = "modify";
    private final Project project = Project.builder().isForOrders(true).build().createObject();
    private Graph graph;

    @BeforeEach
    public void setUp() {
        graph = Graph.builder()
                .name(UUID.randomUUID().toString())
                .title("AT UI Graph")
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .author("AT UI")
                .build()
                .createObject();

        partialUpdateGraphInContext(graph.getGraphId(),
                new JSONObject().put("id", graph.getGraphId()).put("description", "audit test"),
                project.getId());
        deleteGraphByIdInContext(graph.getGraphId(), project.getId());

        new T1LoginPage(project.getId()).signIn(cloudAdmin.getRole());
    }

    @Test
    @TmsLink("SOUL-4129")
    @DisplayName("Просмотр аудита за период")
    public void checkFilterByDate() {
        AuditPage page = new IndexPage().goToPortalAuditPage();
        Assertions.assertEquals("последний 1 час", page.getPeriodSelect().getValue());
        page.getBeginDateInput().getInput().shouldBe(Condition.disabled);
        page.getEndDateInput().getInput().shouldBe(Condition.disabled);
        page.checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), modifyType,
                projectsObject, okCode, okStatus);
        page.selectPeriod("последние 12 часов")
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), modifyType,
                        projectsObject, okCode, okStatus)
                .setFilterByDate(LocalDateTime.now().minusDays(1).format(formatter),
                        LocalDateTime.now().format(formatter))
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), modifyType,
                        projectsObject, okCode, okStatus);
    }

    @Test
    @TmsLink("SOUL-4132")
    @DisplayName("Сортировка таблицы аудита")
    public void checkAuditTable() {
        new IndexPage().goToPortalAuditPage()
                .checkHeaders()
                .checkSortingByDate();
    }

    @Test
    @TmsLink("SOUL-4133")
    @DisplayName("Фильтрация таблицы аудита")
    public void checkFilters() {
        new IndexPage().goToPortalAuditPage()
                .setOperationTypeFilterAndApply(modifyType)
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), modifyType,
                        projectsObject, okCode, okStatus)
                .setUserFilter("test_user")
                .applyAdditionalFilters()
                .checkRecordsNotFoundV2()
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(okCode)
                .applyAdditionalFilters()
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), modifyType,
                        projectsObject, okCode, okStatus);
    }

    @Test
    @TmsLink("SOUL-4135")
    @DisplayName("Просмотр записи аудита в родительском контексте")
    public void checkAuditInParentContext() {
        new IndexPage().goToPortalAuditPage();
        JsonPath projectJson = getProjectJsonPath(project.getId());
        String folderId = projectJson.getString("data.folder");
        Folder folder = getFolderById(folderId);
        new ContextPage().openUserContext().setContext(folder.getTitle());
        new AuditPage()
                .setOperationTypeFilterAndApply(modifyType)
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(okCode)
                .applyAdditionalFilters()
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), modifyType,
                        projectsObject, okCode, okStatus);
    }

    @Test
    @TmsLinks({@TmsLink("SOUL-4131"), @TmsLink("SOUL-4134")})
    @DisplayName("Просмотр записи аудита, проверка отображения запроса и ответа")
    public void checkShowRequestAndResponse() {
        String requestValue = "audit test";
        new IndexPage().goToPortalAuditPage()
                .setOperationTypeFilterAndApply(modifyType)
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(okCode)
                .applyAdditionalFilters()
                .checkFirstRecordDetails(project.getId(), projectsObject, NO_VALUE, NO_VALUE)
                .getShowRequest().setChecked(true);
        AuditPage page = new AuditPage().checkFirstRecordDetails(project.getId(), projectsObject, requestValue, NO_VALUE);
        page.getShowResponse().setChecked(true);
        page.checkRecordDetailsByResponse(project.getId(), projectsObject, requestValue, graph.getGraphId())
                .checkCopyToClipboard(graph.getGraphId(), project.getId())
                .checkResponseFullViewContains(graph.getName(), project.getId());
    }
}