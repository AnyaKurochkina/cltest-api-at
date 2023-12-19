package api.cloud.references.pages;

import api.Tests;
import core.helper.StringUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.references.Directories;
import models.cloud.references.Pages;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static steps.references.ReferencesStep.*;

@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReferencesPageExportTest extends Tests {
    List<String> deleteList = new ArrayList<>();
    Directories directories;

    @Title("Создание тестовых данных")
    @DisplayName("Создание тестовых данных")
    @BeforeAll
    public void createTestData() {
        directories = createDirectory(createDirectoriesJsonObject("directories_for_multi_export_test_api",
                "test_api"));
        deleteList.add(directories.getName());
    }

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        for (String name : deleteList) {
            deletePrivateDirectoryByName(name);
        }
    }

    @Test
    @SneakyThrows
    @DisplayName("Экспорт нескольких справочников")
    @TmsLink("")
    public void multiExportPagesTest() {
        Pages page1 = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(StringUtils.getRandomStringApi(7), directories.getId()));
        Pages page2 = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(StringUtils.getRandomStringApi(7), directories.getId()));
        Pages page3 = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(StringUtils.getRandomStringApi(7), directories.getId()));
        ExportEntity e = new ExportEntity(page1.getId(), page1.getName());
        ExportEntity e2 = new ExportEntity(page2.getId(), page2.getName());
        ExportEntity e3 = new ExportEntity(page3.getId(), page3.getName());
        exportMultiPages(directories.getName(), new ExportData(Arrays.asList(e, e2, e3)).toJson());
    }
}
