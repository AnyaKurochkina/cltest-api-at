package ui.cloud.tests.productCatalog.allowedAction;

import io.qameta.allure.Epic;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import models.cloud.productCatalog.graph.Graph;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import steps.productCatalog.AllowedActionSteps;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.Collections;
import java.util.UUID;

@Epic("Конструктор. Разрешенные действия")
@DisabledIfEnv("prod")
public class AllowedActionBaseTest extends BaseTest {

    protected final String TITLE = "AT UI Allowed Action";
    protected final String NAME = UUID.randomUUID().toString();
    protected final String DESCRIPTION = "Description";
    protected AllowedAction allowedAction;
    protected Action action;
    protected Graph graph;

    @BeforeEach
    public void setUp() {
        createAllowedAction(NAME);
    }

    protected void createAllowedAction(String name) {
        graph = Graph.builder()
                .name(UUID.randomUUID().toString())
                .title("AT UI Graph")
                .type(GraphType.ACTION.getValue())
                .description("for action")
                .build()
                .createObject();

        action = createAction(UUID.randomUUID().toString(), graph.getGraphId());

        allowedAction = AllowedAction.builder()
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

    protected Action createAction(String name, String graphId) {
        return Action.builder()
                .name(name)
                .title("AT UI Action")
                .graphId(graphId)
                .description("for allowed action")
                .number(0)
                .build()
                .createObject();
    }

    protected void deleteAllowedAction(String name) {
        AllowedActionSteps.deleteAllowedActionByName(name);
    }
}
