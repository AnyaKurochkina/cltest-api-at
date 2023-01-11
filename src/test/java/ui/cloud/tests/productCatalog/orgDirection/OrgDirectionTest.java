package ui.cloud.tests.productCatalog.orgDirection;

import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import models.cloud.productCatalog.service.Service;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.orgDirectionsPages.OrgDirectionPage;
import ui.cloud.pages.productCatalog.orgDirectionsPages.OrgDirectionsListPage;
import ui.cloud.tests.productCatalog.BaseTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.OrgDirectionSteps.*;

@Tag("product_catalog_ui")
@Epic("Конструктор")
@Feature("Направления")
@DisabledIfEnv("prod")
public class OrgDirectionTest extends BaseTest {
    public static final String DIRECTION_TITLE = "at_ui_create_direction_title";
    public static final String DIRECTION_NAME = "at_ui_create_direction_name";
    public static final String DIRECTION_DESCRIPTION = "at_ui_create_direction_description";
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/org_direction/",
            "productCatalog/orgDirection/orgDirection.json");

    @Test
    @DisplayName("Создание направления")
    @TmsLink("486332")
    public void createDirections() {
        if (isOrgDirectionExists(DIRECTION_NAME)) {
            deleteOrgDirectionById(getOrgDirectionByName(DIRECTION_NAME).getId());
        }
        new IndexPage()
                .goToOrgDirectionsPage()
                .createDirection()
                .fillAndSave(DIRECTION_TITLE, DIRECTION_NAME, DIRECTION_DESCRIPTION)
                .findDirectionByName(DIRECTION_NAME);
        deleteOrgDirectionById(getOrgDirectionByName(DIRECTION_NAME).getId());
    }

    @Test
    @DisplayName("Редактирование поля имени в направлении")
    @TmsLink("486544")
    public void editDirections() {
        String name = "at_ui_edit_direction_name";
        String title = "at_ui_edit_direction_title";
        String description = "at_ui_edit_direction_description";
        String updName = "upd_name";
        OrgDirection.builder()
                .name(name)
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
    @TmsLink("486568")
    public void deleteDirectionsWithContextMenu() {
        String name = "at_ui_delete_direction_with_context_menu";
        String title = "at_ui_delete_direction_with_context_menu_title";
        String description = "at_ui_delete_direction_with_context_menu_description";
        OrgDirection.builder()
                .name(name)
                .title(title)
                .description(description)
                .build()
                .createObject();
        new IndexPage()
                .goToOrgDirectionsPage()
                .findDirectionByName(name)
                .deleteActionMenu(name)
                .inputInvalidId("invalid-id45")
                .inputValidIdAndDelete();
        assertFalse(new OrgDirectionsListPage().isOrgDirectionExist(name));
    }

    @Test
    @DisplayName("Удалить из формы редактирования направления")
    @TmsLink("508543")
    public void deleteDirectionFromRedactor() {
        String name = "at_ui_delete_direction_from_redactor";
        String title = "at_ui_delete_direction_from_redactor_title";
        String description = "at_ui_delete_direction_from_redactor_description";
        OrgDirection.builder()
                .name(name)
                .title(title)
                .description(description)
                .build()
                .createObject();
        new IndexPage()
                .goToOrgDirectionsPage()
                .openOrgDirectionPage(name)
                .deleteDirection()
                .inputInvalidId("invalid-id45")
                .inputValidIdAndDelete();
        assertFalse(new OrgDirectionsListPage().isOrgDirectionExist(name));
    }

    @Test
    @DisplayName("Удаление направления используемого в сервисе")
    @TmsLink("1240245")
    public void deleteDirectionUsedInServiceTest() {
        String name = "at_ui_delete_direction_used_in_service";
        String alertText = "Нельзя удалить направление, которое используется";
        OrgDirection org = OrgDirection.builder()
                .name(name)
                .title(name)
                .description(name)
                .build()
                .createObject();
        Service.builder()
                .name("at_ui_service_with_org_direction")
                .directionId(org.getId())
                .build()
                .createObject();
        new IndexPage()
                .goToOrgDirectionsPage()
                .openOrgDirectionPage(name)
                .deleteDirection()
                .inputValidIdAndDeleteNotAvailable(alertText);
        new OrgDirectionPage()
                .exitFromOrgDirectionPage()
                .findDirectionByName(name)
                .deleteActionMenu(name)
                .inputValidIdAndDeleteNotAvailable(alertText);
        assertTrue(new OrgDirectionsListPage().isOrgDirectionExist(name));
    }

    @Test
    @DisplayName("Создать копию направления")
    @TmsLink("486597")
    public void copyDirection() {
        String name = "at_ui_copy_direction";
        String title = "at_ui_copy_direction_title";
        String description = "at_ui_copy_direction_description";
        OrgDirection.builder()
                .name(name)
                .title(title)
                .description(description)
                .build()
                .createObject();
        assertTrue(new IndexPage()
                .goToOrgDirectionsPage()
                .findDirectionByName(name)
                .copyActionMenu(name)
                .isFieldsCompare(name, title, description), "Поля не равны");
        deleteOrgDirectionById(getOrgDirectionByName(name + "-clone").getId());
    }

    @Test
    @DisplayName("Импортировать направление")
    @TmsLink("486652")
    public void importDirection() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirection.json");
        String importName = new JsonPath(data).get("OrgDirection.name");
        new IndexPage()
                .goToOrgDirectionsPage()
                .uploadFile("src/test/resources/json/productCatalog/orgDirection/importOrgDirection.json")
                .findDirectionByName(importName);
        deleteOrgDirectionById(getOrgDirectionByName(importName).getId());
    }

    @Test
    @DisplayName("Экспортировать направление")
    @Disabled
    public void exportDirection() {
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
