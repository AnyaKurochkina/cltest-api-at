package ui.cloud.tests.productCatalog.action;

import io.qameta.allure.Step;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import org.junit.DisabledIfEnv;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.Arrays;
import java.util.Collections;

@DisabledIfEnv("prod")
public class ActionBaseTest extends BaseTest {

    protected final String TITLE = "AT UI Action";

    @Step("Создание действия '{name}'")
    protected Action createActionByApi(String name) {
        return Action.builder()
                .name(name)
                .title(TITLE)
                .number(0)
                .eventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                        .event_type(EventType.VM.getValue())
                        .event_provider(EventProvider.VSPHERE.getValue())
                        .build()))
                .build()
                .createObject();
    }
}
