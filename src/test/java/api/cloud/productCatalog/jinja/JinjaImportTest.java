package api.cloud.productCatalog.jinja;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.Jinja2Steps.*;
import static steps.productCatalog.ProductCatalogSteps.importObjects;

@Tag("product_catalog")
@Tag("Jinja")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaImportTest extends Tests {

    @DisplayName("Импорт нескольких jinja2")
    @TmsLink("1518720")
    @Test
    public void importMultiJinjaTest() {
        String jinjaName = "import_jinja_test_api";
        if (isJinja2Exists(jinjaName)) {
            deleteJinjaByName(jinjaName);
        }
        String jinjaName2 = "import_jinja2_test_api";
        if (isJinja2Exists(jinjaName2)) {
            deleteJinjaByName(jinjaName2);
        }
        Jinja2Template jinja = createJinja(Jinja2Template.builder()
                .name(jinjaName)
                .build()
                .toJson());
        Jinja2Template jinja2 = createJinja(Jinja2Template.builder()
                .name(jinjaName2)
                .build()
                .toJson());
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/jinja2/multiJinja.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/jinja2/multiJinja2.json";
        DataFileHelper.write(filePath, exportJinjaById(jinja.getId()).toString());
        DataFileHelper.write(filePath2, exportJinjaById(jinja2.getId()).toString());
        deleteJinjaById(jinja.getId());
        deleteJinjaById(jinja2.getId());
        importObjects("jinja2_templates", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isJinja2Exists(jinjaName), "Jinja2 не существует");
        assertTrue(isJinja2Exists(jinjaName2), "Jinja2 не существует");
        deleteJinjaByName(jinjaName);
        deleteJinjaByName(jinjaName2);
    }

    @DisplayName("Проверка не обновления неверсионных полей при импорте уже существующего jinja2")
    @TmsLink("SOUL-7456")
    @Test
    public void checkNotVersionedFieldsWhenImportedExistJinjaTest() {
        String description = "update description";
        String jinjaName = "check_not_versioned_fields__when_import_exist_jinja2_test_api";
        Jinja2Template jinja = createJinja(jinjaName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/jinja2/checkNotVersionedFieldsExistJinja2Import.json";
        DataFileHelper.write(filePath, exportJinjaById(jinja.getId()).toString());
        partialUpdateJinja2(jinja.getId(), new JSONObject().put("description", description));
        importJinja2(filePath);
        DataFileHelper.delete(filePath);
        Jinja2Template jinja2ById = getJinja2ById(jinja.getId());
        assertEquals(description, jinja2ById.getDescription());
    }

    @DisplayName("Проверка current_version при импорте уже существующего jinja2")
    @TmsLink("SOUL-7770")
    @Test
    public void checkCurrentVersionWhenAlreadyExistJinjaImportTest() {
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/jinja2/checkCurrentVersion.json";
        Jinja2Template jinja2Template = createJinja(Jinja2Template.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase())
                .version("1.0.1")
                .build().toJson());
        DataFileHelper.write(filePath, exportJinjaById(jinja2Template.getId()).toString());
        deleteJinjaById(jinja2Template.getId());
        Jinja2Template createdJinja = Jinja2Template.builder()
                .name(jinja2Template.getName())
                .version("1.0.0")
                .build()
                .createObject();
        partialUpdateJinja2(createdJinja.getId(), new JSONObject()
                .put("jinja2_template", "test_api")
                .put("version", "1.1.1"));
        partialUpdateJinja2(createdJinja.getId(), new JSONObject()
                .put("current_version", "1.1.1"));
        importJinja2(filePath);
        DataFileHelper.delete(filePath);
        Jinja2Template jinja2ById = getJinja2ById(createdJinja.getId());
        assertEquals("1.1.1", jinja2ById.getCurrentVersion());
    }
}
