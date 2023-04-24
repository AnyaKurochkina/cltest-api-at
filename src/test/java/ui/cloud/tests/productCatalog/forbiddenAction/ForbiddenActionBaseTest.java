package ui.cloud.tests.productCatalog.forbiddenAction;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.UUID;

import static steps.productCatalog.ForbiddenActionSteps.createForbiddenAction;

@Epic("Конструктор. Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionBaseTest extends BaseTest {

    protected final String TITLE = "AT UI Forbidden Action";
    protected final String NAME = UUID.randomUUID().toString();
    protected ForbiddenAction forbiddenAction;

    @BeforeEach
    public void setUp() {
        forbiddenAction = createForbiddenAction(NAME, TITLE);
    }
}
