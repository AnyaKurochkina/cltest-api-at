package api.cloud.productCatalog.jinja;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.jinja2.Jinja2Template;
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
    private static Jinja2Template simpleJinja;
    private static Jinja2Template simpleJinja2;

    @BeforeAll
    public static void setUp() {
        simpleJinja = createJinja("export_jinja1_test_api");
        simpleJinja2 = createJinja("export_jinja2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких jinja2")
    @TmsLink("1520118")
    @Test
    public void multiExportJinja2Test() {
        ExportEntity e = new ExportEntity(simpleJinja.getId());
        ExportEntity e2 = new ExportEntity(simpleJinja2.getId());
        exportObjectsById("jinja2_templates", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @SneakyThrows
    @DisplayName("Экспорт jinja по Id")
    @TmsLink("660113")
    @Test
    public void exportJinja2Test() {
        Jinja2Template jinja = createJinja("export_jinja1_test_api");
        exportJinjaById(jinja.getId());
    }
}
