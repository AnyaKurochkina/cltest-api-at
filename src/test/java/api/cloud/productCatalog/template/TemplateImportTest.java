package api.cloud.productCatalog.template;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.template.Template;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.TemplateSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class TemplateImportTest extends Tests {

    @DisplayName("Импорт шаблона")
    @TmsLink("643608")
    @Test
    public void importTemplateTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplate.json");
        String templateName = new JsonPath(data).get("Template.name");
        if (isTemplateExists(templateName)) {
            deleteTemplateByName(templateName);
        }
        String versionArr = new JsonPath(data).get("Template.version_arr").toString();
        assertEquals("[1, 0, 0]", versionArr);
        ImportObject importObject = importTemplate(Configure.RESOURCE_PATH + "/json/productCatalog/templates/importTemplate.json");
        assertEquals(templateName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isTemplateExists(templateName));
        deleteTemplateByName(templateName);
        assertFalse(isTemplateExists(templateName));
    }

    @DisplayName("Импорт нескольких шаблонов")
    @TmsLink("1523264")
    @Test
    public void importTemplatesTest() {
        String templateName = "multi_import_template_test_api";
        if (isTemplateExists(templateName)) {
            deleteTemplateByName(templateName);
        }
        String templateName2 = "multi_import_template2_test_api";
        if (isTemplateExists(templateName2)) {
            deleteTemplateByName(templateName2);
        }
        Template template = createTemplate(Template.builder()
                .name(templateName)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(Template.class);
        Template template2 = createTemplate(Template.builder()
                .name(templateName2)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(Template.class);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/templates/multiImportTemplate.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/templates/multiImportTemplate2.json";
        DataFileHelper.write(filePath, exportTemplateById(template.getId()).toString());
        DataFileHelper.write(filePath2, exportTemplateById(template2.getId()).toString());
        deleteTemplateByName(templateName);
        deleteTemplateByName(templateName2);
        importObjects("templates", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isTemplateExists(templateName), "Шаблон не существует");
        assertTrue(isTemplateExists(templateName2), "Шаблон не существует");
        deleteTemplateByName(templateName);
        deleteTemplateByName(templateName2);
    }

    @DisplayName("Импорт шаблона c иконкой")
    @TmsLink("1086370")
    @Test
    public void importTemplateWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplateWithIcon.json");
        String name = new JsonPath(data).get("Template.name");
        if (isTemplateExists(name)) {
            deleteTemplateByName(name);
        }
        importTemplate(Configure.RESOURCE_PATH + "/json/productCatalog/templates/importTemplateWithIcon.json");
        Template template = getTemplateByName(name);
        assertFalse(template.getIconStoreId().isEmpty());
        assertFalse(template.getIconUrl().isEmpty());
        assertTrue(isTemplateExists(name), "Шаблон не существует");
        deleteTemplateByName(name);
        assertFalse(isTemplateExists(name), "Шаблон существует");
    }

    @DisplayName("Проверка не обновления неверсионных полей при импорте уже существующего шаблона")
    @TmsLink("SOUL-7458")
    @Test
    public void checkNotVersionedFieldsWhenImportedExistTemplateTest() {
        String description = "update description";
        String templateName = "check_not_versioned_fields__when_import_exist_template_test_api";
        Template template = createTemplateByName(templateName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/templates/checkNotVersionedFieldsExistTemplateImport.json";
        DataFileHelper.write(filePath, exportTemplateById(template.getId()).toString());
        partialUpdateTemplate(template.getId(), new JSONObject().put("description", description));
        importTemplate(filePath);
        DataFileHelper.delete(filePath);
        Template templateById = getTemplateById(template.getId());
        assertEquals(description, templateById.getDescription());
    }

    @DisplayName("Проверка current_version при импорте уже существующего шаблона")
    @TmsLink("SOUL-7774")
    @Test
    public void checkCurrentVersionWhenAlreadyExistTemplateImportTest() {
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/templates/checkCurrentVersion.json";
        Template template = Template.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase())
                .version("1.0.1")
                .build()
                .createObject();
        DataFileHelper.write(filePath, exportTemplateById(template.getId()).toString());
        template.deleteObject();
        Template createdTemplate = Template.builder()
                .name(template.getName())
                .version("1.0.0")
                .build()
                .createObject();
        partialUpdateTemplate(createdTemplate.getId(), new JSONObject()
                .put("priority", 6)
                .put("version", "1.1.1"));
        partialUpdateTemplate(createdTemplate.getId(), new JSONObject()
                .put("current_version", "1.1.1"));
        importTemplate(filePath);
        DataFileHelper.delete(filePath);
        Template templateById = getTemplateById(createdTemplate.getId());
        assertEquals("1.1.1", templateById.getCurrentVersion());
    }
}
