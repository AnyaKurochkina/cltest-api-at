package api.cloud.productCatalog.jinja;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.jinja2.Jinja2;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static steps.productCatalog.Jinja2Steps.createJinja;
import static steps.productCatalog.Jinja2Steps.exportJinjaById;
import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;

@Tag("product_catalog")
@Tag("Jinja")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaExportTest extends Tests {
    private static Jinja2 simpleJinja;
    private static Jinja2 simpleJinja2;

    @BeforeAll
    public static void setUp() {
        simpleJinja = createJinja("export_jinja1_test_api");
        simpleJinja2 = createJinja("export_jinja2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких Jinja2")
    @TmsLink("1520118")
    @Test
    public void multiExportGraphTest() {
        ExportEntity e = new ExportEntity(simpleJinja.getId());
        ExportEntity e2 = new ExportEntity(simpleJinja2.getId());
        exportObjectsById("jinja2_templates", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт jinja по Id")
    @TmsLink("660113")
    @Test
    public void exportJinjaByIdTest() {
        Jinja2 jinja2 = createJinja("export_by_id_api_test");
        exportJinjaById(jinja2.getId());
    }
}
