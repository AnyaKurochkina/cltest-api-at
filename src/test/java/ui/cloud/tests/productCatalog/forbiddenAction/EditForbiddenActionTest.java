package ui.cloud.tests.productCatalog.forbiddenAction;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import java.util.Collections;

@Feature("Редактирование запрещенного действия")
public class EditForbiddenActionTest extends ForbiddenActionBaseTest {

    @Test
    @TmsLink("946919")
    @DisplayName("Редактирование запрещенного действия")
    public void editForbiddenAction() {
        forbiddenAction.setDescription("New description");
        forbiddenAction.setEventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                .event_type(EventType.ACL.getValue())
                .event_provider(EventProvider.S3.getValue())
                .build()));
        new ControlPanelIndexPage().goToForbiddenActionsListPage()
                .findAndOpenForbiddenActionPage(NAME)
                .setAttributes(forbiddenAction)
                .checkAttributes(forbiddenAction);
    }
}
