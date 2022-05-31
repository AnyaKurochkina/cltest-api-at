package ui.cloud.tests.productCatalog.orgDirection;

import core.helper.JsonHelper;
import httpModels.productCatalog.orgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.OrgDirection;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.BaseTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("product_catalog_ui")
@Epic("Конструктор")
@Feature("Направления")
@DisabledIfEnv("prod")
public class CreateDirectionTest extends BaseTest {
    public static final String DIRECTION_TITLE = "at_ui_create_direction_title";
    public static final String DIRECTION_NAME = "at_ui_create_direction_name";
    public static final String DIRECTION_DESCRIPTION = "at_ui_create_direction_description";
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/org_direction/",
            "productCatalog/orgDirection/orgDirection.json");

    @Test
    @DisplayName("Создание направления")
    @TmsLink("486332")
    public void createDirections() {
        if (steps.isExists(DIRECTION_NAME)) {
            steps.deleteByName(DIRECTION_NAME, GetOrgDirectionListResponse.class);
        }
        new IndexPage()
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
        assertTrue(new IndexPage()
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
        assertTrue(new IndexPage()
                .goToOrgDirectionsPage()
                .createDirection()
                .fillAndSave(title, name, description)
                .findDirectionByName(name)
                .deleteActionMenu(name)
                .inputInvalidId("invalid-id45")
                .fillIdAndDelete()
                .isNotExist(name), "Направление существует.");
    }

    @Test
    @DisplayName("Удалить из формы редактирования направления")
    public void deleteDirectionFromRedactor() {
        String name = "at_ui_delete_direction_from_redactor";
        String title = "at_ui_delete_direction_from_redactor_title";
        String description = "at_ui_delete_direction_from_redactor_description";
        if (steps.isExists(name)) {
            steps.deleteByName(name, GetOrgDirectionListResponse.class);
        }
        assertFalse(new IndexPage()
                .goToOrgDirectionsPage()
                .createDirection()
                .fillAndSave(title, name, description)
                .openOrgDirectionPage(name)
                .deleteDirection()
                .inputInvalidId("invalid-id45")
                .fillIdAndDelete()
                .isNotExist(name), "Направление существует.");
    }

    @Test
    @DisplayName("Создать копию направления")
    public void copyDirection() {
        String name = "at_ui_copy_direction";
        String title = "at_ui_copy_direction_title";
        String description = "at_ui_copy_direction_description";
        OrgDirection.builder()
                .orgDirectionName(name)
                .title(title)
                .description(description)
                .build()
                .createObject();
        assertTrue(new IndexPage()
                .goToOrgDirectionsPage()
                .findDirectionByName(name)
                .copyActionMenu(name)
                .isFieldsCompare(name, title, description), "Поля не равны");
        String cloneName = name + "-clone";
        steps.deleteByName(cloneName, GetOrgDirectionListResponse.class);
    }

    @Test
    @DisplayName("Импортировать направление")
    public void importDirection() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirection.json");
        String importName = new JsonPath(data).get("OrgDirection.name");
        new IndexPage()
                .goToOrgDirectionsPage()
                .uploadFile("src/test/resources/json/productCatalog/orgDirection/importOrgDirection.json")
                .findDirectionByName(importName);
        steps.deleteByName(importName, GetOrgDirectionListResponse.class);
    }

    @Test
    @DisplayName("Экспортировать направление")
    public void exportDirection() {
        String name = "at_ui_export_direction_name";
        String title = "at_ui_export_direction_title";
        String description = "at_ui_export_direction_description";
        OrgDirection.builder()
                .orgDirectionName(name)
                .title(title)
                .description(description)
                .build()
                .createObject();
        new IndexPage().goToOrgDirectionsPage()
                .findDirectionByName(name)
                .exportActionMenu(name);

    }

    @Test
    @DisplayName("Просмотр списка направлений и поиск")
    @Disabled
    public void viewDirectionsListAndSearch() {
        new IndexPage()
                .goToOrgDirectionsPage()
                .createDirection()
                .fillAndSave("", "", "")
                .checkFields()
                .findDirectionByName(DIRECTION_NAME);
    }
}
