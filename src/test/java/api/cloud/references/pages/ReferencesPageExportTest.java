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
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static steps.references.ReferencesStep.*;

@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReferencesPageExportTest extends Tests {
    Directories directories;

    @Title("Создание тестовых данных")
    @DisplayName("Создание тестовых данных")
    @BeforeAll
    public void createTestData() {
        directories = createDirectory(createDirectoriesJsonObject("directories_for_multi_export_test_api",
                "test_api"));
    }

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        deletePrivateDirectoryByName(directories.getName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Экспорт нескольких справочников")
    @TmsLink("SOUL-8672")
    public void multiExportPagesTest() {
        List<ExportEntity> listOfExportEntity = IntStream.range(0, 3)
                .mapToObj(i -> createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(StringUtils.getRandomStringApi(7), directories.getId())))
                .map(page -> new ExportEntity(page.getId(), page.getName()))
                .collect(Collectors.toList());
        exportMultiPages(directories.getName(), new ExportData(listOfExportEntity).toJson());
    }
}
