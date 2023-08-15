package ui.cloud.tests.productCatalog.action;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.actions.ActionPage;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;
import ui.elements.Alert;

import java.util.UUID;

public class EditActionTest extends ActionBaseTest {

    @Test
    @DisplayName("Регистрация действия")
    //SOUL-6051
    public void registerAction() {
        String name = UUID.randomUUID().toString();
        createActionByApi(name);
        new ControlPanelIndexPage()
                .goToActionsListPage()
                .openActionForm(name)
                .getRegisterButton()
                .click();
        Alert.green("Действие успешно зарегистрировано");
        new ActionsListPage()
                .openActionForm(name)
                .getRegisterButton()
                .click();
        Alert.red("Ошибка при добавлении действия заказа");
    }
}
