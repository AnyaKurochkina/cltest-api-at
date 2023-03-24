package api.cloud.productCatalog.visualTemplate;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.visualTeamplate.*;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.TemplateSteps.deleteTemplateByName;
import static steps.productCatalog.VisualTemplateSteps.*;

@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class VisualTemplateImportTest extends Tests {

    CompactTemplate compactTemplate = CompactTemplate.builder()
            .name(new Name("name"))
            .type(new Type("type", "label"))
            .status(new Status("status"))
            .build();
    FullTemplate fullTemplate = FullTemplate.builder()
            .type("type")
            .value(Arrays.asList("value", "value2"))
            .build();

    @DisplayName("Импорт шаблона визуализации")
    @TmsLink("643640")
    @Test
    public void importVisualTemplateTest() {
        String data = JsonHelper.getStringFromFile("productCatalog/itemVisualTemplate/visualTemplateImport.json");
        String importName = new JsonPath(data).get("ItemVisualisationTemplate.name");
        if (isVisualTemplateExists(importName)) {
            deleteTemplateByName(importName);
        }
        ImportObject importObject = importVisualTemplate(Configure.RESOURCE_PATH + "/json/productCatalog/itemVisualTemplate/visualTemplateImport.json");
        assertEquals(importName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isVisualTemplateExists(importName));
        deleteVisualTemplateByName(importName);
        assertFalse(isVisualTemplateExists(importName));
    }

    @DisplayName("Импорт нескольких шаблонов визуализаций")
    @TmsLink("1523536")
    @Test
    public void importVisualTemplatesTest() {
        String visualTemplateName = "multi_import_visual_template_test_api";
        if (isVisualTemplateExists(visualTemplateName)) {
            deleteVisualTemplateByName(visualTemplateName);
        }
        String visualTemplateName2 = "multi_import_visual_template2_test_api";
        if (isVisualTemplateExists(visualTemplateName2)) {
            deleteVisualTemplateByName(visualTemplateName2);
        }
        ItemVisualTemplate template = createVisualTemplate(ItemVisualTemplate.builder()
                .name(visualTemplateName)
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(ItemVisualTemplate.class);
        ItemVisualTemplate template2 = createVisualTemplate(ItemVisualTemplate.builder()
                .name(visualTemplateName2)
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(ItemVisualTemplate.class);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/itemVisualTemplate/multiImportVisualTemplate.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/itemVisualTemplate/multiImportVisualTemplate2.json";
        DataFileHelper.write(filePath, exportVisualTemplateById(template.getId()).toString());
        DataFileHelper.write(filePath2, exportVisualTemplateById(template2.getId()).toString());
        deleteVisualTemplateByName(visualTemplateName);
        deleteVisualTemplateByName(visualTemplateName2);
        importObjects("item_visual_templates", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isVisualTemplateExists(visualTemplateName), "Шаблон визуализаций не существует");
        assertTrue(isVisualTemplateExists(visualTemplateName2), "Шаблон визуализаций не существует");
        deleteVisualTemplateByName(visualTemplateName);
        deleteVisualTemplateByName(visualTemplateName2);
    }
}
