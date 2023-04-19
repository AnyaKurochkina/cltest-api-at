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
import java.util.UUID;

@Feature("Создание разрешенного действия")
public class CreateAllowedActionTest extends AllowedActionBaseTest {

    @Test
    @TmsLink("1247489")
    @DisplayName("Создание разрешенного действия")
    public void createForbiddenActionTest() {
        checkNameValidation();
        createWithoutRequiredParameters();
        createWithNonUniqueName();
        createForbiddenAction();
    }

    @Step("Создание разрешенного действия без заполнения обязательных полей")
    private void createWithoutRequiredParameters() {
        allowedAction.setName(NAME + "_");
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .addNewAllowedAction()
                .checkRequiredParams(allowedAction);
    }

    @Step("Создание разрешенного действия с неуникальным кодом")
    private void createWithNonUniqueName() {
        allowedAction.setName(NAME);
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .addNewAllowedAction()
                .checkNonUniqueNameValidation(allowedAction);
    }

    @Step("Создание разрешенного действия с недопустимым кодом")
    private void checkNameValidation() {
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .addNewAllowedAction()
                .checkNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }

    @Step("Создание разрешенного действия")
    private void createForbiddenAction() {
        allowedAction.setName(NAME + "_");
        allowedAction.setEventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                .event_type(EventType.BM.getValue())
                .event_provider(EventProvider.HCP.getValue())
                .build()));
        allowedAction.setActionId(createAction(UUID.randomUUID().toString(), graph.getGraphId()).getActionId());
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .addNewAllowedAction()
                .setAttributes(allowedAction);
        AllowedActionPage page = new AllowedActionPage();
        page.getSaveButton().click();
        Alert.green("Разрешенное действие успешно создано");
        page.checkAttributes(allowedAction);
    }
}
