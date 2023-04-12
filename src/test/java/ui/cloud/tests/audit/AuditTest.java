package ui.cloud.tests.audit;

import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.graph.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.LoginPageControlPanel;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.extesions.ConfigExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Feature("Просмотр аудита в Control panel")
@ExtendWith(ConfigExtension.class)
public class AuditTest extends Tests {

    private final String graphsObject = "graphs";
    private final String noValue = "—";
    private final GlobalUser superviewer = GlobalUser.builder().role(Role.SUPERVIEWER).build().createObject();
    private final GlobalUser pcAdmin = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final String createdCode = "201";
    private final String createdStatus = "создан";
    private final String createType = "create";
    private String graphName;
    private Graph graph;

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

        new LoginPageControlPanel().signIn(superviewer.getRole());
    }

    @Test
    @TmsLink("1189306")
    @DisplayName("Просмотр аудита за период")
    public void checkFilterByDate() {
        AuditPage page = new ControlPanelIndexPage().goToAuditPage();
        Assertions.assertEquals("последний 1 час", page.getPeriodSelect().getValue());
        page.getBeginDateInput().getInput().shouldBe(Condition.disabled);
        page.getEndDateInput().getInput().shouldBe(Condition.disabled);
        page.checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                graphsObject, createdCode, createdStatus);
        page.selectPeriod("последние 6 часов")
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                        graphsObject, createdCode, createdStatus)
                .setFilterByDate(LocalDateTime.now().minusDays(1).format(formatter),
                        LocalDateTime.now().format(formatter))
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                        graphsObject, createdCode, createdStatus);
    }

    @Test
    @TmsLink("1189307")
    @DisplayName("Сортировка таблицы аудита")
    public void checkAuditTable() {
        new ControlPanelIndexPage().goToAuditPage()
                .checkHeaders()
                .checkSortingByDate();
    }

    @Test
    @TmsLink("1189310")
    @DisplayName("Фильтрация таблицы аудита")
    public void checkFilters() {
        new ControlPanelIndexPage().goToAuditPage()
                .setOperationTypeFilterAndApply("create")
                .setServiceFilterAndApply("product-catalog")
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                        graphsObject, createdCode, createdStatus)
                .setUserFilter("test_user")
                .applyAdditionalFilters()
                .checkRecordsNotFoundV2()
                .setUserFilter(pcAdmin.getEmail())
                .setStatusCodeFilter(createdCode)
                .setObjectType(graphsObject)
                .setObjectIdFilter(graph.getGraphId())
                .applyAdditionalFilters()
                .checkAuditContains(LocalDateTime.now().format(formatter), pcAdmin.getEmail(), createType,
                        graphsObject, createdCode, createdStatus);
    }

    @Test
    @TmsLink("1189305")
    @DisplayName("Просмотр записи аудита")
    public void checkAuditRecordDetails() {
        new ControlPanelIndexPage().goToAuditPage()
                .setObjectIdFilter(graph.getGraphId())
                .applyAdditionalFilters()
                .checkFirstRecord(LocalDateTime.now().format(formatter), pcAdmin.getUsername(), createType, graphsObject,
                        createdCode, createdStatus)
                .checkFirstRecordDetails(graph.getGraphId(), graphsObject, noValue, noValue)
                .showRequestAndResponse()
                .checkFirstRecordDetails(graph.getGraphId(), graphsObject, graph.getDescription(), graph.getGraphId())
                //TODO.checkCopyToClipboard(graph.getTitle(), graph.getGraphId())
                .checkResponseFullViewContains(graph.getName(), graph.getGraphId());
    }
}
