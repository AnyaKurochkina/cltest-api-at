package ui.cloud.tests.productCatalog.forbiddenAction;

import io.qameta.allure.Epic;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import models.cloud.productCatalog.graph.Graph;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import steps.productCatalog.ForbiddenActionSteps;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.Collections;
import java.util.UUID;

@Epic("Конструктор.Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionBaseTest extends BaseTest {

    protected final String TITLE = "AT UI Forbidden Action";
    protected final String NAME = UUID.randomUUID().toString();
    protected final String DESCRIPTION = "Description";
    protected ForbiddenAction forbiddenAction;
    protected Action action;
    protected Graph graph;

    @BeforeEach
    public void setUp() {
        createForbiddenAction(NAME);
    }

    protected void createForbiddenAction(String name) {
        graph = Graph.builder()
                .name(UUID.randomUUID().toString())
                .title("AT UI Graph")
                .build()
                .createObject();

        action = createAction(UUID.randomUUID().toString(), graph.getGraphId());

        forbiddenAction = ForbiddenAction.builder()
                .name(name)
                .title(TITLE)
                .actionId(action.getActionId())
                .description(DESCRIPTION)
                .eventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                        .event_type(EventType.BM.getValue())
                        .event_provider(EventProvider.S3.getValue())
                        .build()))
                .build()
                .createObject();
    }

    protected void deleteForbiddenAction(String name) {
        ForbiddenActionSteps.deleteForbiddenActionByName(name);
    }

    protected Action createAction(String name, String graphId) {
        return Action.builder()
                .name(name)
                .title("AT UI Action")
                .graphId(graphId)
                .number(0)
                .build()
                .createObject();
    }
}
