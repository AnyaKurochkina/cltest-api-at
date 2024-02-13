package ui.cloud.tests.productCatalog.forbiddenAction;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.forbiddenAction.ForbiddenActionPage;
import ui.elements.Alert;

import java.util.Collections;

import static steps.productCatalog.ActionSteps.createAction;

@Feature("Создание запрещенного действия")
public class CreateForbiddenActionTest extends ForbiddenActionBaseTest {

    @Test
    @TmsLink("946766")
    @DisplayName("Создание запрещенного действия")
    public void createForbiddenActionTest() {
        createWithoutRequiredParameters();
        createWithNonUniqueName();
        createForbiddenAction();
    }

    @Step("Создание запрещенного действия без заполнения обязательных полей")
    private void createWithoutRequiredParameters() {
        forbiddenAction.setActionId(createAction().getId());
        new ControlPanelIndexPage().goToForbiddenActionsListPage()
                .addNewForbbidenAction()
                .checkRequiredParams(forbiddenAction);
    }

    @Step("Создание запрещенного действия с неуникальным кодом")
    private void createWithNonUniqueName() {
        forbiddenAction.setActionId(action.getId());
        new ControlPanelIndexPage().goToForbiddenActionsListPage()
                .addNewForbbidenAction()
                .checkNonUniqueNameValidation(forbiddenAction);
    }

    @Step("Создание запрещенного действия")
    private void createForbiddenAction() {
        forbiddenAction.setEventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                .event_type(EventType.BM.getValue())
                .event_provider(EventProvider.HCP.getValue())
                .build()));
        forbiddenAction.setActionId(createAction().getId());
        new ControlPanelIndexPage().goToForbiddenActionsListPage()
                .addNewForbbidenAction()
                .setAttributes(forbiddenAction);
        ForbiddenActionPage page = new ForbiddenActionPage();
        page.getSaveButton().click();
        Alert.green("Запрещенное действие успешно создано");
        page.checkAttributes(forbiddenAction);
    }
}
