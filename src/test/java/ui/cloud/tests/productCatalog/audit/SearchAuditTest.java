package ui.cloud.tests.productCatalog.audit;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.productCatalog.enums.AuditChangeType;
import models.cloud.productCatalog.graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.MeccanoAuditPage;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;
import ui.t1.tests.audit.AuditPeriod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.GraphSteps.deleteGraphById;
import static steps.productCatalog.ProductCatalogSteps.getAuditListByObjKeys;

@Feature("Просмотр истории изменений")
public class SearchAuditTest extends ProductCatalogUITest {

    private static GlobalUser user;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final String name = UUID.randomUUID().toString();
    private final String title = "AT UI Graph";
    private Graph graph;

    @BeforeEach
    @DisplayName("Создание и удаление графа")
    public void setup() {
        graph = createGraph(name, title);
        deleteGraphById(graph.getGraphId());
    }

    @Test
    @TmsLink("SOUL-8830")
    @DisplayName("Поиск в истории изменений")
    public void searchGraphAuditTest() {
        user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        MeccanoAuditPage page = new ControlPanelIndexPage().goToMeccanoAuditPage();
        page.getSetTypeAndCodeWarning()
                .shouldBe(Condition.visible.because("Отображается подсказка о необходимости задания параметров"));
        List<ProductAudit> auditList = getAuditListByObjKeys("graphs", graph.getName());
        page.setObjectType("Граф")
                .setObjectName(graph.getName())
                .find()
                .checkFirstRecord(user.getEmail(), AuditChangeType.DELETE, "")
                .setAuditId(auditList.get(1).getAuditId())
                .find()
                .checkFirstRecord(user.getEmail(), AuditChangeType.CREATE, "1.0.0");
        page.getAuditIdInput().clear();
        page.setObjectName(graph.getName().substring(0, 20))
                .find()
                .checkRecordsNotFound()
                .setObjectName(graph.getName())
                .find()
                .selectPeriod(AuditPeriod.LAST_6_HOURS)
                .checkRecordsFound()
                .setFilterByDate(LocalDateTime.now().plusDays(1).format(formatter),
                        LocalDateTime.now().plusDays(2).format(formatter))
                .checkRecordsNotFound();
        //TODO после исправления PO-1854 добавить проверку на фильтр по пользователю
    }
}
