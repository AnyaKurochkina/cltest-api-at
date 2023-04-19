package ui.cloud.tests.productCatalog.allowedAction;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import java.util.Collections;

@Feature("Редактирование разрешенного действия")
public class EditAllowedActionTest extends AllowedActionBaseTest {

    @Test
    @TmsLink("1247490")
    @DisplayName("Редактирование разрешенного действия")
    public void editForbiddenAction() {
        allowedAction.setDescription("New description");
        allowedAction.setEventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                .event_type(EventType.ACL.getValue())
                .event_provider(EventProvider.S3.getValue())
                .build()));
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .findAndOpenAllowedActionPage(NAME)
                .setAttributes(allowedAction)
                .checkAttributes(allowedAction);
    }
}
