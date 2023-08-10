package ui.cloud.tests.productCatalog.allowedAction;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.allowedAction.AllowedActionPage;
import ui.elements.Alert;

import java.util.Collections;

import static steps.productCatalog.ActionSteps.createAction;

@Feature("Создание разрешенного действия")
public class CreateAllowedActionTest extends AllowedActionBaseTest {

    @Test
    @TmsLink("1247489")
    @DisplayName("Создание разрешенного действия")
    public void createAllowedActionTest() {
        createWithoutRequiredParameters();
        createWithNonUniqueName();
        createAllowedAction();
    }

    @Step("Создание разрешенного действия без заполнения обязательных полей")
    private void createWithoutRequiredParameters() {
        allowedAction.setActionId(createAction().getActionId());
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage()
                .openAddNewAllowedActionDialog()
                .checkRequiredParams(allowedAction);
    }

    @Step("Создание разрешенного действия с неуникальным кодом")
    private void createWithNonUniqueName() {
        allowedAction.setActionId(action.getActionId());
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .openAddNewAllowedActionDialog()
                .checkNonUniqueNameValidation(allowedAction);
    }

    @Step("Создание разрешенного действия")
    private void createAllowedAction() {
        allowedAction.setEventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                .event_type(EventType.BM.getValue())
                .event_provider(EventProvider.HCP.getValue())
                .build()));
        allowedAction.setActionId(createAction().getActionId());
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .openAddNewAllowedActionDialog()
                .setAttributes(allowedAction);
        AllowedActionPage page = new AllowedActionPage();
        page.getSaveButton().click();
        Alert.green("Разрешенное действие успешно создано");
        page.checkAttributes(allowedAction);
    }
}
