package ui.productCatalog.tests.orgDirection;

import httpModels.productCatalog.orgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import models.productCatalog.OrgDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import ui.productCatalog.pages.MainPage;
import ui.productCatalog.tests.BaseTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateDirectionTest extends BaseTest {
    public static final String DIRECTION_TITLE = "at_ui_create_direction_title";
    public static final String DIRECTION_NAME = "at_ui_create_direction_name";
    public static final String DIRECTION_DESCRIPTION = "at_ui_create_direction_description";
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/org_direction/",
            "productCatalog/orgDirection/orgDirection.json");

    @Test
    @DisplayName("Создание направления")
    public void createDirections() {
        if (steps.isExists(DIRECTION_NAME)) {
            steps.deleteByName(DIRECTION_NAME, GetOrgDirectionListResponse.class);
        }
        new MainPage()
                .goToOrgDirectionsPage()
                .createDirection()
                .fillAndSave(DIRECTION_TITLE, DIRECTION_NAME, DIRECTION_DESCRIPTION)
                .findDirectionByName(DIRECTION_NAME);
        steps.deleteByName(DIRECTION_NAME, GetOrgDirectionListResponse.class);
    }

    @Test
    @DisplayName("Редактирование поля имени в направлении")
    public void editDirections() {
        String name = "at_ui_edit_direction_name";
        String title = "at_ui_edit_direction_title";
        String description = "at_ui_edit_direction_description";
        String updName = "upd_name";
        OrgDirection.builder()
                .orgDirectionName(name)
                .title(title)
                .description(description)
                .build()
                .createObject();
        assertTrue(new MainPage()
                .goToOrgDirectionsPage()
                .openOrgDirectionPage(name)
                .editNameField(updName)
                .isNameChanged(updName), "Имя направления не соответствует ожидаемому");
    }

    @Test
    @DisplayName("Удалить направление из списка с помощью контекстного меню")
    public void deleteDirectionsWithContextMenu() {
        String name = "at_ui_delete_direction_with_context_menu";
        String title = "at_ui_delete_direction_with_context_menu_title";
        String description = "at_ui_delete_direction_with_context_menu_description";
        if (steps.isExists(name)) {
            steps.deleteByName(name, GetOrgDirectionListResponse.class);
        }
        assertTrue(new MainPage()
                .goToOrgDirectionsPage()
                .createDirection()
                .fillAndSave(title, name, description)
                .findDirectionByName(name)
                .clickActionMenu(name)
                .deleteActionMenu()
                .inputInvalidId("invalid-id45")
                .fillIdAndDelete()
                .isNotExist(name));
    }

    @Test
    @DisplayName("Просмотр списка направлений и поиск")
    public void viewDirectionsListAndSearch() {
        new MainPage()
                .goToOrgDirectionsPage()
                .createDirection()
                .fillAndSave("", "", "")
                .checkFields()
                .findDirectionByName(DIRECTION_NAME);
    }
}
