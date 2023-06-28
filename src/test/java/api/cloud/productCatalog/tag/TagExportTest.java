package api.cloud.productCatalog.tag;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;
import static steps.productCatalog.TagSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Теги")
@DisabledIfEnv("prod")
public class TagExportTest extends Tests {
    private static models.cloud.productCatalog.tag.Tag simpleTag;
    private static models.cloud.productCatalog.tag.Tag simpleTag2;
    private static models.cloud.productCatalog.tag.Tag simpleTag3;

    @BeforeAll
    public static void setUp() {
        simpleTag = createTagByName("export_tag1_test_api");
        simpleTag2 = createTagByName("export_tag2_test_api");
        simpleTag3 = createTagByName("export_tag3_test_api");
    }

    @AfterAll
    public static void deleteTestData() {
        deleteTagByName(simpleTag.getName());
        deleteTagByName(simpleTag2.getName());
        deleteTagByName(simpleTag3.getName());
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких Тегов")
    @TmsLink("1698452")
    @Test
    public void exportTagsTest() {
        ExportEntity e = new ExportEntity(simpleTag.getId());
        ExportEntity e2 = new ExportEntity(simpleTag2.getId());
        exportObjectsById("tags", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт Тега по Id")
    @TmsLink("1698455")
    @Test
    public void exportTagByIdTest() {
        exportTagByName(simpleTag3.getName());
    }
}
