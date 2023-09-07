package ui.t1.tests.IAM.users.orgStructure;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.elements.Alert;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IAM.OrgStructurePage;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("IAM и Управление")
@Feature("Действия с проектом")
@Tags({@Tag("ui_cloud_folder_actions")})
@Log4j2
@ExtendWith(ConfigExtension.class)
public class FolderActionTest extends Tests {

    Project project = Project.builder().isForOrders(true).build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @DisplayName("Создание/удаление папки")
    public void createAndDeleteFolderTest() {
        String folderName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        assertTrue(new IndexPage().goToOrgStructure()
                .createFolder(folderName)
                .isFolderExist(folderName));
        assertFalse(new OrgStructurePage()
                .deleteFolder(folderName)
                .isFolderExist(folderName));
    }

    @Test
    @DisplayName("Редактирование папки")
    public void editFolderTest() {
        String folderName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        String newName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        assertTrue(new IndexPage()
                .goToOrgStructure()
                .createFolder(folderName)
                .changeNameFolder(folderName, newName)
                .isFolderExist(newName));
        new OrgStructurePage()
                .deleteFolder(newName);
    }

    @Test
    @DisplayName("Удаление папки, находясь в ее контексте")
    public void deleteFolderInContextTest() {
        String folderName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        new IndexPage()
                .goToOrgStructure()
                .createFolder(folderName)
                .selectContext(folderName)
                .deleteFolder(folderName);
        Alert.green("Выбран контекст:");

    }
}
