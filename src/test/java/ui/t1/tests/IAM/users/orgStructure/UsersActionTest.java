package ui.t1.tests.IAM.users.orgStructure;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Folder;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.Project;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.models.IamUser;
import ui.t1.pages.IAM.ModalWindow;
import ui.t1.pages.IAM.OrgStructurePage;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.StringUtils.format;
import static models.cloud.authorizer.Folder.BUSINESS_BLOCK;
import static org.junit.jupiter.api.Assertions.*;
import static steps.authorizer.AuthorizerSteps.createFolder;
import static steps.authorizer.AuthorizerSteps.deleteFolderByNameT1;

@Epic("IAM и Управление")
@Feature("Действия с пользователями")
@Tags({@Tag("ui_cloud_users_actions")})
@Log4j2
@ExtendWith(ConfigExtension.class)
public class UsersActionTest extends Tests {

    Project project = Project.builder().isForOrders(true).build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @DisplayName("Добавление/удаление пользователя")
    public void addUserTest() {
        IamUser user = new IamUser("airat.muzafarov@gmail.com", Collections.singletonList("Администратор"));
        String folderTitle = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        String folderName = createFolder(folderTitle);
        try {
            assertTrue(new IndexPage().goToOrgStructure()
                    .openModalWindow(folderTitle)
                    .setRole(user)
                    .isUserAdded(user), String.format("User %s not found", user.getEmail()));
            new IndexPage().goToOrgStructure()
                    .openModalWindow(folderTitle)
                    .deleteUser(user);
            assertTrue(new ModalWindow().isUserTableEmpty());
        } finally {
            deleteFolderByNameT1(folderName);
        }
    }

    @Test
    @DisplayName("Проверка возможности в разделе Оргструктура выбирать отображаемые столбцы таблицы")
    public void editTableInOrgStructureTest() {
        List<String> expectedHeaders = Arrays.asList("Название", "Тип", "Теги");
        List<String> actualHeaders = new IndexPage()
                .goToOrgStructure()
                .openTableSettings()
                .removeColumn("Название")
                .removeColumn("Баланс счета")
                .removeColumn("Идентификатор")
                .saveSettings()
                .getTableHeaders();
        assertEquals(expectedHeaders, actualHeaders, "Заголовки таблицы отличаются");
        List<String> newExpectedHeaders = Arrays.asList("Название", "Теги", "Тип");
        List<String> newActualHeaders = new OrgStructurePage()
                .openTableSettings()
                .moveColumnTo("Теги", 0, 40)
                .saveSettings()
                .getTableHeaders();
        assertEquals(newExpectedHeaders, newActualHeaders, "Заголовки таблицы отличаются");
    }

    @Test
    @DisplayName("Контекст пользователя")
    public void contextUserTest() {
        Organization org = Organization.builder()
                .type("not_default")
                .build()
                .onlyGetObject();
        String orgTitle = org.getTitle();
        Folder folder = Folder.builder()
                .kind(BUSINESS_BLOCK)
                .title("1folder-for-user-context-test")
                .build()
                .createObject();
        String folderName = folder.getName();
        assertTrue(new IndexPage()
                .goToContextDialog()
                .goToAllTab()
                .addToFavorite(folderName)
                .goToFavoriteTab()
                .removeFromFavorite(folderName)
                .goToRecentTab()
                .goToAllTab()
                .changeContext(folder.getTitle())
                .goToContextDialog()
                .goToOrgStructure()
                .goToContextDialog()
                .selectOrganization(orgTitle)
                .changeContext(orgTitle)
                .isContextNameDisplayed(orgTitle), format("Текущий контекст отличается от ожидаемого {}", orgTitle));
    }

    @Test
    @DisplayName("История переводов")
    public void transferHistoryTest() {
        assertFalse(new IndexPage()
                .goToOrgStructure()
                .expandOrgActions()
                .isActionExist("История переводов"), "Действие присутствует");
    }
}
