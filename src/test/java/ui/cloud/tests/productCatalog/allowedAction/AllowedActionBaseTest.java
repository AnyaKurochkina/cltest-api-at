package ui.cloud.tests.productCatalog.allowedAction;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import models.cloud.productCatalog.graph.Graph;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import ui.cloud.tests.productCatalog.BaseTest;

import static steps.productCatalog.ActionSteps.getActionById;
import static steps.productCatalog.AllowedActionSteps.createAllowedAction;

@Epic("Конструктор. Разрешенные действия")
@DisabledIfEnv("prod")
public class AllowedActionBaseTest extends BaseTest {

    protected final String TITLE = "AT UI Allowed Action";
    protected String NAME;
    protected AllowedAction allowedAction;
    protected Action action;
    protected Graph graph;

    @BeforeEach
    public void setUp() {
        allowedAction = createAllowedAction(TITLE);
        NAME = allowedAction.getName();
        action = getActionById(allowedAction.getActionId());
    }
}
