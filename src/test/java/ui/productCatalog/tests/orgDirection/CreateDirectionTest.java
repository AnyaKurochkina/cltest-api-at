package ui.productCatalog.tests.orgDirection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.LoginPage;
import ui.productCatalog.pages.MainPage;
import ui.productCatalog.tests.BaseTest;

public class CreateDirectionTest extends BaseTest {
    public static final String DIRECTION_TITLE = "at_ui_create_direction_title";
    public static final String DIRECTION_NAME = "at_ui_create_direction_name";
    public static final String DIRECTION_DESCRIPTION = "at_ui_create_direction_description";


    @BeforeEach
    public void login() {
        LoginPage loginPage = new LoginPage();
        loginPage.login("portal_admin", "portal_admin");
    }

    @Test
    @DisplayName("Создание направления")
    public void createDirections() {
        new MainPage()
                .goToOrgDirectionsPage()
                .createDirection(DIRECTION_TITLE, DIRECTION_NAME, DIRECTION_DESCRIPTION)
                .findDirectionByName(DIRECTION_NAME);
    }

    @Test
    @DisplayName("Просмотр списка направлений и поиск")
    public void viewDirectionsListAndSearch() {
        new MainPage()
                .goToOrgDirectionsPage()
                .createDirection(DIRECTION_TITLE, DIRECTION_NAME, DIRECTION_DESCRIPTION)
                .checkFields()
                .findDirectionByName(DIRECTION_NAME);
    }
}
