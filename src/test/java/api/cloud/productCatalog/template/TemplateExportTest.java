package api.cloud.productCatalog.template;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.template.Template;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;
import static steps.productCatalog.TemplateSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class TemplateExportTest extends Tests {
    private static Template simpleTemplate;
    private static Template simpleTemplate2;

    @BeforeAll
    public static void setUp() {
        simpleTemplate = createTemplateByName("export_template1_test_api");
        simpleTemplate2 = createTemplateByName("export_template2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких шаблонов")
    @TmsLink("1523289")
    @Test
    public void exportTemplatesTest() {
        ExportEntity e = new ExportEntity(simpleTemplate.getId(), simpleTemplate.getVersion());
        ExportEntity e2 = new ExportEntity(simpleTemplate2.getId(), simpleTemplate2.getVersion());
        exportObjectsById("templates", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт шаблона по Id")
    @TmsLink("1523310")
    @Test
    public void exportTemplateByIdTest() {
        Template Template = createTemplateByName("template_export_test_api");
        exportTemplateById(Template.getId());
    }

    @DisplayName("Экспорт шаблона по имени")
    @TmsLink("1523319")
    @Test
    public void exportTemplateByNameTest() {
        String templateName = "template_export_by_name_test_api";
        createTemplateByName(templateName);
        exportTemplateByName(templateName);
    }
}
