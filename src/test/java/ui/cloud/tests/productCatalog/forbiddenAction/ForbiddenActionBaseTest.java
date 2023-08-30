package ui.cloud.tests.productCatalog.forbiddenAction;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;

import static steps.productCatalog.ActionSteps.getActionById;
import static steps.productCatalog.ForbiddenActionSteps.createForbiddenAction;

@Epic("Конструктор. Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionBaseTest extends ProductCatalogUITest {

    protected final String TITLE = "AT UI Forbidden Action";
    protected String NAME;
    protected ForbiddenAction forbiddenAction;
    protected Action action;

    @BeforeEach
    public void setUp() {
        forbiddenAction = createForbiddenAction(TITLE);
        NAME = forbiddenAction.getName();
        action = getActionById(forbiddenAction.getActionId());
    }
}
