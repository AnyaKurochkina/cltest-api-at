package ui.t1.tests.IAM.orgStructure;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Folder;
import models.cloud.authorizer.Project;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IAM.OrgStructurePage;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.authorizer.AuthorizerSteps.deleteFolder;

@Epic("IAM и Управление")
@Feature("Действия с проектом")
@Tags({@Tag("ui_cloud_project_actions")})
@Log4j2
@ExtendWith(ConfigExtension.class)
public class ProjectActionTest extends Tests {

    Project project = Project.builder().isForOrders(true).build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @DisplayName("Создание и удаление проекта в структуре организации")
    public void createAndDeleteProjectTest() {
        String name = "create_project_ui_test";
        assertTrue(new IndexPage()
                .goToOrgStructure()
                .createProject(name)
                .isProjectExist(name));
        assertFalse(new OrgStructurePage()
                .deleteProject(name)
                .isProjectExist(name));
    }

    @Test
    @DisplayName("Редактирование проекта")
    public void editProjectTest() {
        String name = "edit_project_ui_test";
        String updatedName = "updated_project_ui_test";
        assertTrue(new IndexPage()
                .goToOrgStructure()
                .createProject(name)
                .changeNameProject(name, updatedName)
                .isProjectExist(updatedName));
        new OrgStructurePage()
                .deleteProject(updatedName);
    }

    @Test
    @DisplayName("Создание проекта в структуре папки")
    public void createProjectInFolderTest() {
        String folderName = "org_structure_ui_test";
        Folder folder = Folder
                .builder()
                .title(folderName)
                .kind(Folder.BUSINESS_BLOCK)
                .build()
                .createObject();
        String projectName = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        assertTrue(new IndexPage()
                .goToOrgStructure()
                .createProjectInFolder(folder.getTitle(), projectName)
                .isProjectExistInFolder(projectName, folder.getTitle()));
        String projectId = new OrgStructurePage().getResourceId(projectName);
        deleteFolder(projectId);
    }
}
