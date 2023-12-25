package api.cloud.references.pages;

import api.Tests;
import core.helper.StringUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.references.Directories;
import models.cloud.references.Pages;
import models.cloud.references.UpdateData;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.references.ReferencesStep.*;

@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReferencesUpdateDataTest extends Tests {
    List<String> deleteList = new ArrayList<>();
    Directories directories;
    Pages page;

    @Title("Создание тестовых данных")
    @DisplayName("Создание тестовых данных")
    @BeforeAll
    public void createTestData() {
        directories = createDirectory(createDirectoriesJsonObject("directories_for_update_data_test_api",
                "update_data"));

        deleteList.add(directories.getName());
    }

    @BeforeEach
    public void createPage() {
        String pageName = StringUtils.getRandomStringApi(6);
        page = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(pageName, directories.getId()));
    }

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        for (String name : deleteList) {
            deletePrivateDirectoryByName(name);
        }
    }

    @DisplayName("Добавление ключа в существующий Page через updateData")
    @TmsLink("SOUL-8621")
    @Test
    public void addKeyToExistPageTest() {
        UpdateData data = UpdateData.builder()
                .directory(directories.getId())
                .page(page.getId())
                .updateData(new LinkedHashMap<String, String>() {{
                    put("test_key", "test_value");
                }})
                .build();
        checkedUpdateDataPageRequest(data);
        LinkedHashMap<String, String> actualData = (LinkedHashMap) getPagesById(page.getId()).getData();
        assertEquals(data.getUpdateData(), actualData);
    }

    @DisplayName("Обновление value у существующего key в Page через updateData")
    @TmsLink("SOUL-8622")
    @Test
    public void updateKeyValueExistPageTest() {
        LinkedHashMap<String, String> updateData = new LinkedHashMap<String, String>() {{
            put("test_key", "test_value");
        }};
        UpdateData data = UpdateData.builder()
                .directory(directories.getId())
                .page(page.getId())
                .updateData(updateData)
                .build();

        checkedUpdateDataPageRequest(data);

        updateData.put("test_key", "updated_value");
        data.setUpdateData(updateData);

        checkedUpdateDataPageRequest(data);
        LinkedHashMap<String, String> actualData = (LinkedHashMap) getPagesById(page.getId()).getData();
        assertEquals(data.getUpdateData(), actualData);
    }

    @DisplayName("Добавление ключа в существующий Page по uuid страницы без указания Directories через updateData")
    @TmsLink("SOUL-8623")
    @Test
    public void addKeyToExistPageByUUIDWithOutDirectoriesTest() {
        UpdateData data = UpdateData.builder()
                .page(page.getId())
                .updateData(new LinkedHashMap<String, String>() {{
                    put("test_key", "test_value");
                }})
                .build();
        checkedUpdateDataPageRequest(data);
        LinkedHashMap<String, String> actualData = (LinkedHashMap) getPagesById(page.getId()).getData();
        assertEquals(data.getUpdateData(), actualData);
    }

    @DisplayName("Добавление ключа в существующий Page по name страницы без указания Directories через updateData")
    @TmsLink("SOUL-8624")
    @Test
    public void addKeyToExistPageByNameWithOutDirectoriesTest() {
        UpdateData data = UpdateData.builder()
                .page(page.getName())
                .updateData(new LinkedHashMap<String, String>() {{
                    put("test_key", "test_value");
                }})
                .build();
        String errorMessage = uncheckedUpdateDataPageRequest(data).assertStatus(400).jsonPath()
                .getList("errors", String.class).get(0);
        assertEquals("Value (Directory) is required if the (Page) type is not uuid", errorMessage);
    }

    @DisplayName("Добавление ключа без указания Page через updateData")
    @TmsLink("SOUL-8625")
    @Test
    public void addKeyWithoutPageTest() {
        UpdateData data = UpdateData.builder()
                .directory(directories.getId())
                .updateData(new LinkedHashMap<String, String>() {{
                    put("test_key", "test_value");
                }})
                .build();
        String errorMessage = uncheckedUpdateDataPageRequest(data).assertStatus(400).jsonPath()
                .getList("errors", String.class).get(0);
        assertEquals("Missing required query params: (page)", errorMessage);
    }

    @DisplayName("Добавление ключа в несуществующий Page и Directories по имени через updateData.")
    @TmsLink("SOUL-8626")
    @Test
    public void addKeyToNotExistPageAndDirectoriesTest() {
        String directoryName = StringUtils.getRandomStringApi(6);
        String pageName = StringUtils.getRandomStringApi(6);
        UpdateData data = UpdateData.builder()
                .directory(directoryName)
                .page(pageName)
                .updateData(new LinkedHashMap<String, String>() {{
                    put("test_key", "test_value");
                }})
                .build();

        checkedUpdateDataPageRequest(data);
        deleteList.add(directoryName);
        assertTrue(getDirectoriesList().stream().anyMatch(x -> x.getName().equals(directoryName)));
        assertTrue(isPageExist(getPagesList(),pageName, getDirectoryByName(directoryName).getId()));
    }

    @DisplayName("Добавление ключа в несуществующий Page и Directories по uuid через updateData.")
    @TmsLink("SOUL-8627")
    @Test
    public void addKeyToNotExistPageAndDirectoriesByNameTest() {
        String directoryUUID = UUID.randomUUID().toString();
        String pageUUID = UUID.randomUUID().toString();
        UpdateData data = UpdateData.builder()
                .directory(directoryUUID)
                .page(pageUUID)
                .updateData(new LinkedHashMap<String, String>() {{
                    put("test_key", "test_value");
                }})
                .build();

        List<String> errors = uncheckedUpdateDataPageRequest(data).assertStatus(400).jsonPath().getList("errors", String.class);
        assertTrue(errors.contains(format("Directory with id={} does not exist", directoryUUID)));
        assertTrue(errors.contains(format("Page with id={} does not exist", pageUUID)));
    }
}
